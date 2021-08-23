package io.tackle.applicationinventory.services.issues;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.applicationinventory.services.BusinessServiceService;
import io.tackle.applicationinventory.services.TagService;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

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

    @InjectMock
    @RestClient
    TagService mockTagService;

    @InjectMock
    @RestClient
    BusinessServiceService mockBusinessServiceService;

    @BeforeAll
    public static void init() {
        PATH = "/file/upload";
    }

    @Test
    protected void testImportServiceLongCSVColumnValues() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        Mockito.when(mockTagService.getListOfTags(0, 1000)).thenReturn(Collections.emptySet());
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices(0, 1000)).thenReturn(Collections.emptySet());

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

        // Clean test data to not alter other tests execution
        // Remove the successfully imported 'Import-app-8' application
        final long importApp8Id = Long.parseLong(given()
                .queryParam("name", "Import-app-8")
                .accept(ContentType.JSON)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .body("size()", Is.is(1))
                .extract()
                .path("[0].id")
                .toString());

        given()
                .accept(ContentType.JSON)
                .pathParam("id", importApp8Id)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);

        userTransaction.begin();
        ApplicationImport.deleteAll();
        ImportSummary.deleteAll();
        userTransaction.commit();
    }

}

