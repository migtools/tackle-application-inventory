package io.tackle.applicationinventory.services;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.MultipartImportBody;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInRelativeOrder;
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
@QuarkusTestResource(WireMockControlsServices.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportServiceTest extends SecuredResourceTest {


    @BeforeAll
    public static void init() {

        PATH = "/file/upload";
    }


    @Test
    @Order(1)
    protected void testImportServicePost() {


        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());



        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(1));



        Response response2 = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response2.getStatusCode());


        final String successfulimportSummaryApplicationName = String.valueOf(given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(1))
        .extract().path("_embedded.'application-import'[0].'Application Name'").toString());

        final Long successfulimportApplicationId = Long.valueOf(given()
                .accept("application/hal+json")
                .queryParam("name", successfulimportSummaryApplicationName)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.application.size()", is(1))
                .extract().path("_embedded.application[0].id").toString());



        given()
                .accept(ContentType.JSON)
                .pathParam("id", successfulimportApplicationId)
                .when()
                .delete("/application/{id}")
                .then()
                .statusCode(204);

        removeTestObjects(Collections.emptyList());


    }

    @Test
    @Order(2)
    protected void testMapToApplicationRejected()  {


        createDummyRejectedImports();




        given()
                .accept("application/hal+json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .body("_embedded.import-summary.size()", is(1),
                "_embedded.import-summary.invalidCount", containsInRelativeOrder(6),
                        "total_count", is(1));



        removeTestObjects(Collections.emptyList());

    }

    protected void createDummyRejectedImports()
    {

        // import 2 applications
        final String multipartPayload = "Record Type 1,Application Name,Description,Comments,Business Service,Tag Type 1,Tag 1,Tag Type 2,Tag 2,Tag Type 3,Tag 3" +
        ",Tag Type 4,Tag 4,Tag Type 5,Tag 5,Tag Type 6,Tag 6,Tag Type 7,Tag 7,Tag Type 8,Tag 8,Tag Type 9,Tag 9" +
        ",Tag Type 10,Tag 10,Tag Type 11,Tag 11,Tag Type 12,Tag 12,Tag Type 13,Tag 13,Tag Type 14,Tag 14,Tag Type 15,Tag 15,Tag Type 16,Tag 16" +
        ",Tag Type 17,Tag 17,Tag Type 18,Tag 18,Tag Type 19,Tag 19,Tag Type 20,Tag 20,Dependency,Dependency Direction\n" +
                "1,,hello,,BS 1,,\n" +
                "1,  ,,,BS 2,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1,tag type 1,tag 1\n" +
                "1,name 1,and this,,BS 2,,,,,,mystery tag,,\n" +
                "1,name 4,and this,,BS 1,,,mystery tag type,\n" +
                "1,name 5,and this,,BS 2,,tag1\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", multipartPayload)
                .multiPart("fileName", "File1")
                .when()
                .post("/file/upload")
                .then()
                .statusCode(200);


    }

    @Test
    @Order(3)
    protected void testMultipartImport() {

        MultipartImportBody multipartImport = new MultipartImportBody();
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());
        multipartImport.setFilename("testImport");
        multipartImport.setFile(importFile.toString());

        assertEquals(multipartImport.getFileName(),"testImport");

        removeTestObjects(Collections.emptyList());

    }


    @Test
    @Order(4)
    protected void testMapToApplicationMissingFields() {

        createMissingFieldsObjects();



        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(4));


        removeTestObjects(Arrays.asList("Test App 1", "Test App 2", "Test App 3", "Test App 4"));

    }

    protected void createMissingFieldsObjects()
    {

        // import 2 applications
        final String multipartPayload = "Record Type 1,Application Name,Description,Comments,Business Service,Tag Type 1,Tag 1,Tag Type 2,Tag 2,Tag Type 3,Tag 3" +
                ",Tag Type 4,Tag 4,Tag Type 5,Tag 5,Tag Type 6,Tag 6,Tag Type 7,Tag 7,Tag Type 8,Tag 8,Tag Type 9,Tag 9" +
                ",Tag Type 10,Tag 10,Tag Type 11,Tag 11,Tag Type 12,Tag 12,Tag Type 13,Tag 13,Tag Type 14,Tag 14,Tag Type 15,Tag 15,Tag Type 16,Tag 16" +
                ",Tag Type 17,Tag 17,Tag Type 18,Tag 18,Tag Type 19,Tag 19,Tag Type 20,Tag 20\n" +
                "1,Test App 1\n" +
                "1,Test App 2,,,,,,,,,,,\n" +
                "1,Test App 3,,,BS 1,,,,\n" +
                "1,Test App 4,,,BS 1,,";
        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", multipartPayload)
                .multiPart("fileName", "File1")
                .when()
                .post("/file/upload")
                .then()
                .statusCode(200);

    }

    @Test
    @Order(5)
    protected void testImportServiceNoMatchingTag() {


        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());


        removeTestObjects(Collections.singletonList("OrderHub"));
    }

    @Test
    @Order(6)
    protected void testImportServiceDuplicatesInFile() {


        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","duplicate_application_names.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.FALSE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'[0].'errorMessage'", is("Duplicate Application Name within file: Order Hub"));

        given()
                .accept("application/hal+json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'import-summary'[0].'importStatus'", is("Completed"));

        Long summaryId = Long.valueOf(given()
                .accept("application/json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", is(1))
                .extract().path("[0].id").toString());

        Response r =
                given()
                .accept("text/csv")
                .when()
                .get("/csv-export?importSummaryId=" + summaryId);



        String csv = r.body().print();
        String[] csvFields = csv.split(",");
        List<String> found = Arrays.stream(csvFields).filter("Comments"::equals).collect(Collectors.toList());
        assertEquals(1,found.size());



        removeTestObjects(Collections.emptyList());

    }

    @Test
    @Order(7)
    protected void testImportServiceCaseInsensitiveColumnHeaders() {
        
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("mixed_case_column_headers.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","mixed_case_column_headers.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());


        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(1));



        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.FALSE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(6));

        final String successfulimportSummaryApplicationName = String.valueOf(given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(1))
                .extract().path("_embedded.'application-import'[0].'Application Name'").toString());

        final String successfulimportApplicationName = String.valueOf(given()
                .accept("application/hal+json")
                .queryParam("name", successfulimportSummaryApplicationName)
                .when()
                .get("/application")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.application.size()", is(1))
                .extract().path("_embedded.application[0].name").toString());

        removeTestObjects(Arrays.asList(successfulimportApplicationName));

    }

    @Test
    @Order(8)
    protected void testImportServiceNoTagsRetrieved() {

        final StubMapping tagStubMapping = WireMock.stubFor(get(urlPathEqualTo("/controls/tag"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","duplicate_application_names.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        given()
                .accept("application/json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].'errorMessage'", is("Unable to retrieve TagTypes from remote resource"));

        removeTestObjects(Collections.emptyList());
        WireMock.removeStub(tagStubMapping);
      
    }

    @Test
    @Order(9)
    protected void testImportServiceNoBSRetrieved() {

        final StubMapping tagStubMapping = WireMock.stubFor(get(urlPathEqualTo("/controls/tag"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "[\n" +
                                        "      {\n" +
                                        "        \"id\": 1,\n" +
                                        "        \"name\": \"RHEL 8\",\n" +
                                        "        \"tagType\": {\n" +
                                        "          \"id\": 1,\n" +
                                        "          \"name\": \"Operating System\"\n" +
                                        "        }\n" +
                                        "      }]")));

        final StubMapping businessServiceStubMapping = WireMock.stubFor(get(urlPathEqualTo("/controls/business-service"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());

        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","duplicate_application_names.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        given()
                .accept("application/json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .log().body()
                .body("[0].'errorMessage'", is("Unable to retrieve BusinessServices from remote resource"));

        removeTestObjects(Collections.emptyList());
        WireMock.removeStub(tagStubMapping);
        WireMock.removeStub(businessServiceStubMapping);
    }



    @Test
    @Order(10)
    protected void testImportDependencies() {
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("dependency_import.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","dependency_import.csv")
                .when().post(PATH)
                .then()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());


        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .body("_embedded.'application-import'.size()", is(5));

        File importFile2 = new File(classLoader.getResource("only_dependencies_imported.csv").getFile());


        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile2)
                .multiPart("fileName","only_dependencies_imported.csv")
                .when().post(PATH)
                .then()
                .statusCode(200).extract().response();


        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .body("_embedded.'application-import'.size()", is(5));


        removeTestObjects(Arrays.asList("OrderHub","OrderHubDependency","OrderHubDependency2"));

    }



    private void removeTestObjects(List<String> appNamesToDelete)
    {
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

        for (String appName : appNamesToDelete) {
            long firstApplicationId = Long.parseLong(given()
                    .queryParam("name", appName)
                    .accept(ContentType.JSON)
                    .when()
                    .get("/application")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("[0].id")
                    .toString());



                    given()
                            .accept(ContentType.JSON)
                            .pathParam("id", firstApplicationId)
                            .when()
                            .delete("/application/{id}")
                            .then()
                            .statusCode(204);

        }

    }


}

