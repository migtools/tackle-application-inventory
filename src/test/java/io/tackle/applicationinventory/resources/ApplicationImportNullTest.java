package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.applicationinventory.mapper.ApplicationInventoryAPIMapper;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
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
public class ApplicationImportNullTest extends SecuredResourceTest {

    @Test
    @Transactional
    public void testNullApplicationName() {

        ImportSummary importSummary = new ImportSummary();
        importSummary.persistAndFlush();

        ApplicationImport importItem = new ApplicationImport();
        importItem.setApplicationName(null);

        Set<Tag> tags = new HashSet<>();
        Tag tag1 = new Tag();
        tag1.id = "1";
        tag1.name = "tag1";
        tags.add(tag1);

        Set<BusinessService> businessServices = new HashSet<>();
        BusinessService bs = new BusinessService();
        bs.id = "1";
        bs.name = "bs";
        businessServices.add(bs);

        ApplicationInventoryAPIMapper apiMapper = new ApplicationInventoryAPIMapper(tags, businessServices);
        apiMapper.map(importItem, importSummary.id);

        assertEquals("Application Name is mandatory", importItem.getErrorMessage());

        //Remove test data before finishing
        ImportSummary[] summaryList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/import-summary")
                        .as(ImportSummary[].class);

        Arrays.asList(summaryList).forEach(summary ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", summary.id)
                        .when()
                        .delete("/import-summary/{id}")
                        .then()
                        .statusCode(204));


        ApplicationImport[] importList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/application-import")
                        .as(ApplicationImport[].class);


        Arrays.asList(importList).forEach(thisImport ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", thisImport.id)
                        .when()
                        .delete("/application-import/{id}")
                        .then()
                        .statusCode(204));


    }

    @Test
    @Transactional
    protected void testNullTagTypes() {
        ImportSummary appImportParent = new ImportSummary();
        appImportParent.persistAndFlush();
        
        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService(null);
        appImport1.setApplicationName(null);
        appImport1.importSummary = appImportParent;
        appImport1.setDescription(null);
        appImport1.setTag1(null);
        appImport1.setTagType1(null);
        appImport1.setTag2(null);
        appImport1.setTagType2(null);
        appImport1.setTag3(null);
        appImport1.setTagType3(null);
        appImport1.setTag4(null);
        appImport1.setTagType4(null);
        appImport1.setTag5(null);
        appImport1.setTagType5(null);
        appImport1.setTag6(null);
        appImport1.setTagType6(null);
        appImport1.setTag7(null);
        appImport1.setTagType7(null);
        appImport1.setTag8(null);
        appImport1.setTagType8(null);
        appImport1.setTag9(null);
        appImport1.setTagType9(null);
        appImport1.setTag10(null);
        appImport1.setTagType10(null);
        appImport1.setTag11(null);
        appImport1.setTagType11(null);
        appImport1.setTag12(null);
        appImport1.setTagType12(null);
        appImport1.setTag13(null);
        appImport1.setTagType13(null);
        appImport1.setTag14(null);
        appImport1.setTagType14(null);
        appImport1.setTag15(null);
        appImport1.setTagType15(null);
        appImport1.setTag16(null);
        appImport1.setTagType16(null);
        appImport1.setTag17(null);
        appImport1.setTagType17(null);
        appImport1.setTag18(null);
        appImport1.setTagType18(null);
        appImport1.setTag19(null);
        appImport1.setTagType19(null);
        appImport1.setTag20(null);
        appImport1.setTagType20(null);
        appImport1.setFilename(null);
        appImport1.setErrorMessage(null);
        appImport1.setRecordType1(null);
        appImport1.setComments(null);

        Set<Tag> tags = new HashSet<>() ;
        Tag.TagType tagType1 = new Tag.TagType();
        tagType1.id = "1";
        tagType1.name = "Unknown tag type";
        Tag tag = new Tag();
        tag.id = "1";
        tag.name = "Unknown OS";
        tag.tagType = tagType1;
        tags.add(tag);

        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "BS 2";
        businessServices.add(businessService);

        ApplicationInventoryAPIMapper apiMapper = new ApplicationInventoryAPIMapper(tags, businessServices);
        apiMapper.map(appImport1, appImportParent.id);

        //Remove test data before finishing
        ImportSummary[] summaryList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/import-summary")
                        .as(ImportSummary[].class);

        Arrays.asList(summaryList).forEach(summary ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", summary.id)
                        .when()
                        .delete("/import-summary/{id}")
                        .then()
                        .statusCode(204));


        ApplicationImport[] importList =
                given()
                        .accept("application/json")
                        .when()
                        .get("/application-import")
                        .as(ApplicationImport[].class);


        Arrays.asList(importList).forEach(thisImport ->
                given()
                        .accept(ContentType.JSON)
                        .pathParam("id", thisImport.id)
                        .when()
                        .delete("/application-import/{id}")
                        .then()
                        .statusCode(204));


    }
}
