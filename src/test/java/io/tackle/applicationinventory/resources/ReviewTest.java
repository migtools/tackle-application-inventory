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
import io.tackle.applicationinventory.entities.Review;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
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
public class ReviewTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/review";
    }

    @Test
    public void testGetOne() {
        given()
                .accept("application/hal+json")
                .pathParam("id", "8")
                .when()
                .get(PATH + "/{id}")
                .then()
                .statusCode(200)
                .body("id", is(8),
                        "workPriority", is(21),
                        "comments.length()", is(445),
                        "application.id", is(2),
                        "_links.size()", is(4));
    }

    @Test
    public void testUpdate() {
        // read an already available review
        Review review = given()
                .accept("application/json")
                .pathParam("id", "7")
                .when()
                .get(PATH + "/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Review.class);
        review.effortEstimate = "foo";
        review.workPriority = 1000;

        // update an already available review
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", "7")
                .body(review)
                .when()
                .put(PATH + "/{id}")
                .then()
                .statusCode(204);

        // check the update is "readable"
        given()
                .accept("application/json")
                .pathParam("id", "7")
                .when()
                .get(PATH + "/{id}")
                .then()
                .statusCode(200)
                .body("id", is(7),
                        "workPriority", is(1000),
                        "effortEstimate", is("foo"),
                        "proposedAction", is("Retire"),
                        "comments.length()", is(445),
                        "application.id", is(1));

        // restore the previous status for the review
        review.effortEstimate = "Large";
        review.workPriority = 100;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", "7")
                .body(review)
                .when()
                .put(PATH + "/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteApplicationDeletesAlsoTheReview() {
        // add application
        Application application = new Application();
        application.name = "application";
        application.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(application)
                        .when()
                        .post("/application")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // add review for the application
        Review review = new Review();
        review.workPriority = 8;
        review.application = application;
        review.effortEstimate = "Mission Impossible";
        review.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(review)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // check the application has the review's ID
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review.id", is(review.id.intValue()));
        // delete the application
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);
        // check the review has been deleted as well
        given()
                .accept(ContentType.JSON)
                .pathParam("id", review.id)
                .when()
                .get(PATH + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteReviewForApplicationAndCreateAnother() {
        // add application
        Application application = new Application();
        application.name = "application";
        application.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(application)
                        .when()
                        .post("/application")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // add review for the application
        Review review = new Review();
        review.workPriority = 8;
        review.application = application;
        review.effortEstimate = "Mission Impossible";
        review.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(review)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // check the application has the review's ID
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review.id", is(review.id.intValue()));

        // delete the review
        given()
                .accept(ContentType.JSON)
                .pathParam("id", review.id)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);
        // check the application hasn't the review's ID anymore
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review", is(emptyOrNullString()));
        // add another review to the application
        Review anotherReview = new Review();
        anotherReview.workPriority = 1;
        anotherReview.application = application;
        anotherReview.effortEstimate = "Easy";
        anotherReview.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(anotherReview)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // check the application has the latest review's ID
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review.id", is(anotherReview.id.intValue()));

        // delete the review again
        // to check the unique index in the review table on application_id is working properly
        given()
                .accept(ContentType.JSON)
                .pathParam("id", anotherReview.id)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);
        // check the application hasn't the review's ID anymore
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review", is(emptyOrNullString()));
        // add another review to the application
        Review yetAnotherReview = new Review();
        yetAnotherReview.workPriority = 100;
        yetAnotherReview.application = application;
        yetAnotherReview.effortEstimate = "Interesting";
        yetAnotherReview.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(yetAnotherReview)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        // check the application has the latest review's ID
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .get("/application/{id}")
                .then()
                .statusCode(200)
                .body("review.id", is(yetAnotherReview.id.intValue()));

        // delete the application to not alter the following tests' results
        // this will delete also the review as tested
        // in the above testDeleteApplicationDeletesAlsoTheReview test
        given()
                .accept(ContentType.JSON)
                .pathParam("id", application.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteNonExistingReview(){
        given()
                .pathParam("id", "0")
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddReviewToAnAlreadyReviewedApplication(){
        Review review = new Review();
        review.proposedAction = "Already reviewed";
        review.businessCriticality = 42;
        Application application = new Application();
        application.id = 1L;
        review.application = application;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(review)
                .when()
                .post(PATH)
                .then()
                .statusCode(409);
    }
}
