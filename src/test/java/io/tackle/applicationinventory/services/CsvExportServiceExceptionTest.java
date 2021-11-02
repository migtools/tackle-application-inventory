package io.tackle.applicationinventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.AbstractBase1Test;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;


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
public class CsvExportServiceExceptionTest extends AbstractBase1Test {


    @Test
    public void testCsvExportExceptionTest()  {

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","duplicate_application_names.csv")
                .when().post("/file/upload")
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        Long summaryId = Long.valueOf(given()
                .accept("application/hal+json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .extract().path("_embedded.'import-summary'[0].id").toString());


        RuntimeException exception = null;
        try{
            ObjectWriter objectWriter = Mockito.mock(ObjectWriter.class);
            doThrow(JsonProcessingException.class).when(objectWriter).writeValueAsString(List.class);
            CsvExportService csvExportService = new CsvExportService();
            csvExportService.getCsvExportForImportSummaryId(summaryId);
        }
        catch(RuntimeException jpe)
        {
            exception = jpe;
        }
        catch(JsonProcessingException thrown)
        {

        }

        assert(exception.getCause().toString().contains("JsonProcessingException"));

        //Remove test data before finishing
        ImportSummary[] summaryList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/import-summary")
                        .as(ImportSummary[].class);

        Arrays.asList(summaryList).forEach(summary ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", summary.id)
                        .when()
                        .delete("/import-summary/{id}")
                        .then()
                        .statusCode(204));


        ApplicationImport[] importList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/application-import")
                        .as(ApplicationImport[].class);


        Arrays.asList(importList).forEach(thisImport ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", thisImport.id)
                        .when()
                        .delete("/application-import/{id}")
                        .then()
                        .statusCode(204));

    }
}
