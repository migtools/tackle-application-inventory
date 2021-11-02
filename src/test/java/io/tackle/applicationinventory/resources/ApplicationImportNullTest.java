package io.tackle.applicationinventory.resources;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.tackle.applicationinventory.AbstractBaseTest;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.applicationinventory.mapper.ApplicationDependencyAPIMapper;
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
import static org.junit.jupiter.api.Assertions.assertNull;


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
public class ApplicationImportNullTest extends AbstractBaseTest {

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
        appImport1.setDependency(null);
        appImport1.setDependencyDirection(null);

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

        assertNull(appImport1.getRecordType1());
        assertNull(appImport1.getComments());
        assertNull(appImport1.getBusinessService());
        assertNull(appImport1.getApplicationName());
        assertNull(appImport1.getDescription());
        assertNull(appImport1.getTag1());
        assertNull(appImport1.getTagType1());
        assertNull(appImport1.getTag2());
        assertNull(appImport1.getTagType2());
        assertNull(appImport1.getTag3());
        assertNull(appImport1.getTagType3());
        assertNull(appImport1.getTag4());
        assertNull(appImport1.getTagType4());
        assertNull(appImport1.getTag5());
        assertNull(appImport1.getTagType5());
        assertNull(appImport1.getTag6());
        assertNull(appImport1.getTagType6());
        assertNull(appImport1.getTag7());
        assertNull(appImport1.getTagType7());
        assertNull(appImport1.getTag8());
        assertNull(appImport1.getTagType8());
        assertNull(appImport1.getTag9());
        assertNull(appImport1.getTagType9());
        assertNull(appImport1.getTag10());
        assertNull(appImport1.getTagType10());
        assertNull(appImport1.getTag11());
        assertNull(appImport1.getTagType11());
        assertNull(appImport1.getTag12());
        assertNull(appImport1.getTagType12());
        assertNull(appImport1.getTag13());
        assertNull(appImport1.getTagType13());
        assertNull(appImport1.getTag14());
        assertNull(appImport1.getTagType14());
        assertNull(appImport1.getTag15());
        assertNull(appImport1.getTagType15());
        assertNull(appImport1.getTag16());
        assertNull(appImport1.getTagType16());
        assertNull(appImport1.getTag17());
        assertNull(appImport1.getTagType17());
        assertNull(appImport1.getTag18());
        assertNull(appImport1.getTagType18());
        assertNull(appImport1.getTag19());
        assertNull(appImport1.getTagType19());
        assertNull(appImport1.getTag20());
        assertNull(appImport1.getTagType20());
    }

    @Test
    @Transactional
    protected void testNullDependency() {
        ImportSummary appImportParent = new ImportSummary();
        appImportParent.persistAndFlush();

        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService(null);
        appImport1.setApplicationName("Online Investments service");
        appImport1.importSummary = appImportParent;
        appImport1.setRecordType1("2");
        appImport1.setDependency(null);
        appImport1.setDependencyDirection(null);

        ApplicationDependencyAPIMapper apiMapper = new ApplicationDependencyAPIMapper();
        apiMapper.map(appImport1, appImportParent.id);

        assertNull(appImport1.getDependency());

        ImportSummary appImportParent2 = new ImportSummary();
        appImportParent2.persistAndFlush();

        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setBusinessService(null);
        appImport2.setApplicationName(null);
        appImport2.importSummary = appImportParent;
        appImport1.setRecordType1("2");
        appImport1.setDependency(null);
        appImport1.setDependencyDirection(null);

        apiMapper.map(appImport2, appImportParent2.id);

        assertNull(appImport2.getDependency());

        ImportSummary appImportParent3 = new ImportSummary();
        appImportParent3.persistAndFlush();

        ApplicationImport appImport3 = new ApplicationImport();
        appImport3.setApplicationName("Online Investments service");
        appImport3.importSummary = appImportParent;
        appImport3.setRecordType1("2");
        appImport3.setDependency("Home Banking BU");
        appImport3.setDependencyDirection(null);

        apiMapper.map(appImport3, appImportParent3.id);

        assertNull(appImport3.getDependencyDirection());
    }

}
