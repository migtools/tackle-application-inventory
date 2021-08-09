package io.tackle.applicationimporter.issues;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Issue268Test extends SecuredResourceTest {

    @Inject
    UserTransaction userTransaction;

    @BeforeAll
    public static void init() {
        PATH = "/file/upload";
    }

    @Test
    @Order(4)
    protected void testImportServiceLongCSVColumnValues() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("long_characters_columns.csv").getFile());

        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", importFile)
                .multiPart("fileName", "long_characters_columns.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        given()
                .accept("application/hal+json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'import-summary'[0].'importStatus'", is("Completed"));

        given()
                .accept("application/json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", is(1),
                        "[0].'importStatus'", is("Completed"),
                        "[0].'validCount'", is(1),
                        "[0].'invalidCount'", is(2)
                );

        ImportSummary summary = ImportSummary.findAll().firstResult();

        Response r =
                given()
                        .accept("text/csv")
                        .when()
                        .get("/csv-export?importSummaryId=" + summary.id);

        String csv = r.body().print();
        String[] csvFields = csv.split(",");

        int numberOfRows = (int) Arrays.stream(csvFields).filter("\n"::equals).count();
        assertEquals(2, numberOfRows);

        assertEquals(1, (int) Arrays.stream(csvFields).filter("Import-app-9"::equals).count());
        assertEquals(1, Arrays.stream(csvFields).filter(f -> f.startsWith("\"Very-long-app-name-name-")).count());


        userTransaction.begin();
        ApplicationImport.deleteAll();
        ImportSummary.deleteAll();
        userTransaction.commit();
    }

}

