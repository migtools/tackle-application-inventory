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
        assertEquals(8, flyway.info().applied().length);
        // check the current migration version is the one from the last file in resources/db/migration folder
        assertEquals("20210602.1", flyway.info().current().getVersion().toString());
        // just a basic test to double check the application started
        // to prove the flyway scripts ran successfully during startup
        given()
            .accept("application/json")
            .when().get("/q/health/")
            .then()
            .statusCode(200);
    }
}
