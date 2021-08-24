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

      /**  ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService("BS 1");
        appImport1.importSummary = appImportParent;
        appImport1.setDescription("hello");
        appImport1.persistAndFlush();
        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setBusinessService("BS 2");
        appImport2.setApplicationName("");
        appImport2.importSummary = appImportParent;
        appImport2.setDescription("this");
        appImport2.setTag1("tag 1");
        appImport2.setTagType1("tag type 1");
        appImport2.setTag2("tag 1");
        appImport2.setTagType2("tag type 1");
        appImport2.setTag3("tag 1");
        appImport2.setTagType3("tag type 1");
        appImport2.setTag4("tag 1");
        appImport2.setTagType4("tag type 1");
        appImport2.setTag5("tag 1");
        appImport2.setTagType5("tag type 1");
        appImport2.setTag6("tag 1");
        appImport2.setTagType6("tag type 1");
        appImport2.setTag7("tag 1");
        appImport2.setTagType7("tag type 1");
        appImport2.setTag8("tag 1");
        appImport2.setTagType8("tag type 1");
        appImport2.setTag9("tag 1");
        appImport2.setTagType9("tag type 1");
        appImport2.setTag10("tag 1");
        appImport2.setTagType10("tag type 1");
        appImport2.setTag11("tag 1");
        appImport2.setTagType11("tag type 1");
        appImport2.setTag12("tag 1");
        appImport2.setTagType12("tag type 1");
        appImport2.setTag13("tag 1");
        appImport2.setTagType13("tag type 1");
        appImport2.setTag14("tag 1");
        appImport2.setTagType14("tag type 1");
        appImport2.setTag15("tag 1");
        appImport2.setTagType15("tag type 1");
        appImport2.setTag16("tag 1");
        appImport2.setTagType16("tag type 1");
        appImport2.setTag17("tag 1");
        appImport2.setTagType17("tag type 1");
        appImport2.setTag18("tag 1");
        appImport2.setTagType18("tag type 1");
        appImport2.setTag19("tag 1");
        appImport2.setTagType19("tag type 1");
        appImport2.setTag20("tag 1");
        appImport2.setTagType20("tag type 1");
        appImport2.persistAndFlush();
        ApplicationImport appImport3 = new ApplicationImport();
        appImport3.setBusinessService("BS 2");
        appImport3.setApplicationName("Name 3");
        appImport3.importSummary = appImportParent;
        appImport3.setDescription("and this");
        appImport3.setTag1("");
        appImport3.setTag2("");
        appImport3.setTagType2("");
        appImport3.setTag3("mystery tag");
        appImport3.setTagType3("");
        appImport3.setTag4("");
        appImport3.setTagType4("");
        appImport3.persistAndFlush();
        ApplicationImport appImport4 = new ApplicationImport();
        appImport4.setBusinessService("BS 2");
        appImport4.setApplicationName("Name 4");
        appImport4.importSummary = appImportParent;
        appImport4.setDescription("and this");
        appImport4.setTagType1("");
        appImport4.setTagType2("mystery tag type");
        appImport4.persistAndFlush();
        ApplicationImport appImport5 = new ApplicationImport();
        appImport5.setBusinessService("BS 2");
        appImport5.setApplicationName("Name 5");
        appImport5.importSummary = appImportParent;
        appImport5.setDescription("and this");
        appImport5.setTag1("yes");
        appImport5.persistAndFlush(); */
        ApplicationImport appImport6 = new ApplicationImport();
        appImport6.setBusinessService(null);
        appImport6.setApplicationName(null);
        appImport6.importSummary = appImportParent;
        appImport6.setDescription(null);
        appImport6.setTag1(null);
        appImport6.setTagType1(null);
        appImport6.setTag2(null);
        appImport6.setTagType2(null);
        appImport6.setTag3(null);
        appImport6.setTagType3(null);
        appImport6.setTag4(null);
        appImport6.setTagType4(null);
        appImport6.setTag5(null);
        appImport6.setTagType5(null);
        appImport6.setTag6(null);
        appImport6.setTagType6(null);
        appImport6.setTag7(null);
        appImport6.setTagType7(null);
        appImport6.setTag8(null);
        appImport6.setTagType8(null);
        appImport6.setTag9(null);
        appImport6.setTagType9(null);
        appImport6.setTag10(null);
        appImport6.setTagType10(null);
        appImport6.setTag11(null);
        appImport6.setTagType11(null);
        appImport6.setTag12(null);
        appImport6.setTagType12(null);
        appImport6.setTag13(null);
        appImport6.setTagType13(null);
        appImport6.setTag14(null);
        appImport6.setTagType14(null);
        appImport6.setTag15(null);
        appImport6.setTagType15(null);
        appImport6.setTag16(null);
        appImport6.setTagType16(null);
        appImport6.setTag17(null);
        appImport6.setTagType17(null);
        appImport6.setTag18(null);
        appImport6.setTagType18(null);
        appImport6.setTag19(null);
        appImport6.setTagType19(null);
        appImport6.setTag20(null);
        appImport6.setTagType20(null);
        appImport6.setFilename(null);
        appImport6.setErrorMessage(null);
        appImport6.setRecordType1(null);
        appImport6.setComments(null);

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
        apiMapper.map(appImport6, appImportParent.id);

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
