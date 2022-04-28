/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = PostgreSQLDatabaseTestResource.class,
        initArgs = {
                @ResourceArg(name = PostgreSQLDatabaseTestResource.DB_NAME, value = "application_inventory_db"),
                @ResourceArg(name = PostgreSQLDatabaseTestResource.USER, value = "application_inventory"),
                @ResourceArg(name = PostgreSQLDatabaseTestResource.PASSWORD, value = "application_inventory")
        }
)
@QuarkusTestResource(value = KeycloakTestResource.class,
        initArgs = {
                @ResourceArg(name = KeycloakTestResource.IMPORT_REALM_JSON_PATH, value = "keycloak/quarkus-realm.json"),
                @ResourceArg(name = KeycloakTestResource.REALM_NAME, value = "quarkus")
        }
)
// different approach to tests using an ordered set of test methods
// due to the fact the dependencies tests need quite a lot of data
// that depends on each other and to test cycles a very specific situation
// must be created in a determinist way.
// The test order numbering approach is kind of BASIC C64 line numbering approach:
// incrementing by 10 between tests to let some room for further later tests additions
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationsDependencyTest extends SecuredResourceTest {

    private static final Application TEST_APPLICATION = new Application();
    // 1000 applications has been reported as a kind of worst case scenario
    // so it's worth testing the behavior in such condition
    // => check 'applications-dependencies-test' in .github/workflows/pull_request.yml
    private static int numberOfApplications;

    @BeforeAll
    public static void beforeAll() {
        PATH = "/applications-dependency";
        TEST_APPLICATION.id = 6L; // from 'V20210326__insert_application.sql'
        numberOfApplications = Integer.parseInt(System.getProperty("dependencies.test.applications", "10"));
    }

    @Test
    @Order(0)
    public void testCreateApplications() {
        IntStream.range(0, numberOfApplications).forEach(i -> {
            Application application = new Application();
            application.name = String.format("Application Test %d", i);
            application.description = String.format("Application Test description %d", i);
            application.comments = String.format("Application Test comments %d", i);
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(application)
                    .when()
                    .post("/application")
                    .then()
                    .statusCode(201);
        });
    }

    @Test
    @Order(10)
    public void testCreateApplicationsDependencies() {
        // retrieve the id of the first application added in the previous method
        long firstApplicationId = Long.parseLong(given()
                .queryParam("name", "test 0")
                .accept(ContentType.JSON)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .extract()
                .path("[0].id")
                .toString());

        IntStream.range(0, numberOfApplications).forEach(i -> {
            final Application application = new Application();
            application.id = (long) i + firstApplicationId;
            ApplicationsDependency dependency = new ApplicationsDependency();
            // even iterations create southbound dependencies for TEST_APPLICATION
            if ((i % 2) == 0) {
                dependency.from = TEST_APPLICATION;
                dependency.to = application;
            }
            // odd iterations create northbound dependencies for TEST_APPLICATION
            else {
                dependency.from = application;
                dependency.to = TEST_APPLICATION;
            }
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(dependency)
                    .when()
                    .post(PATH)
                    .then()
                    .statusCode(201);
        });

        // test the cycle detector creating a cycle
        // considering with already have the dependencies:
        // * 'Home Banking BU' => 'Online Investments service' => 'Credit Cards BS'
        //    (from 'V20210325.2__insert_applications-dependency.sql')
        // * 'Application Test 1' => 'Test Application' => 'Application Test 0'
        //    (from the above dependencies creation loop)
        // Now we create two further dependencies:
        // 1. from southbound dependency 'Application Test 0' to 'Home Banking BU'
        // 2. from 'Credit Cards BS' to northbound dependency 'Application Test 1'
        // so that the loop with 6 applications should be detected
        // that tries to connect 2 previously disconnected graphs
        Application from = new Application();
        Application to = new Application();

        ApplicationsDependency firstDependency = new ApplicationsDependency();
        from.id = firstApplicationId; // 'Application Test 0'
        to.id = 1L; // 'Home Banking BU' from 'V20210305.2__insert_application.sql'
        firstDependency.from = from;
        firstDependency.to = to;
        firstDependency.id = Long.parseLong(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(firstDependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
        .extract()
        .path("id")
        .toString());

        ApplicationsDependency cycle = new ApplicationsDependency();
        from.id = 3L; // 'Credit Cards BS' from 'V20210305.2__insert_application.sql'
        to.id = firstApplicationId + 1L; // 'Application Test 1'
        cycle.from = from;
        cycle.to = to;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(cycle)
                .when()
                .post(PATH)
                .then()
                .statusCode(409)
                .body("errorMessage", is("Dependencies cycle created from applications 'Credit Cards BS', 'Application Test 1', 'Test Application', 'Application Test 0', 'Home Banking BU', 'Online Investments service'"));

        // now that the cycle detection has been tested successfully
        // it's fine to remove the 'firstDependency'
        given()
                .pathParam("id", firstDependency.id)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);
    }
    
    @Test
    @Order(15)
    public void testRetrieveNorthboundDependencies() {
        given()
                .accept("application/hal+json")
                .queryParam("to.id", TEST_APPLICATION.id)
                .queryParam("size", numberOfApplications)
                .queryParam("sort", "id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.applications-dependency.size()", is(numberOfApplications / 2));
    }

    @Test
    @Order(16)
    public void testRetrieveSouthboundDependencies() {
        given()
                .accept("application/hal+json")
                .queryParam("from.id", TEST_APPLICATION.id)
                .queryParam("size", numberOfApplications)
                .queryParam("sort", "id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.applications-dependency.size()", is(numberOfApplications / 2));
    }

    @Test
    @Order(20)
    public void testCreateApplicationsDependenciesWithWrongValues() {
        ApplicationsDependency dependency = new ApplicationsDependency();
        Application frontend = new Application();
        frontend.id = 2L;
        dependency.from = frontend;
        Application db = new Application();
        db.id = 1L;
        dependency.to = db;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(409)
                .body("errorMessage", is("Dependencies cycle created from applications 'Online Investments service', 'Home Banking BU'"));

        db.id = 2L;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(409)
                .body("errorMessage", is("'from' and 'to' values are the same: an application can not be a dependency of itself"));

        db.id = 98765L;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(404)
                .body("errorMessage", is("Not found the application with id 98765"));

        frontend.id = 12345L;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(404)
                .body("errorMessage", is("Not found the application with id 12345"));

        dependency.to = null;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(400)
                .body("errorMessage", is("Not valid application reference provided"));

        dependency.from = null;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post(PATH)
                .then()
                .statusCode(400)
                .body("errorMessage", is("Not valid application reference provided"));
    }

    @Test
    @Order(30)
    public void testDeleteApplicationsDependencies() {
        Response response = given()
                .queryParam("from.id", "6")
                .queryParam("size", numberOfApplications)
                .accept(ContentType.JSON)
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .extract()
                .response();
        List<Integer> ids = response.path("id");

        response = given()
                .queryParam("to.id", "6")
                .queryParam("size", numberOfApplications)
                .accept(ContentType.JSON)
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .extract()
                .response();
        ids.addAll(response.path("id"));
        
        assertEquals(numberOfApplications, ids.size());

        ids.forEach(id -> {
            given()
                    .pathParam("id", id)
                    .when()
                    .delete(PATH + "/{id}")
                    .then()
                    .statusCode(204);
        });
    }

    @Test
    @Order(40)
    public void testDeleteApplications() {
        long firstApplicationId = Long.parseLong(given()
                .queryParam("name", "test 0")
                .accept(ContentType.JSON)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .extract()
                .path("[0].id")
                .toString());

        IntStream.range((int) firstApplicationId, (int) firstApplicationId + numberOfApplications).forEach(i -> given()
                .pathParam("id", i)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204));
    }

    @Test
    // test not order because it can be executed whenever
    public void testUpdateNotAllowed() {
        ApplicationsDependency dependency = new ApplicationsDependency();
        dependency.id = 1L;
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", dependency.id)
                .body(dependency)
                .when()
                .put(PATH + "/{id}")
                .then()
                .statusCode(405);
    }

    @Test
    // https://github.com/konveyor/tackle-application-inventory/issues/39
    public void testDeletedDependencyCanBeAddedAgain() {
        // create a new dependency
        final ApplicationsDependency dependency = new ApplicationsDependency();
        final Application from = new Application();
        from.id = 6L;
        dependency.from = from;
        Application to = new Application();
        to.id = 1L;
        dependency.to = to;
        final Integer firstId = given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(dependency)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        // delete the dependency
        given()
                .pathParam("id", firstId)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);

        // add a dependency involving the same applications in the same order
        final Integer secondId = given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(dependency)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        // check the first ID is strictly less then the second one
        assertTrue(firstId < secondId);

        // delete the second dependency as well to not alter other tests
        given()
                .pathParam("id", secondId)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    // https://github.com/konveyor/tackle-application-inventory/issues/40
    public void testDeletingApplicationsCascadeOnDeletingTheirDependencies() {
        // create 3 applications
        Application first = new Application();
        first.name = "first";
        first.id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(first)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        Application second = new Application();
        second.name = "second";
        second.id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(second)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        Application third = new Application();
        third.name = "third";
        third.id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(third)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        // add dependencies 1st->2nd, 1st->3rd, 2nd->3rd
        ApplicationsDependency firstToSecond = new ApplicationsDependency();
        firstToSecond.from = first;
        firstToSecond.to = second;
        firstToSecond.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(firstToSecond)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString());

        ApplicationsDependency firstToThird = new ApplicationsDependency();
        firstToThird.from = first;
        firstToThird.to = third;
        firstToThird.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(firstToThird)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString());

        ApplicationsDependency secondToThird = new ApplicationsDependency();
        secondToThird.from = second;
        secondToThird.to = third;
        secondToThird.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(secondToThird)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString());

        // check there are 3 applications dependencies
        given()
                .accept("application/hal+json")
                .queryParam("from.id", first.id)
                .queryParam("from.id", second.id)
                .queryParam("sort", "id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.applications-dependency.size()", is(3));

        // delete 1st application
        given()
                .pathParam("id", first.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);

        // check there's just one applications dependency
        given()
                .accept("application/hal+json")
                .queryParam("from.id", first.id)
                .queryParam("from.id", second.id)
                .queryParam("sort", "id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.applications-dependency.size()", is(1));

        // delete 2nd application
        given()
                .pathParam("id", second.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);

        // check there are no applications dependencies left
        given()
                .accept("application/hal+json")
                .queryParam("from.id", first.id)
                .queryParam("from.id", second.id)
                .queryParam("sort", "id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.applications-dependency.size()", is(0));

        // delete 3rd application for not altering the other tests
        given()
                .pathParam("id", third.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);
    }
}
