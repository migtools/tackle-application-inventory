package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
public class ApplicationTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/application";
    }

    @Test
    public void testComments() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application.comments[1].length()", is(1000));
    }

    @Test
    public void testTagIDs() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application[0].tags.size()", is(3));
    }



    @Test
    public void testTagIDSort() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "tags.size")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.application[0].id", is(3));
    }

    @Test
    public void testTagIDFilteredSingleParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("tags.id", "7")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.application.size()", is(1),
                        "_embedded.application[0].id", is(1));
    }
}
