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
package io.tackle.applicationinventory.flyway;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(FlywayMigrationProfile.class)
public class FlywayMigrationTest {

    @Inject
    Flyway flyway;

    @Test
    public void testMigration() {
        // check the number of migrations applied equals the number of files in resources/db/migration folder
        assertEquals(19, flyway.info().applied().length);
        // check the current migration version is the one from the last file in resources/db/migration folder
        assertEquals("20211008.1", flyway.info().current().getVersion().toString());
        // just a basic test to double check the application started
        // to prove the flyway scripts ran successfully during startup
        given()
            .accept("application/json")
            .when().get("/q/health/")
            .then()
            .statusCode(200);
    }
}
