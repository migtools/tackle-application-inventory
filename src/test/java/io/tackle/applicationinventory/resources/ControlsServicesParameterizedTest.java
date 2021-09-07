package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.commons.entities.AbstractEntity;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import io.tackle.applicationinventory.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
public class ControlsServicesParameterizedTest extends SecuredResourceTest {

    // the 'name' output seems not to work with Quarkus
    @DisplayName("testListEndpoints")
    @ParameterizedTest(name = "{index} ==> Resource ''{0}'' tested is {1}")
    @CsvSource({
            //   resource path    ,size, IDs               ,tot,linkSize,anotherField, anotherFieldValues
            "stakeholder      ,   2, 59::60               , 2 ,       4, displayName, Jessica Fletcher::Emmett Brown                              ",
            "business-service ,   3, 56::57::58           , 3 ,       4, name       , Home Banking BU::Online Investments service::Credit Cards BS",
            "job-function     ,   5, 10::11::12::13::14   , 12,       5, role       , Business Analyst::Business Service Owner / Manager::Consultant::DBA::Developer / Software Engineer",
            "tag-type         ,   5, 22::23::24           , 6 ,       5, colour     , #ec7a08::#2b9af3::#6ec664::#009596::#a18fff",
            "tag              ,   5, 28::29::30::31::32   , 28,       5, name       , COTS::In house::SaaS::Boston (USA)::London (UK)",
            "stakeholder-group,   3, 61::62::63           , 3 ,       4, description, Managers Group::Engineers Group::Marketing Group"
    })
    public void testListEndpoints(String resource, int size, @ConvertWith(CSVtoArray.class) Integer[] ids, int totalCount, int linkSize,
                                  String anotherFieldName, @ConvertWith(CSVtoArray.class) String[] anotherFieldValues) {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .queryParam("size", "5")
                .when().get(resource)
                .then()
                .statusCode(200)
                .body(format("_embedded.%s.size()", resource), is(size),
                        format("_embedded.%s.id", resource), containsInRelativeOrder(ids),
                        format("_embedded.%s.%s", resource, anotherFieldName), containsInRelativeOrder(anotherFieldValues),
                        format("_embedded.%s[1]._links.size()", resource), is(5),
                        format("_embedded.%s[1]._links.self.href", resource), is(format("http://localhost:8081/application-inventory/%s/%d", resource, ids[1])),
                        format("_embedded._metadata.totalCount", resource), is(totalCount),
                        "_links.size()", is(linkSize));

        given()
                .accept("application/json")
                .queryParam("sort", "id")
                .queryParam("size", "5")
                .when().get(resource)
                .then()
                .statusCode(200)
                .body("size()", is(size),
                        "id", containsInRelativeOrder(ids),
                        format("%s", anotherFieldName), containsInRelativeOrder(anotherFieldValues));
    }

    @ParameterizedTest(name = "{index} ==> Resource ''{0}'' tested is {1}")
    @MethodSource("provideObjects")
    public void testCreateAndDeleteEndpoint(AbstractEntity entity, String resource) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(entity)
                .when().post(resource)
                .then()
                .log().all()
                .statusCode(201).extract().response();

        assertEquals("alice", response.path("createUser"));
        assertEquals("alice", response.path("updateUser"));

        Integer entityId = response.path("id");

        given()
                .pathParam("id", entityId)
                .when().delete(format("%s/{id}", resource))
                .then().statusCode(204);

        given()
                .accept("application/json")
                .pathParam("id", entityId)
                .when().get(format("%s/{id}", resource))
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @MethodSource("testEntityUniquenessArguments")
    // https://github.com/konveyor/tackle-controls/issues/114
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
                // this will expect the same '409' from Quarkus 1.13+ with the introduction of RestDataPanacheException
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
            if (targetType.isAssignableFrom(Integer[].class))
                return stream.map(Integer::valueOf).toArray(Integer[]::new);
            else return stream.toArray(String[]::new);
        }
    }

    private static Stream<Arguments> provideObjects() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.displayName = "John Smith";
        JobFunction consultant = new JobFunction();
        consultant.id = 11L;
        stakeholder.jobFunction = consultant;
        stakeholder.email = "another@email.foo";
        StakeholderGroup marketing = new StakeholderGroup();
        marketing.id = 63L;
        stakeholder.stakeholderGroups = Collections.singleton(marketing);

        JobFunction ceo = new JobFunction();
        ceo.role = "CEO";

        TagType tagType = new TagType();
        tagType.name = "Red";
        tagType.rank = 1;
        tagType.colour = "#FF0000";

        TagType parentTagType = new TagType();
        parentTagType.id = 25L;
        Tag tag = new Tag();
        tag.tagType = parentTagType;
        tag.name = "value";

        StakeholderGroup stakeholderGroup = new StakeholderGroup();
        stakeholderGroup.name = "name";
        stakeholderGroup.description = "description";
        Stakeholder stakeholder1 = new Stakeholder();
        stakeholder1.id = 59L;
        Stakeholder jessica = new Stakeholder();
        jessica.id = 59L;
        Stakeholder emmett = new Stakeholder();
        emmett.id = 60L;
        stakeholderGroup.stakeholders = new HashSet<>(Arrays.asList(jessica, emmett));

        return Stream.of(
                Arguments.of(stakeholder, "/stakeholder"),
                Arguments.of(ceo, "/job-function"),
                Arguments.of(tagType, "/tag-type"),
                Arguments.of(tag, "/tag"),
                Arguments.of(stakeholderGroup, "/stakeholder-group")
        );
    }

    private static Stream<Arguments> testEntityUniquenessArguments() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.email = "unique@email.com";

        StakeholderGroup stakeholderGroup = new StakeholderGroup();
        stakeholderGroup.name = "for the testUniqueName";

        JobFunction jobFunction = new JobFunction();
        jobFunction.role = "test unique role";

        BusinessService businessService = new BusinessService();
        businessService.name = "test unique name";

        TagType tagType = new TagType();
        tagType.name = "test unique name";

        Tag tag = new Tag();
        tag.name = "test unique name";
        TagType associatedTagType = new TagType();
        associatedTagType.id = 24L;
        tag.tagType = associatedTagType;

        return Stream.of(
                Arguments.of(stakeholder, "/stakeholder"),
                Arguments.of(stakeholderGroup, "/stakeholder-group"),
                Arguments.of(jobFunction, "/job-function"),
                Arguments.of(businessService, "/business-service"),
                Arguments.of(tagType, "/tag-type"),
                Arguments.of(tag, "/tag")
        );
    }
}
