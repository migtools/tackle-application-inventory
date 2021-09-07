package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import io.tackle.applicationinventory.entities.Tag;
import io.tackle.applicationinventory.entities.TagType;
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
public class TagTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/tag";
    }

    @Test
    public void testTagListByTagTypeHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "name")
                .queryParam("tagType.id", "27")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.tag.size()", is(7));
    }

    @Test
    // https://github.com/konveyor/tackle-controls/issues/101
    public void testCreateWithoutTagType() {
        Tag tag = new Tag();
        tag.name = "test";
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag)
                .when()
                .post(PATH)
                .then()
                // this will expect as well a '409' from Quarkus 1.13+ with the introduction of RestDataPanacheException
                .statusCode(409);
    }

    @Test
    // https://github.com/konveyor/tackle-controls/issues/114
    public void testSameNameWithDifferentTagTypes() {
        final String tagName = "test unique name";
        // create a tag associated with a tag type
        final Tag tag = new Tag();
        tag.name = tagName;
        final TagType tagType = new TagType();
        tagType.id = 24L;
        tag.tagType = tagType;
        tag.id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(tag)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        // create a another tag with the same name
        // BUT associated with a different tag type
        final Tag anotherTag = new Tag();
        anotherTag.name = tagName;
        final TagType anotherTagType = new TagType();
        anotherTagType.id = 23L;
        anotherTag.tagType = anotherTagType;
        anotherTag.id = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(anotherTag)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        // delete both tags to not alter other tests execution
        given()
                .pathParam("id", tag.id)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", anotherTag.id)
                .when()
                .delete(PATH + "/{id}")
                .then()
                .statusCode(204);
    }
}
