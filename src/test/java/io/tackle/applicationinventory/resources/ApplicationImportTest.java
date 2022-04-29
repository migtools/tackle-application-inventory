/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
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

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
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
public class ApplicationImportTest extends SecuredResourceTest {

    private static StubMapping tagStubMapping;
    private static StubMapping businessServiceStubMapping;

    @BeforeAll
    public static void init() {
        PATH = "/application-import";
        tagStubMapping = WireMock.stubFor(get(urlPathEqualTo("/controls/tag"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "[\n" +
                                        "      {\n" +
                                        "        \"id\": 1,\n" +
                                        "        \"name\": \"tag1\",\n" +
                                        "        \"tagType\": {\n" +
                                        "          \"id\": 1,\n" +
                                        "          \"name\": \"tag type 1\"\n" +
                                        "        }\n" +
                                        "      }]")));


        businessServiceStubMapping = WireMock.stubFor(get(urlPathEqualTo("/controls/business-service"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(                                "[\n" +
                                "      {\n" +
                                "        \"id\": 1,\n" +
                                "        \"name\": \"BS 2\"\n" +
                                "      }," +
                                "      {\n" +
                                "        \"id\": 2,\n" +
                                "        \"name\": \"BS 1\"\n" +
                                "      }," +
                                "      {\n" +
                                "        \"id\": 3,\n" +
                                "        \"name\": \"BS 3\"\n" +
                                "      }]")));
    }

    @AfterAll
    public static void tearDown() {
        WireMock.removeStub(tagStubMapping);
        WireMock.removeStub(businessServiceStubMapping);
    }

    @Test
    public void testFilterByIsValid()  {

        createTestData();
        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.FALSE)
                .queryParam("filename","File1")
                .queryParam("sort","-id")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(2))
                .body("_embedded.'application-import'[0].'Business Service'", is("BS 2"))
                .body("_embedded.'application-import'[0].'Tag Type 1'", is("tag type 1"));

        given()
                .accept("application/json")
                .queryParam("isValid", Boolean.FALSE)
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", is(3));

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

    protected void createTestData()
    {
        // import 2 applications
        final String multipartPayload = "Record Type 1,Application Name,Description,Comments,Business Service,Tag Type 1,Tag 1\n" +
                "1,,,,BS 1,,\n" +
                "1,,,,BS 2,tag type 1,tag1";
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


        // import 1 application
        final String multipartPayload2 = "Record Type 1,Application Name,Description,Comments,Business Service,Tag Type 1,Tag 1\n" +
                "1,,,,BS 3,,";
        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", multipartPayload2)
                .multiPart("fileName", "File2")
                .when()
                .post("/file/upload")
                .then()
                .statusCode(200);
    }
}
