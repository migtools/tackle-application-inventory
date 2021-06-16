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
        assertEquals(7, ApplicationImport.listAll().size());

        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }

    @Test
    @Order(2)
    protected void testMapToApplicationRejected() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        ImportService svc = new ImportService();

        ApplicationImport appImport1 = new ApplicationImport();
        appImport1.setBusinessService("BS 1");
        appImport1.persistAndFlush();
        ApplicationImport appImport2 = new ApplicationImport();
        appImport2.setBusinessService("BS 2");
        appImport2.setTag1("tag 1");
        appImport2.setTagType1("tag type 1");
        appImport2.persistAndFlush();
        ApplicationImport appImport3 = new ApplicationImport();
        appImport3.setBusinessService("BS 3");
        appImport3.persistAndFlush();

        List<ApplicationImport> appList = new ArrayList();


        appList.add(appImport1);
        appList.add(appImport2);
        appList.add(appImport3);


        Long id = appImport1.id;
        System.out.println("appImport1.id= " + id);

        Set<Tag> tags = new HashSet<>();
        Set<BusinessService> businessServices = new HashSet<>();
        svc.mapImportsToApplication(appList, tags, businessServices);


        userTransaction.commit();

        ApplicationImport refusedImport = ApplicationImport.findById(id);
        assertEquals(Boolean.FALSE, refusedImport.getValid());

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
                .statusCode(500).extract().response();

        assertEquals(500, response.getStatusCode());

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
                .statusCode(500).extract().response();

        assertEquals(500, response.getStatusCode());



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
                .statusCode(500).extract().response();

        assertEquals(500, response.getStatusCode());



        userTransaction.begin();
        ApplicationImport.deleteAll();
        userTransaction.commit();

    }


}

