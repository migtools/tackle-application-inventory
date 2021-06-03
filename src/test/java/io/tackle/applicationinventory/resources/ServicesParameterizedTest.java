package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.commons.entities.AbstractEntity;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.Matchers.containsInRelativeOrder;
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
public class ServicesParameterizedTest extends SecuredResourceTest {

    // the 'name' output seems not to work with Quarkus
    @DisplayName("testListEndpoints")
    @ParameterizedTest(name = "{index} ==> Resource ''{0}'' tested is {1}")
    @CsvSource({
        //   resource path          , size, IDs       , tot, pageLinksSize, entityLinksSize, anotherField, anotherFieldValues
            "application            ,    4, 1::2::3::6,   4,             4,               5,         name, Home Banking BU::Online Investments service::Credit Cards BS",
            "applications-dependency,    2,       4::5,   2,             4,               4,    from.name, Home Banking BU::Online Investments service"
    })
    public void testListEndpoints(String resource, int size, @ConvertWith(CSVtoArray.class) Integer[] ids,
                                  int totalCount, int pageLinksSize, int entityLinksSize,
                                  String anotherFieldName, @ConvertWith(CSVtoArray.class) String[] anotherFieldValues) {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .queryParam("size", "5")
                .when()
                .get(resource)
                .then()
                .statusCode(200)
                .body(String.format("_embedded.%s.size()", resource), is(size),
                        String.format("_embedded.%s.id", resource), containsInRelativeOrder(ids),
                        String.format("_embedded.%s.%s", resource, anotherFieldName), containsInRelativeOrder(anotherFieldValues),
                        String.format("_embedded.%s[1]._links.size()", resource), is(entityLinksSize),
                        String.format("_embedded.%s[1]._links.self.href", resource), is(String.format("http://localhost:8081/application-inventory/%s/%d", resource, ids[1])),
                        String.format("_embedded._metadata.totalCount", resource), is(totalCount),
                        "_links.size()", is(pageLinksSize));

        given()
                .accept("application/json")
                .queryParam("sort", "id")
                .queryParam("size", "5")
                .when()
                .get(resource)
                .then()
                .statusCode(200)
                .body(String.format("size()", resource), is(size),
                        String.format("id", resource), containsInRelativeOrder(ids),
                        String.format("%s", anotherFieldName), containsInRelativeOrder(anotherFieldValues));
    }

    @ParameterizedTest(name = "{index} ==> Resource ''{0}'' tested is {1}")
    @MethodSource("provideObjects")
    public void testCreateAndDeleteEndpoint(AbstractEntity entity, String resource) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(entity)
                .when()
                .post(resource)
                .then()
                .statusCode(201)
                .extract()
                .response();

        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

        Integer entityId = response.path("id");

        given()
                .pathParam("id", entityId)
                .when()
                .delete(format("%s/{id}", resource))
                .then()
                .statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", entityId)
                .when()
                .get(format("%s/{id}", resource))
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @MethodSource("testEntityUniquenessArguments")
    // https://github.com/konveyor/tackle-application-inventory/issues/65
    public void testEntityUniqueness(AbstractEntity entity, String resource) {
        // create the entity
        Long firstId = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(entity)
                .when()
                .post(resource)
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        // try to add another time the same entity
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(entity)
                .when()
                .post(resource)
                .then()
                .statusCode(409);

        // remove the initial entity
        given()
                .pathParam("id", firstId)
                .when()
                .delete(resource + "/{id}")
                .then()
                .statusCode(204);

        // and check the 'duplicated' entity now will be added
        // proving the partial unique index is working properly with soft-delete
        Long secondId = Long.valueOf(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(entity)
                .when()
                .post(resource)
                .then()
                .statusCode(201)
                .extract()
                .path("id")
                .toString());

        // remove 'duplicated' entity to not alter other tests
        given()
                .pathParam("id", secondId)
                .when()
                .delete(resource + "/{id}")
                .then()
                .statusCode(204);
    }

    public static class CSVtoArray extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            final Stream<String> stream = Arrays.stream(((String) source).split("::"));
            if (targetType.isAssignableFrom(Integer[].class)) return stream.map(Integer::valueOf).toArray(Integer[]::new);
            else return stream.toArray(String[]::new);
        }
    }

    private static Stream<Arguments> provideObjects() {
        Application application = new Application();
        application.name = "tackle";
        application.description = "Tackle helps you modernize your applications.";
        application.businessService = "123";
        application.comments = "Tackle application needs even some comments";

        ApplicationsDependency dependency = new ApplicationsDependency();
        Application frontend = new Application();
        frontend.id = 1L;
        dependency.from = frontend;
        Application db = new Application();
        db.id = 3L;
        dependency.to = db;

        Review review = new Review();
        review.proposedAction = "Retain";
        review.effortEstimate = "Medium";
        review.businessCriticality = 10;
        review.workPriority = 10;
        review.comments = "No comment!";
        Application reviewed = new Application();
        reviewed.id = 6L;
        review.application = reviewed;

        return Stream.of(
                Arguments.of(application, "/application"),
                Arguments.of(dependency, "/applications-dependency"),
                Arguments.of(review, "/review")
        );
    }

    private static Stream<Arguments> testEntityUniquenessArguments() {
        Application application = new Application();
        application.name = "application";

        return Stream.of(
                Arguments.of(application, "/application")
        );
    }
}
