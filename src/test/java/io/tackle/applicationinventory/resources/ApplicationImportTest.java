package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;

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
public class ApplicationImportTest extends SecuredResourceTest {
    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction userTransaction;


    @BeforeAll
    public static void init() {

        PATH = "/application-import";

    }

    @Test
    public void testFilterByIsValid() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {

        userTransaction.begin();

        ImportSummary appImportParent = new ImportSummary();
        appImportParent.persistAndFlush();

        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService("BS 1");
        appImport1.importSummary = appImportParent;
        appImport1.setFilename("File1");
        appImport1.persistAndFlush();
        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setBusinessService("BS 2");
        appImport2.importSummary = appImportParent;
        appImport2.setFilename("File1");
        appImport2.setTag1("tag 1");
        appImport2.setTagType1("tag type 1");
        appImport2.setValid(Boolean.FALSE);
        appImport2.persistAndFlush();
        ApplicationImport appImport3 = new ApplicationImport();
        appImport3.setBusinessService("BS 3");
        appImport3.importSummary = appImportParent;
        appImport3.setFilename("File2");
        appImport3.setValid(Boolean.FALSE);
        appImport3.persistAndFlush();

        userTransaction.commit();

        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.FALSE)
                .queryParam("filename","File1")
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(1))
                .body("_embedded.'application-import'[0].'Business Service'", is("BS 2"))
                .body("_embedded.'application-import'[0].'Tag Type 1'", is("tag type 1"));

        given()
                .accept("application/json")
                .queryParam("isValid", Boolean.FALSE)
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .log().body()
                .body("size()", is(2));

        userTransaction.begin();
        ApplicationImport.deleteAll();
        ImportSummary.deleteAll();
        userTransaction.commit();
    }
}
