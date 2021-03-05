package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInRelativeOrder;
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
public class ServicesParameterizedTest extends SecuredResourceTest {

    // the 'name' output seems not to work with Quarkus
    @DisplayName("testListHalEndpoint")
    @ParameterizedTest(name = "{index} ==> Resource ''{0}'' tested is {1}")
    @CsvSource({
            "application, 3, 1::2::3, name, Home Banking BU::Online Investments service::Credit Cards BS, 2, 3"
    })
    public void testListHalEndpoint(String resource, int size, @ConvertWith(CSVtoArray.class) Integer[] ids,
                                    String anotherFieldName, @ConvertWith(CSVtoArray.class) String[] anotherFieldValues,
                                    int selfId, int totalCount) {
        given()
                .accept("application/hal+json")
                .queryParam("sort", "id")
                .when().get(resource)
                .then()
                .statusCode(200)
                .body(String.format("_embedded.%s.size()", resource), is(size),
                        String.format("_embedded.%s.id", resource), containsInRelativeOrder(ids),
                        String.format("_embedded.%s.%s", resource, anotherFieldName), containsInRelativeOrder(anotherFieldValues),
                        String.format("_embedded.%s[1]._links.size()", resource), is(5),
                        String.format("_embedded.%s[1]._links.self.href", resource), is(String.format("http://localhost:8081/application-inventory/%s/%d", resource, selfId)),
                        String.format("_embedded._metadata.totalCount", resource), is(totalCount),
                        "_links.size()", is(4));

        given()
                .accept("application/json")
                .queryParam("sort", "id")
                .when().get(resource)
                .then()
                .statusCode(200)
                .body(String.format("size()", resource), is(size),
                        String.format("id", resource), containsInRelativeOrder(ids),
                        String.format("%s", anotherFieldName), containsInRelativeOrder(anotherFieldValues));
    }

    public static class CSVtoArray extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            final Stream<String> stream = Arrays.stream(((String) source).split("::"));
            if (targetType.isAssignableFrom(Integer[].class)) return stream.map(Integer::valueOf).toArray(Integer[]::new);
            else return stream.toArray(String[]::new);
        }
    }

}
