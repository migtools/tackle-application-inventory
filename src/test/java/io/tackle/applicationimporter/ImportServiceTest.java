package io.tackle.applicationimporter;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.MultipartImportBody;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.services.BusinessServiceService;
import io.tackle.applicationinventory.services.ImportService;
import io.tackle.applicationinventory.services.TagService;
import io.tackle.commons.testcontainers.KeycloakTestResource;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import io.tackle.commons.tests.SecuredResourceTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImportServiceTest extends SecuredResourceTest {
    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction userTransaction;

    @InjectMock
    @RestClient
    TagService mockTagService;

    @InjectMock
    @RestClient
    BusinessServiceService mockBusinessServiceService;

    @BeforeAll
    public static void init() {
        PATH = "/file/upload";
    }


    @Test
    @Order(1)
    protected void testImportServicePost() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

        userTransaction.begin();
        Set<Tag> tags = new HashSet<>() ;
        Tag.TagType tagType1 = new Tag.TagType();
        tagType1.id = "1";
        tagType1.name = "Operating System";
        Tag tag = new Tag();
        tag.id = "1";
        tag.name = "RHEL 8";
        tag.tagType = tagType1;
        tags.add(tag);
        Tag.TagType tagType2 = new Tag.TagType();
        tagType2.id = "2";
        tagType2.name = "Database";
        Tag tag1 = new Tag();
        tag1.id = "2";
        tag1.name = "Oracle";
        tag1.tagType = tagType2;
        tags.add(tag1);
        Tag.TagType tagType3 = new Tag.TagType();
        tagType3.id = "3";
        tagType3.name = "Language";
        Tag tag2 = new Tag();
        tag2.id = "3";
        tag2.name = "Java EE";
        tag2.tagType = tagType3;
        tags.add(tag2);
        Tag.TagType tagType4 = new Tag.TagType();
        tagType4.id = "4";
        tagType4.name = "Runtime";
        Tag tag3 = new Tag();
        tag3.id = "3";
        tag3.name = "Tomcat";
        tag3.tagType = tagType4;
        tags.add(tag3);
        Mockito.when(mockTagService.getListOfTags()).thenReturn(tags);


        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "Food2Go";
        businessServices.add(businessService);
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices()).thenReturn(businessServices);

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());
        //check the correct number of application imports have been persisted
        assertEquals(8, ApplicationImport.listAll().size());
        userTransaction.commit();

        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.TRUE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'.size()", is(2));

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(2)
    protected void testMapToApplicationRejected() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        ImportService svc = new ImportService();
        ApplicationImport appImportParent = new ApplicationImport();
        appImportParent.setBusinessService("BS 1");
        appImportParent.setDescription("hello");
        appImportParent.persistAndFlush();
        Long parentId = appImportParent.id;

        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService("BS 1");
        appImport1.setDescription("hello");
        appImport1.persistAndFlush();
        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setBusinessService("BS 2");
        appImport2.setDescription("this");
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
        appImport4.setDescription("and this");
        appImport4.setTagType1("");
        appImport4.setTagType2("mystery tag type");
        appImport4.persistAndFlush();
        ApplicationImport appImport5 = new ApplicationImport();
        appImport5.setBusinessService("BS 2");
        appImport5.setDescription("and this");
        appImport5.setTag1("yes");
        appImport5.persistAndFlush();

        List<ApplicationImport> appList = new ArrayList();


        appList.add(appImport1);
        appList.add(appImport2);
        appList.add(appImport3);
        appList.add(appImport4);
        appList.add(appImport5);


        Long id1 = appImport1.id;
        Long id2 = appImport2.id;
        Long id3 = appImport3.id;
        Long id4 = appImport4.id;
        Long id5 = appImport5.id;

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
        svc.mapImportsToApplication(appList, tags, businessServices, parentId);


        userTransaction.commit();

        ApplicationImport refusedImport = ApplicationImport.findById(id1);
        assertEquals(Boolean.FALSE, refusedImport.getValid());
        ApplicationImport refusedImport2 = ApplicationImport.findById(id2);
        assertEquals(Boolean.FALSE, refusedImport2.getValid());
        ApplicationImport refusedImport3 = ApplicationImport.findById(id3);
        assertEquals(Boolean.FALSE, refusedImport3.getValid());
        ApplicationImport refusedImport4 = ApplicationImport.findById(id4);
        assertEquals(Boolean.FALSE, refusedImport4.getValid());
        ApplicationImport refusedImport5 = ApplicationImport.findById(id5);
        assertEquals(Boolean.FALSE, refusedImport4.getValid());

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(2)
    protected void testMultipartImport() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        ImportService svc = new ImportService();
        MultipartImportBody multipartImport = new MultipartImportBody();
        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());
        multipartImport.setFilename("testImport");
        multipartImport.setFile(importFile.toString());

        javax.ws.rs.core.Response response = svc.importFile(multipartImport);
        assertEquals(javax.ws.rs.core.Response.Status.OK.getStatusCode(),response.getStatus());





        userTransaction.commit();



        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(2)
    protected void testMapToApplicationMissingFields() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        ImportService svc = new ImportService();

        ApplicationImport appImportParent = new ApplicationImport();
        appImportParent.setBusinessService("BS 1");
        appImportParent.setDescription("hello");
        appImportParent.persistAndFlush();
        Long parentId = appImportParent.id;

        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setApplicationName("Test App 1");
        appImport1.persistAndFlush();
        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setApplicationName("Test App 2");
        appImport2.setBusinessService((""));
        appImport2.persistAndFlush();
        ApplicationImport appImport3= new ApplicationImport();
        appImport3.setApplicationName("Test App 3");
        appImport3.setBusinessService(("BS 2"));
        appImport3.persistAndFlush();
        ApplicationImport appImport4= new ApplicationImport();
        appImport4.setApplicationName("Test App 4");
        appImport4.setBusinessService(("BS 2"));
        appImport4.setDescription("");
        appImport4.persistAndFlush();


        List<ApplicationImport> appList = new ArrayList();


        appList.add(appImport1);
        appList.add(appImport2);
        appList.add(appImport3);
        appList.add(appImport4);


        Long id = appImport1.id;
        Long id2 = appImport2.id;
        Long id3 = appImport3.id;
        Long id4 = appImport4.id;

        Set<Tag> tags = new HashSet<>();
        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "BS 2";
        businessServices.add(businessService);
        svc.mapImportsToApplication(appList, tags, businessServices, parentId);


        userTransaction.commit();

        ApplicationImport refusedImport1 = ApplicationImport.findById(id);
        assertEquals(Boolean.FALSE, refusedImport1.getValid());
        assertEquals("Business Service is Mandatory",refusedImport1.getErrorMessage());

        ApplicationImport refusedImport2 = ApplicationImport.findById(id2);
        assertEquals(Boolean.FALSE, refusedImport2.getValid());
        assertEquals("Business Service is Mandatory",refusedImport2.getErrorMessage());

        ApplicationImport refusedImport3 = ApplicationImport.findById(id3);
        assertEquals(Boolean.FALSE, refusedImport3.getValid());
        assertEquals("Description is Mandatory",refusedImport3.getErrorMessage());

        ApplicationImport refusedImport4 = ApplicationImport.findById(id4);
        assertEquals(Boolean.FALSE, refusedImport4.getValid());
        assertEquals("Description is Mandatory",refusedImport4.getErrorMessage());

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(3)
    protected void testImportServiceNoMatchingTag() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {


        Set<Tag> tags = new HashSet<>() ;
        Tag.TagType tagType1 = new Tag.TagType();
        tagType1.id = "1";
        tagType1.name = "Unknown tag type";
        Tag tag = new Tag();
        tag.id = "1";
        tag.name = "Unknown OS";
        tag.tagType = tagType1;
        tags.add(tag);
        Mockito.when(mockTagService.getListOfTags()).thenReturn(tags);


        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "Foot2Go";
        businessServices.add(businessService);

        BusinessService businessService2 = new BusinessService();
        businessService2.id = "2";
        businessService2.name = "Food2Go";
        businessServices.add(businessService2);
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices()).thenReturn(businessServices);

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("sample_application_import.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();
    }

    @Test
    @Order(4)
    protected void testImportServiceDuplicatesInFile() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

        Set<Tag> tags = new HashSet<>() ;
        Tag.TagType tagType1 = new Tag.TagType();
        tagType1.id = "1";
        tagType1.name = "Operating System";
        Tag tag = new Tag();
        tag.id = "1";
        tag.name = "RHEL";
        tag.tagType = tagType1;
        tags.add(tag);
        Mockito.when(mockTagService.getListOfTags()).thenReturn(tags);


        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "Food2Go";
        businessServices.add(businessService);
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices()).thenReturn(businessServices);

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","duplicate_application_names.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());

        given()
                .accept("application/hal+json")
                .queryParam("isValid", Boolean.FALSE)
                .when()
                .get("/application-import")
                .then()
                .statusCode(200)
                .log().body()
                .body("_embedded.'application-import'[0].'errorMessage'", is("Duplicate Application Name within file: OrderHub"));

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(5)
    protected void testImportServiceNoTagsRetrieved() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {


        Mockito.when(mockTagService.getListOfTags()).thenReturn(null);


        Set<BusinessService> businessServices = new HashSet<>() ;
        BusinessService businessService = new BusinessService();
        businessService.id = "1";
        businessService.name = "Food2Go";
        businessServices.add(businessService);
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices()).thenReturn(businessServices);

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());



        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(5)
    protected void testImportServiceNoBSRetrieved() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {


        Mockito.when(mockTagService.getListOfTags()).thenReturn(null);


        Set<Tag> tags = new HashSet<>() ;
        Tag.TagType tagType1 = new Tag.TagType();
        tagType1.id = "1";
        tagType1.name = "Operating System";
        Tag tag = new Tag();
        tag.id = "1";
        tag.name = "RHEL";
        tag.tagType = tagType1;
        tags.add(tag);
        Mockito.when(mockTagService.getListOfTags()).thenReturn(tags);
        Mockito.when(mockBusinessServiceService.getListOfBusinessServices()).thenReturn(null);

        ClassLoader classLoader = getClass().getClassLoader();
        File importFile = new File(classLoader.getResource("duplicate_application_names.csv").getFile());


        Response response = given()
                .config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.JSON)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file",importFile)
                .multiPart("fileName","sample_application_import.csv")
                .when().post(PATH)
                .then()
                .log().all()
                .statusCode(200).extract().response();

        assertEquals(200, response.getStatusCode());



        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }


}

