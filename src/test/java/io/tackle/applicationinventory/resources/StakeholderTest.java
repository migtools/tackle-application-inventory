package io.tackle.applicationinventory.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import io.tackle.applicationinventory.entities.JobFunction;
import io.tackle.applicationinventory.entities.Stakeholder;
import io.tackle.applicationinventory.entities.StakeholderGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
public class StakeholderTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/stakeholder";
    }

    @Test
    public void testStakeholdersListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.stakeholder.size()", is(2),
                        "_embedded.stakeholder.id", containsInRelativeOrder(59, 60),
                        "_embedded.stakeholder.displayName", containsInRelativeOrder("Jessica Fletcher", "Emmett Brown"),
                        "_embedded.stakeholder[1]._links.size()", is(5),
                        "_embedded.stakeholder[1]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder/60"),
                        "_links.size()", is(4));
    }

    @Test
    public void testBusinessServicesFilteredSingleParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("displayName", "sica")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.stakeholder.size()", is(1),
                        "_embedded.stakeholder.id", contains(59),
                        "_embedded.stakeholder.displayName", contains("Jessica Fletcher"),
                        "_embedded.stakeholder[0]._links.size()", is(5),
                        "_embedded.stakeholder[0]._links.self.href", is("http://localhost:8081/application-inventory/stakeholder/59"),
                        "_links.size()", is(4));
    }

    @Test
    public void testBusinessServicesFilteredMultipleParamsListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("displayName", "met")
                .queryParam("email", "greatscott")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(60),
                        "displayName", contains("Emmett Brown"),
                        "createUser", contains("mrizzi")
                );
    }

    @Test
    public void testBusinessServicesFilteredMultipleParamsWithMultipleValuesListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("displayName", "emme")
                .queryParam("displayName", "essi")
                .queryParam("email", "murder")
                .queryParam("email", "she")
                .queryParam("email", "wrote")
                .queryParam("sort", "-id")
//                .queryParam("stakeholder group ???", "met")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(59),
                        "displayName", contains("Jessica Fletcher"),
                        "createUser", contains("mrizzi")
                );
    }

    @Test
    public void testStakeholdersListEndpoint() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.config
                .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory((type, s) -> new ObjectMapper()
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule())
                        /*.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)*/));

        List<PanacheEntityBase> list = Stakeholder.findAll().list();

        given()
                .accept("application/json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(2),
                        "id", containsInRelativeOrder(59, 60),
                        "displayName", containsInRelativeOrder("Jessica Fletcher", "Emmett Brown"),
                        "createUser", containsInRelativeOrder("mrizzi", "mrizzi"),
                        "updateUser", containsInRelativeOrder(blankOrNullString(), blankOrNullString())
                );
    }

    @Test
    public void testStakeholderCreateAndDeleteEndpoint() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.displayName = "John Smith";
        JobFunction consultant = new JobFunction();
        consultant.id = 11L;
        stakeholder.jobFunction = consultant;
        stakeholder.email = "another@email.foo";

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(stakeholder)
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(201).extract().response();

        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

        Integer stakeholderId = response.path("id");

        given()
                .pathParam("id", stakeholderId)
                .when().delete(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", stakeholderId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    @DisabledOnNativeImage
    public void testStakeholderCreateUpdateAndDeleteEndpoint() {
        testStakeholderCreateUpdateAndDeleteEndpoint(false);
    }

    protected void testStakeholderCreateUpdateAndDeleteEndpoint(boolean nativeExecution) {
        final String displayName = "Another Stakeholder displayName";
        final String email = "another@description.it";
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.displayName = displayName;
        stakeholder.email = email;
        StakeholderGroup stakeholderGroup = new StakeholderGroup();
        stakeholderGroup.id = 62L;
        StakeholderGroup nonexistent = new StakeholderGroup();
        nonexistent.id = 1234567890L;
        stakeholder.stakeholderGroups.add(stakeholderGroup);
        stakeholder.stakeholderGroups.add(nonexistent);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept("application/hal+json")
                .body(stakeholder)
                .when().log().body().post(PATH)
                .then()
                .log().all()
                .statusCode(201).extract().response();

        assertEquals(displayName, response.path("displayName"));
        assertEquals(email, response.path("email"));
        assertEquals(1, Integer.valueOf(response.path("stakeholderGroups.size()").toString()));
        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

        Long stakeholderId = Long.valueOf(response.path("id").toString());

        StakeholderGroup sg = new StakeholderGroup();
        sg.id = 63L;

        final String newName = "Yet another different displayName";
        stakeholder.displayName = newName;
        stakeholder.stakeholderGroups.add(sg);
        // TODO fix the update with referenced a non-existent Stakeholder Group ID
        stakeholder.stakeholderGroups.remove(nonexistent);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(stakeholder)
                .pathParam("id", stakeholderId)
                .when().put(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/hal+json")
                .pathParam("id", stakeholderId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("displayName", is(newName),
                        "stakeholderGroups.size()", is(2));

        StakeholderGroup sgDuplicate = new StakeholderGroup();
        sgDuplicate.id = 63L;
        stakeholder.stakeholderGroups.add(sgDuplicate);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(stakeholder)
                .pathParam("id", stakeholderId)
                .when().put(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/hal+json")
                .pathParam("id", stakeholderId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("displayName", is(newName),
                        "stakeholderGroups.size()", is(2));

        if (!nativeExecution) {
            Stakeholder updatedStakeholderFromDb = Stakeholder.findById(stakeholderId);
            assertEquals(newName, updatedStakeholderFromDb.displayName);
            assertNotNull(updatedStakeholderFromDb.createTime);
            assertNotNull(updatedStakeholderFromDb.updateTime);
        }

        given()
                .pathParam("id", stakeholderId)
                .when().delete(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", stakeholderId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(404);

    }
}
