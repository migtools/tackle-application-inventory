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
import io.tackle.applicationinventory.entities.Application;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;

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
public class ApplicationTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/application";
    }

    @Test
    public void testComments() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application.comments[1].length()", is(1000));
    }

    @Test
    public void testTagIDs() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application[0].tags.size()", is(3));
    }

    @Test
    public void testTagIDSort() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "tags.size()")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application[0].id", is(3));
    }

    @Test
    public void testTagIDFilteredSingleParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("tags.tag", "7")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.application.size()", is(1),
                        "_embedded.application[0].id", is(1));
    }

    @Test
    public void testBusinessServiceEqualFilter() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .queryParam("businessService", "2")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application.size()", is(1),
                        "_embedded.application[0].description", is("Important service to let private customer use their home banking accounts"),
                        "_embedded.application[0].review.id", is(7));
    }

    @Test
    public void testAddBadPayload() {
        Application application = new Application();
        application.id = 1L;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(application)
                .when()
                .post(PATH)
                .then()
                .statusCode(500);
    }

    @Test
    public void testFilterByTags() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "name")
                .queryParam("tags.tag", "712")
                .queryParam("tags.tag", "7")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.application.size()", is(1),
                        "_embedded.application[0].description", is("Important service to let private customer use their home banking accounts"),
                        "_embedded.application[0].review.id", is(7));
    }

    @Test
    public void testSortByReview() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-review.deleted,-id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application.size()", is(4),
                        "_embedded.application.id", containsInRelativeOrder(6, 3, 2, 1),
                        "_embedded.application[0].review", is(emptyOrNullString()),
                        "_embedded.application[3].review.id", is(7));
    }

}
