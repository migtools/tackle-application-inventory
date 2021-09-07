package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Test done to be sure the `/q/*` endpoints (e.g. metrics, health, etc)
 * are not secured and so do not require any authentication/authorization
 */
@QuarkusTest
// starting Keycloak to be sure it doesn't block request
@QuarkusTestResource(value = KeycloakTestResource.class,
        initArgs = {
                @ResourceArg(name = KeycloakTestResource.IMPORT_REALM_JSON_PATH, value = "keycloak/quarkus-realm.json"),
                @ResourceArg(name = KeycloakTestResource.REALM_NAME, value = "quarkus")
        }
)
public class QEndpointsTest extends SecuredResourceTest {

    @Test
    public void testQEndpoints() {
        given().auth().oauth2("")
                .accept("application/json")
                .when().get("/q/health/")
                .then()
                .statusCode(200);
    }
}
