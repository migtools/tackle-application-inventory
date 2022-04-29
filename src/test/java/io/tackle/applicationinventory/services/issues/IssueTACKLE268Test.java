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
package io.tackle.applicationinventory.services.issues;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.services.WireMockControlsServices;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
@QuarkusTestResource(WireMockControlsServices.class)
// https://issues.redhat.com/browse/TACKLE-268
public class IssueTACKLE268Test extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/file/upload";
    }

    @Test
    protected void testImportServiceLongCSVColumnValues() {
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("long_characters_columns.csv").getFile());

        given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", importFile)
                .multiPart("fileName", "long_characters_columns.csv")
                .when().post(PATH)
                .then()
                .statusCode(200);

        final long importSummaryId = Long.parseLong(given()
                .accept("application/hal+json")
                .when()
                .get("/import-summary")
                .then()
                .statusCode(200)
                .body("_embedded.import-summary.size()", is(1),
                        "_embedded.import-summary[0].importStatus", is("Completed"),
                        "_embedded.import-summary[0].validCount", is(1),
                        "_embedded.import-summary[0].invalidCount", is(2))
                .extract().path("_embedded.import-summary[0].id").toString());

        final String csv = given()
                .accept("text/csv")
                .queryParam("importSummaryId", importSummaryId)
                .when()
                .get("/csv-export")
                .body()
                .print();
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
                .body("size()", is(1))
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

        // Remove the import summary record (on cascade also the ApplicationImport entities will be deleted)
        given()
                .accept(ContentType.JSON)
                .pathParam("id", importSummaryId)
                .when()
                .delete("/import-summary/{id}")
                .then()
                .statusCode(204);
    }
}
