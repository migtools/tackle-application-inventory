package io.tackle.applicationinventory.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.MultipartImportBody;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.applicationinventory.mapper.ApplicationInventoryAPIMapper;
import io.tackle.applicationinventory.mapper.ApplicationMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.REQUIRED;

@Path("/file")
@ApplicationScoped
public class ImportService {

    public static final String COMPLETED_STATUS = "Completed";
    public static final String IN_PROGRESS_STATUS = "In Progress";
    public static final String FAILED_STATUS = "Failed";

    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction usrTransaction;

    @Inject
    @RestClient
    TagService tagService;

    @Inject
    @RestClient
    BusinessServiceService businessServiceService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional(REQUIRED)
    public Response importFile(@MultipartForm MultipartImportBody data) {
        ImportSummary parentRecord = new ImportSummary();

        try {
            parentRecord.filename = data.getFileName();
            parentRecord.importStatus = IN_PROGRESS_STATUS;
            parentRecord.persistAndFlush();
            Set<Tag> tags = tagService.getListOfTags();
            if (tags == null)
            {
                String msg = "Unable to retrieve TagTypes from remote resource";
                parentRecord.errorMessage = msg;
                throw new Exception(msg);
            }
             Set<BusinessService> businessServices =businessServiceService.getListOfBusinessServices();
            if (businessServices == null)
            {
                String msg = "Unable to retrieve BusinessServices from remote resource";
                parentRecord.errorMessage = msg;
                throw new Exception(msg);
            }

            List<ApplicationImport> importList = writeFile(data.getFile(), data.getFileName(), parentRecord);
            //we're not allowed duplicate application names within the file
            Set<String> discreteAppNames = new HashSet();
            //make a list of all the duplicate app names
            List<ApplicationImport> importListMinusDuplicates = importList;
            List<ApplicationImport> duplicateAppNames = importList.stream().filter(importApp ->

                !discreteAppNames.add(minimiseWhitespace(importApp.getApplicationName()))).collect(Collectors.toList());
            if( !duplicateAppNames.isEmpty())
            {
                //find all the imported apps with a duplicate name and set appropriate error message
                duplicateAppNames.forEach(app -> {
                    importList.stream().filter(importApp ->
                            minimiseWhitespace(app.getApplicationName()).equals(minimiseWhitespace(importApp.getApplicationName()))).collect(Collectors.toList())
                            .forEach(duplicateApp -> {
                                        importListMinusDuplicates.remove(duplicateApp);
                                        duplicateApp.setErrorMessage("Duplicate Application Name within file: " + duplicateApp.getApplicationName());
                                        markFailedImportAsInvalid(duplicateApp);
                            });

                });
            }
            mapImportsToApplication(importListMinusDuplicates, tags, businessServices, parentRecord);
            parentRecord.importStatus = COMPLETED_STATUS;
            parentRecord.flush();

        } catch (Exception e) {

            e.printStackTrace();
            parentRecord.importStatus = FAILED_STATUS;
            parentRecord.flush();

        }
            return Response.ok().build();



    }


    private List<ApplicationImport> writeFile(String content, String filename, ImportSummary parentObject) throws IOException {

        MappingIterator<ApplicationImport> iter = decode(content);
        List<ApplicationImport> importList = new ArrayList();
        while (iter.hasNext())
        {
            ApplicationImport importedApplication = iter.next();
            importedApplication.setFilename(filename);
            importedApplication.importSummary = parentObject;
            importList.add(importedApplication);
            importedApplication.persistAndFlush();
        }
        return importList;
    }



    private MappingIterator<ApplicationImport> decode(String inputFileContent) throws IOException{
        CsvMapper mapper = new CsvMapper();

        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        String columnSeparator = ",";

        csvSchema = csvSchema.withColumnSeparator(columnSeparator.charAt(0))
                .withLineSeparator("\r\n")
                .withUseHeader(true);

        ObjectReader reader = mapper.readerFor(ApplicationImport.class)
                .with(csvSchema);

        return reader.readValues(inputFileContent);

    }

    public void mapImportsToApplication(List<ApplicationImport> importList, Set<Tag> tags, Set<BusinessService> businessServices, ImportSummary parentRecord)
    {
        ApplicationMapper mapper = new ApplicationInventoryAPIMapper(tags, businessServices);
        importList.forEach(importedApplication -> {
            Response response = mapper.map(importedApplication, parentRecord.id);
            if (response.getStatus() != Response.Status.OK.getStatusCode())
            {
                markFailedImportAsInvalid(importedApplication);
            }

        });
    }

    private void markFailedImportAsInvalid(ApplicationImport importFile)
    {
        importFile.setValid(Boolean.FALSE);
        importFile.flush();
    }

    public static String minimiseWhitespace(String input)
    {
        StringBuffer whitespaceMinimized = new StringBuffer();
        String [] array = StringUtils.split(input);
        Arrays.asList(array).forEach(name -> whitespaceMinimized.append(name).append(" "));
        return whitespaceMinimized.toString().strip();
    }




}
