package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import io.tackle.applicationinventory.entities.StakeholderGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

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
public class StakeholderGroupTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/stakeholder-group";
    }

    @Test
    public void testStakeholderGroupListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("sort", "-stakeholders.size()")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(3),
                        "id", containsInRelativeOrder(61, 62, 63),
                        "name", containsInRelativeOrder("Managers", "Engineers", "Marketing"),
                        "createUser", containsInRelativeOrder("<pre-filled>", "<pre-filled>", "<pre-filled>"),
                        "[0].stakeholders.size()", is(2)
                );
    }

    @Test
    public void testStakeholderGroupFilteredMultipleParamsListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("name", "gers")
                .queryParam("description", "Man")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(61),
                        "name", contains("Managers"),
                        "createUser", contains("<pre-filled>"),
                        "updateUser", contains("<pre-filled>")
                );
    }



    @Test
    public void testStakeholderGroupFilteredMultipleParamsWithMultipleValuesListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("name", "gers")
                .queryParam("name", "edit")
                .queryParam("description", "Man")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(61),
                        "name", contains("Managers"),
                        "[0].stakeholders.size()", is(2),
                        "[0].stakeholders.displayName", containsInAnyOrder("Jessica Fletcher", "Emmett Brown"),
                        "createUser", contains("<pre-filled>"),
                        "updateUser", contains("<pre-filled>")
                );

        given()
                .accept("application/json")
                .queryParam("sort", "-id")
                .queryParam("description", "up")
                .queryParam("stakeholders.displayName", "met")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(2),
                        "id", containsInRelativeOrder(62, 61),
                        "name", containsInRelativeOrder("Engineers", "Managers"),
                        "createUser", containsInRelativeOrder("<pre-filled>", "<pre-filled>"),
                        "[0].stakeholders[0].displayName", is("Emmett Brown"),
                        "[1].stakeholders.displayName", containsInAnyOrder("Emmett Brown", "Jessica Fletcher")
                );
    }

    @Test
    @DisabledOnNativeImage
    public void testStakeholderGroupCreateUpdateAndDeleteEndpoint() {
        testStakeholderGroupCreateUpdateAndDeleteEndpoint(false);
    }

    protected void testStakeholderGroupCreateUpdateAndDeleteEndpoint(boolean nativeExecution) {
        final String name = "Another StakeholderGroup name";
        final String description = "Another StakeholderGroup description";
        StakeholderGroup stakeholderGroup = new StakeholderGroup();
        stakeholderGroup.name = name;
        stakeholderGroup.description = description;

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(stakeholderGroup)
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(201).extract().response();

        assertEquals(name, response.path("name"));
        assertEquals(description, response.path("description"));
        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

        Long stakeholderGroupId = Long.valueOf(response.path("id").toString());

        final String newName = "Yet another different name";
        stakeholderGroup.name = newName;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(stakeholderGroup)
                .pathParam("id", stakeholderGroupId)
                .when().put(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", stakeholderGroupId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(newName),
                        "description", is(description));

        if (!nativeExecution) {
            StakeholderGroup updatedStakeholderGroupFromDb = StakeholderGroup.findById(stakeholderGroupId);
            assertEquals(newName, updatedStakeholderGroupFromDb.name);
            assertEquals(description, updatedStakeholderGroupFromDb.description);
            assertNotNull(updatedStakeholderGroupFromDb.createTime);
            assertNotNull(updatedStakeholderGroupFromDb.updateTime);
        }

        given()
                .pathParam("id", stakeholderGroupId)
                .when().delete(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", stakeholderGroupId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(404);

    }

    @Test
    public void testStakeholderGroupFilteredSingleParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("name", "Marketing")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.stakeholder-group.size()", is(1),
                        "_embedded.stakeholder-group.id", contains(63),
                        "_embedded.stakeholder-group.name", contains("Marketing"),
                        "_embedded.stakeholder-group[0]._links.size()", is(5),
                        "_embedded.stakeholder-group[0]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder-group/63"),
                        "_links.size()", is(4));
    }

    @Test
    public void testStakeholderGroupFilteredWrongParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("wrong", "wrongAsWell")
                .when().get(PATH)
                .then()
                .statusCode(400);

        given()
                .accept("application/hal+json")
                .queryParam("stakeholders.wrong", "wrongAsWell")
                .when().get(PATH)
                .then()
                .statusCode(400);
    }



    @Test
    public void testStakeholderGroupPaginationWithPrevAndNextLinks() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "name")
                .queryParam("description", "Group")
                .queryParam("size", "1")
                .queryParam("page", "1")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.stakeholder-group.size()", is(1),
                        "_embedded.stakeholder-group.id", containsInRelativeOrder( 61),
                        "_embedded.stakeholder-group.name", containsInRelativeOrder("Managers"),
                        "_embedded.stakeholder-group[0]._links.size()", is(5),
                        "_embedded.stakeholder-group[0]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder-group/61"),
                        "_links.size()", is(6));
    }

    @Test
    public void testStakeholderGroupListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.stakeholder-group.size()", is(3),
                        "_embedded.stakeholder-group.id", containsInRelativeOrder(63, 62, 61),
                        "_embedded.stakeholder-group.name", containsInRelativeOrder("Marketing", "Engineers", "Managers"),
                        "_embedded.stakeholder-group[1]._links.size()", is(5),
                        "_embedded.stakeholder-group[1]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder-group/62"),
                        "_links.size()", is(4));
    }

    @Test
    // https://github.com/konveyor/tackle-controls/issues/103
    public void testFilteringTheSameStakeholderGroupByMultipleMembers() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-name")
                .queryParam("stakeholders.displayName", "ssi")
                .queryParam("stakeholders.displayName", "mme")
                .when()
                .get(PATH)
                .then()
                .log().body()
                .statusCode(200)
                .body("_embedded.stakeholder-group.size()", is(2),
                        "_embedded.stakeholder-group.id", containsInRelativeOrder( 61, 62),
                        "_embedded.stakeholder-group[0].stakeholders.size()", is(2),
                        "_embedded.stakeholder-group[0]._links.size()", is(5),
                        "_embedded.stakeholder-group[0]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder-group/61"),
                        "_links.size()", is(4));
    }
}
