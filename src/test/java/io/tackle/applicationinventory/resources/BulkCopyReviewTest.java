package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.dto.BulkReviewDto;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
public class BulkCopyReviewTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/review/bulk";
    }

    public void deleteApplication(Application... applications) {
        // Clean data
        Arrays.asList(applications)
                .forEach(app -> {
                    given()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .when()
                            .delete("/application/" + app.id)
                            .then()
                            .statusCode(204);
                });
    }

    @Test
    public void createBulkCopyWithNonExistingSource() {
        BulkReviewDto bulkCopy = new BulkReviewDto();
        bulkCopy.setSourceReview(Long.MAX_VALUE); // Non existing source
        bulkCopy.setTargetApplications(Arrays.asList(1L, 2L, 3L)); // Whether or not the app's id exists is irrelevant since the Source is invalid.

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bulkCopy)
                .when()
                .post(PATH)
                .then()
                .statusCode(400);
    }

    @Test
    public void createBulkCopyWithNonExistingTargetApps() {
        // Create app1
        Application app1 = new Application();
        app1.name = "app1";

        app1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(app1)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract().body().as(Application.class);

        // Create review (source) for app1
        Review review1 = new Review();
        review1.workPriority = 1;
        review1.businessCriticality = 1;
        review1.effortEstimate = "low";
        review1.proposedAction = "rehost";
        review1.application = app1;

        review1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(review1)
                .when()
                .post("/review")
                .then()
                .statusCode(201)
                .extract().body().as(Review.class);

        // Create bulk copy using valid source but non existing targets
        BulkReviewDto bulkCopy = new BulkReviewDto();
        bulkCopy.setSourceReview(review1.id);
        bulkCopy.setTargetApplications(Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)); // Non existing apps (target)

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bulkCopy)
                .when()
                .post("/review/bulk")
                .then()
                .statusCode(400);

        // Clean data
        deleteApplication(app1);
    }

    @Test
    public void createBulkCopy() {
        // Case: create app1, app2, app3, and app4. App1 and app2 will have a review manually created.
        // TestPhase1: Copy review from app1 to app3 and app4
        // TestPhase2: Copy (override) review from app2 to app3 and app4

        // Create app1, app2, app3, and app4
        Application app1 = new Application();
        app1.name = "app1";

        Application app2 = new Application();
        app2.name = "app2";

        Application app3 = new Application();
        app3.name = "app3";

        Application app4 = new Application();
        app4.name = "app4";

        app1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(app1)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract().body().as(Application.class);

        app2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(app2)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract().body().as(Application.class);

        app3 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(app3)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract().body().as(Application.class);

        app4 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(app4)
                .when()
                .post("/application")
                .then()
                .statusCode(201)
                .extract().body().as(Application.class);

        // Create reviews for app1 and app2
        Review review1 = new Review();
        review1.workPriority = 1;
        review1.businessCriticality = 1;
        review1.effortEstimate = "low";
        review1.proposedAction = "rehost";
        review1.application = app1;

        Review review2 = new Review();
        review2.workPriority = 2;
        review2.businessCriticality = 2;
        review2.effortEstimate = "high";
        review2.proposedAction = "replatform";
        review2.application = app2;

        review1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(review1)
                .when()
                .post("/review")
                .then()
                .statusCode(201)
                .extract().body().as(Review.class);

        review2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(review2)
                .when()
                .post("/review")
                .then()
                .statusCode(201)
                .extract().body().as(Review.class);

        // TestPhase1: Copy review from app1 to app3 and app4
        BulkReviewDto bulkCopy = new BulkReviewDto();
        bulkCopy.setSourceReview(review1.id);
        bulkCopy.setTargetApplications(Arrays.asList(app3.id, app4.id));

        BulkReviewDto bulkCopyToWatch1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bulkCopy)
                .when()
                .post("/review/bulk")
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "completed", is(false)
                )
                .extract().body().as(BulkReviewDto.class);

        await().atMost(20, TimeUnit.SECONDS).until(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(PATH + "/" + bulkCopyToWatch1.getId())
                .then()
                .statusCode(200)
                .extract().body().as(BulkReviewDto.class)
                .isCompleted()
        );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/application/" + app3.id)
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "review", is(notNullValue()),
                        "review.workPriority", is(1),
                        "review.businessCriticality", is(1),
                        "review.effortEstimate", is("low"),
                        "review.proposedAction", is("rehost")
                );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/application/" + app4.id)
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "review", is(notNullValue()),
                        "review.workPriority", is(1),
                        "review.businessCriticality", is(1),
                        "review.effortEstimate", is("low"),
                        "review.proposedAction", is("rehost")
                );

        // TestPhase2: Copy review from app2 to app3 and app4
        bulkCopy = new BulkReviewDto();
        bulkCopy.setSourceReview(review2.id);
        bulkCopy.setTargetApplications(Arrays.asList(app3.id, app4.id));

        BulkReviewDto bulkCopyToWatch2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bulkCopy)
                .when()
                .post("/review/bulk")
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "completed", is(false)
                )
                .extract().body().as(BulkReviewDto.class);

        await().atMost(20, TimeUnit.SECONDS).until(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(PATH + "/" + bulkCopyToWatch2.getId())
                .then()
                .statusCode(200)
                .extract().body().as(BulkReviewDto.class)
                .isCompleted()
        );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/application/" + app3.id)
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "review", is(notNullValue()),
                        "review.workPriority", is(2),
                        "review.businessCriticality", is(2),
                        "review.effortEstimate", is("high"),
                        "review.proposedAction", is("replatform")
                );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/application/" + app4.id)
                .then()
                .statusCode(200)
                .body("id", is(notNullValue()),
                        "review", is(notNullValue()),
                        "review.workPriority", is(2),
                        "review.businessCriticality", is(2),
                        "review.effortEstimate", is("high"),
                        "review.proposedAction", is("replatform")
                );

        // Clean data
        deleteApplication(app1, app2, app3, app4);
    }

}
