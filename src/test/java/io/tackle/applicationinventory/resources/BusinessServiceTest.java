package io.tackle.applicationinventory.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.tackle.applicationinventory.entities.BusinessService;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
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
public class BusinessServiceTest extends SecuredResourceTest {

    @BeforeAll
    public static void init() {
        PATH = "/business-service";
    }

    @Test
    public void testBusinessServicesListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "-id")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(3),
                        "_embedded.business-service.id", containsInRelativeOrder(58, 57, 56),
                        "_embedded.business-service.name", containsInRelativeOrder("Credit Cards BS", "Online Investments service", "Home Banking BU"),
                        "_embedded.business-service[1]._links.size()", is(5),
                        "_embedded.business-service[1]._links.self.href", is("http://localhost:8081/application-inventory/business-service/57"),
                        "_links.size()", is(4));
    }

    @Test
    public void testBusinessServicesFilteredSingleParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("name", "home")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(1),
                        "_embedded.business-service.id", contains(56),
                        "_embedded.business-service.name", contains("Home Banking BU"),
                        "_embedded.business-service[0]._links.size()", is(5),
                        "_embedded.business-service[0]._links.self.href", is("http://localhost:8081/application-inventory/business-service/56"),
                        "_links.size()", is(4));
    }

    @Test
    public void testBusinessServicesFilteredWrongParamListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("wrong", "wrongAsWell")
                .when().get(PATH)
                .then()
                .statusCode(400);

        given()
                .accept("application/hal+json")
                .queryParam("owner.wrong", "wrongAsWell")
                .when().get(PATH)
                .then()
                .statusCode(400);
    }

    @Test
    public void testBusinessServicesFilteredMultipleParamsListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("name", "service")
                .queryParam("description", "maNAgeMent")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(57),
                        "name", contains("Online Investments service"),
                        "createUser", contains("foo"),
                        "updateUser", contains("mrizzi")
                );
    }

    @Test
    public void testBusinessServicesFilteredMultipleParamsWithMultipleValuesListEndpoint() {
        given()
                .accept("application/json")
                .queryParam("name", "service")
                .queryParam("name", "edit")
                .queryParam("description", "management")
                .queryParam("sort", "id")
                .queryParam("owner.displayName", "met")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(1),
                        "id", contains(57),
                        "name", contains("Online Investments service"),
                        "owner.displayName", contains("Emmett Brown"),
                        "createUser", contains("foo"),
                        "updateUser", contains("mrizzi")
                );
    }

    @Test
    public void testBusinessServicesListEndpoint() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.config
                .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory((type, s) -> new ObjectMapper()
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule())
                        /*.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)*/));

        given()
                .accept("application/json")
                .queryParam("sort", "id")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", is(3),
                        "id", containsInRelativeOrder(56, 57, 58),
                        "name", containsInRelativeOrder("Home Banking BU", "Online Investments service", "Credit Cards BS"),
                        "createUser", containsInRelativeOrder("mrizzi", "foo", "foo"),
                        "updateUser", containsInRelativeOrder("mrizzi", "mrizzi", null)
                );
    }

    @Test
    @DisabledOnNativeImage
    public void testBusinessServiceCreateUpdateAndDeleteEndpoint() {
        testBusinessServiceCreateUpdateAndDeleteEndpoint(false);
    }

    protected void testBusinessServiceCreateUpdateAndDeleteEndpoint(boolean nativeExecution) {
        final String name = "Another Business Service name";
        final String description = "Another Business Service description";
        BusinessService businessService = new BusinessService();
        businessService.name = name;
        businessService.description = description;

         Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(businessService)
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(201).extract().response();

         assertEquals(name, response.path("name"));
         assertEquals(description, response.path("description"));
         assertEquals("alice", response.path("createUser"));
         assertEquals("alice", response.path("updateUser"));

        Long businessServiceId = Long.valueOf(response.path("id").toString());

        final String newName = "Yet another different name";
        businessService.name = newName;
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(businessService)
                .pathParam("id", businessServiceId)
                .when().put(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", businessServiceId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is(newName),
                "description", is(description));

        if (!nativeExecution) {
            BusinessService updatedBusinessServiceFromDb = BusinessService.findById(businessServiceId);
            assertEquals(newName, updatedBusinessServiceFromDb.name);
            assertEquals(description, updatedBusinessServiceFromDb.description);
            assertNotNull(updatedBusinessServiceFromDb.createTime);
            assertNotNull(updatedBusinessServiceFromDb.updateTime);
        }

        given()
                .pathParam("id", businessServiceId)
                .when().delete(PATH + "/{id}")
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", businessServiceId)
                .when().get(PATH + "/{id}")
                .then()
                .log().all()
                .statusCode(404);

    }

    @Test
    public void testBusinessServicePaginationWithPrevAndNextLinks() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "name")
                .queryParam("name", "bank")
                .queryParam("name", "line")
                .queryParam("name", "card")
                .queryParam("size", "1")
                .queryParam("page", "1")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(1),
                        "_embedded.business-service.id", containsInRelativeOrder( 56),
                        "_embedded.business-service.name", containsInRelativeOrder("Home Banking BU"),
                        "_embedded.business-service[0]._links.size()", is(5),
                        "_embedded.business-service[0]._links.self.href", is("http://localhost:8081/application-inventory/business-service/56"),
                        "_links.size()", is(6));
    }

    @Test
    public void testBusinessServicePaginationWithSameNextAndLastLinks() {
        Map<String, List<String>> expectedLinkHeaders = new HashMap<>();
        expectedLinkHeaders.put("Link", new ArrayList<>());
        Response response = given()
                .accept("application/hal+json")
                .queryParam("sort", "name")
                .queryParam("name", "bank")
                .queryParam("name", "line")
                .queryParam("size", "1")
                .queryParam("page", "0")
                .when().get(PATH)
                .then()
                .log().all()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(1),
                        "_embedded.business-service.id", containsInRelativeOrder( 56),
                        "_embedded.business-service.name", containsInRelativeOrder("Home Banking BU"),
                        "_embedded.business-service[0]._links.size()", is(5),
                        "_embedded.business-service[0]._links.self.href", is("http://localhost:8081/application-inventory/business-service/56"),
                        "_links.size()", is(5),
                        "_links.next.href", is("http://localhost:8081/application-inventory/business-service?page=1&size=1&sort=name&name=bank&name=line"),
                        "_links.last.href", is("http://localhost:8081/application-inventory/business-service?page=1&size=1&sort=name&name=bank&name=line"))
                .extract().response();
        assertThat(response.headers().getValues("Link"), containsInRelativeOrder(
                "<http://localhost:8081/application-inventory/business-service?page=0&size=1&sort=name&name=bank&name=line>; rel=\"first\"",
                "<http://localhost:8081/application-inventory/business-service?page=1&size=1&sort=name&name=bank&name=line>; rel=\"last\"",
                "<http://localhost:8081/application-inventory/business-service?page=1&size=1&sort=name&name=bank&name=line>; rel=\"next\""));
    }

    @Test
    public void testBusinessServiceWeirdParameters() {
        given()
                .accept("application/hal+json")
                .queryParam("size", "-1")
                .queryParam("page", "-1")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(3),
                        "_embedded.business-service[0]._links.size()", is(5),
                        "_embedded.business-service[0]._links.self.href", is("http://localhost:8081/application-inventory/business-service/56"),
                        "_links.size()", is(4));
    }

    @Test
    public void testBusinessServicesSortOwnerDisplayNameListHalEndpoint() {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "owner.displayName")
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body("_embedded.business-service.size()", is(3),
                        "_embedded.business-service.id", containsInRelativeOrder(57, 56, 58),
                        "_embedded.business-service.name", containsInRelativeOrder("Online Investments service", "Home Banking BU", "Credit Cards BS"),
                        "_embedded.business-service[1]._links.size()", is(5),
                        "_embedded.business-service[1]._links.self.href", is("http://localhost:8081/application-inventory/business-service/56"),
                        "_links.size()", is(4));
    }
}
