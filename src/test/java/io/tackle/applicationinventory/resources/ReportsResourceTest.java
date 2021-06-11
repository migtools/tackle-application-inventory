package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.dto.ApplicationDto;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.services.ReportTestUtil;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

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
class ReportsResourceTest extends ReportTestUtil {
    @Test
    @Transactional
    public void given_10appsWithDependencies_when_calltoadoptionplan_result_isexpectedjson() {
        List<Long> listApps = Application.find("name like 'App%'").stream().map(e -> ((Application) e).id).collect(Collectors.toList());
        given()
            .contentType(ContentType.JSON)
            .body(listApps.stream().map(ApplicationDto::new).collect(Collectors.toList()))
        .when()
            .post("/report/adoptionplan")
        .then()
            .log().all()
            .body("size()", is(10))
            .body("find{it.applicationName=='App19'}.positionX", is(0))
            .body("find{it.applicationName=='App19'}.decision", is("Refactor"))
            .body("find{it.applicationName=='App19'}.effortEstimate", is("Extra_Large"))
            .body("find{it.applicationName=='App19'}.positionY", is(0))
            .body("find{it.applicationName=='App19'}.effort", is(8))
            .body("find{it.applicationName=='App16'}.positionX", is(8))
            .body("find{it.applicationName=='App16'}.positionY", is(2));
    }

}