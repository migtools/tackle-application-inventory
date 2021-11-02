package io.tackle.applicationinventory.resources.issues;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.AbstractBase1Test;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.applicationinventory.services.BusinessServiceService;
import io.tackle.applicationinventory.services.TagService;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.core.Is.is;

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
// https://issues.redhat.com/browse/TACKLE-282
public class IssueTACKLE282Test extends AbstractBase1Test {

    @InjectMock
    @RestClient
    TagService mockTagService;

    @InjectMock
    @RestClient
    BusinessServiceService mockBusinessServiceService;

    @Test
    public void test() {
        // setup mockito for TagType and Tag
        final Tag.TagType tagTypeDatabase = new Tag.TagType();
        tagTypeDatabase.id = "2";
        tagTypeDatabase.name = "Database";
        final Tag tagOracle = new Tag();
        tagOracle.id = "1";
        tagOracle.name = "Oracle";
        tagOracle.tagType = tagTypeDatabase;
        final Tag tagDB2 = new Tag();
        tagDB2.id = "2";
        tagDB2.name = "DB2";
        tagDB2.tagType = tagTypeDatabase;
        final Set<Tag> tags = new HashSet<>() ;
        tags.add(tagOracle);
        tags.add(tagDB2);
        Mockito.when(mockTagService.getListOfTags(0, 1000)).thenReturn(tags);

        // setup mockito for BusinessService
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices(0, 1000)).thenReturn(Collections.emptySet());

        // import 2 applications
        final String multipartPayload = "Record Type 1,Application Name,Description,Comments,Business Service,Tag Type 1,Tag 1\n" +
                "1,ForestAndTrees,Mainframe batch scheduling application,Used to test 20 Tags on the importer,,Database,Oracle\n" +
                "1,Tiller,\"'generic, interfaces, extract and reporting tool’\",Replacement for QED,,Database,DB2";
        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", multipartPayload)
                .multiPart("fileName", "TACKLE-282.csv")
                .when()
                .post("/file/upload")
                .then()
                .statusCode(200);
        final Long importSummaryId = Long.valueOf(given()
                .accept("application/hal+json")
                .queryParam("filename", "TACKLE-282.csv")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .body("_embedded.import-summary.size()", Matchers.is(1),
                        "_embedded.import-summary.invalidCount", containsInRelativeOrder(0),
                        "_embedded.import-summary.validCount", containsInRelativeOrder(2),
                        "total_count", Matchers.is(1))
                .extract().path("_embedded.import-summary[0].id").toString());
        // get the imported applications to double check everything worked fine
        final Application firstApplication = given()
                .queryParam("name", "ForestAndTrees")
                .accept(ContentType.JSON)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .extract()
                .as(Application[].class)
                [0];
        final Application secondApplication = given()
                .queryParam("name", "Tiller")
                .accept(ContentType.JSON)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .extract()
                .as(Application[].class)
                [0];
        // add a review for firstApplication
        final Review review = new Review();
        review.application = firstApplication;
        review.effortEstimate = "high";
        review.workPriority = 11;
        review.businessCriticality = 3;
        review.proposedAction = "go on holiday";
        review.id = Long.valueOf(
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(review)
                        .when()
                        .post("/review")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id")
                        .toString()
        );
        firstApplication.review = new Review();
        firstApplication.review.id = review.id;
        // add the reviewed as dependency to the other application
        ApplicationsDependency dependency = new ApplicationsDependency();
        dependency.from = firstApplication;
        dependency.to = secondApplication;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(dependency)
                .when()
                .post("/applications-dependency")
                .then()
                .statusCode(201);
        // clean the data inserted to not alter other tests
        given()
                .accept(ContentType.JSON)
                .pathParam("id", firstApplication.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);
        given()
                .accept(ContentType.JSON)
                .pathParam("id", secondApplication.id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);
        given()
                .pathParam("id", importSummaryId)
                .when()
                .delete("/import-summary/{id}")
                .then()
                .statusCode(204);
    }

}
