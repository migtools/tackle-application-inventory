package io.tackle.applicationinventory;

import io.restassured.RestAssured;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;

public class AbstractBaseTest {

    private static final String KEYCLOAK_SERVER_URL = System.getProperty("quarkus.oidc.auth-server-url", "http://localhost:8180/auth");
    protected static String PATH = "";
    protected static String ACCESS_TOKEN;

    @BeforeAll
    public static void setUp() {
        ACCESS_TOKEN = given()
                .relaxedHTTPSValidation()
                .auth().preemptive().basic("backend-service", "secret")
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", "alice")
                .formParam("password", "alice")
                .when()
                .post(KEYCLOAK_SERVER_URL + "/protocol/openid-connect/token")
                .then().extract().path("access_token").toString();
        RestAssured.authentication = oauth2(ACCESS_TOKEN);
    }

    /**
     * Maybe too much to execute it every time a class extends this one but, right now,
     * the "better safe than sorry" approach with security is the winning one.
     */
    @Test
    public void testUnauthorized(){
        given().auth().oauth2("")
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .statusCode(401);
    }

    @BeforeEach
    public void beforeEach() {
        Flyway flyway = flyway();
        flyway.clean();
        flyway.migrate();
    }

    @AfterEach
    public void afterEach() {
        Flyway flyway = flyway();
        flyway.clean();
    }

    private Flyway flyway() {
        Config config = ConfigProvider.getConfig();
        String username = config.getValue("quarkus.datasource.username", String.class);
        String password = config.getValue("quarkus.datasource.password", String.class);
        String jdbUrl = config.getValue("quarkus.datasource.jdbc.url", String.class);

        // Flyway
        final List<String> locations = new ArrayList<>();
        locations.add("db" + File.separator + "migration");
        locations.add("db" + File.separator + "test-data");

        return Flyway.configure()
                .dataSource(jdbUrl, username, password)
                .connectRetries(120)
                .locations(locations.toArray(String[]::new))
                .load();
    }
}
