package io.tackle.applicationinventory.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.MultipartImportBody;
import io.tackle.applicationinventory.TagType;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.exceptions.ApplicationsInventoryException;
import io.tackle.applicationinventory.mapper.ApplicationInventoryAPIMapper;
import io.tackle.applicationinventory.mapper.ApplicationMapper;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.transaction.Transactional.TxType.REQUIRED;

@Path("/file")
@ApplicationScoped
public class ImportService {

    @Inject
    @RestClient
    TagTypeService tagTypeService;

    @Inject
    @RestClient
    BusinessServiceService businessServiceService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional(REQUIRED)
    public Response importFile(@MultipartForm MultipartImportBody data) throws Exception {
        try {

            System.out.println("File: " + data.getFile());
            System.out.println("FileName: " + data.getFileName());

            Set<TagType> tagTypes =tagTypeService.getListOfTagTypes();
            if (tagTypes == null)
            {
                throw new Exception("Unable to connect to remote resource to retrieve TagTypes");
            }
            Set<BusinessService> businessServices =businessServiceService.getListOfBusinessServices();
            if (businessServices == null)
            {
                throw new Exception("Unable to connect to remote resource to retrieve BusinessServices");
            }

            List<ApplicationImport> importList = writeFile(data.getFile(), data.getFileName());
            mapImportsToApplication(importList, tagTypes, businessServices);
        } catch (Exception e) {

            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().build();
    }


    private List<ApplicationImport> writeFile(String content, String filename) throws IOException {

        MappingIterator<ApplicationImport> iter = decode(content);
        List<ApplicationImport> importList = new ArrayList();
        System.out.println("Printing csv fields");
        while (iter.hasNext())
        {
            ApplicationImport importedApplication = iter.next();
            System.out.println(importedApplication);
            importList.add(importedApplication);
            importedApplication.persistAndFlush();
        }
        return importList;
    }



    private MappingIterator<ApplicationImport> decode(String inputContent) {
        try {
           String inputFileContent = getFilePortionOfMessage(inputContent);

            CsvMapper mapper = new CsvMapper();

            CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
            String columnSeparator = ",";

            csvSchema = csvSchema.withColumnSeparator(columnSeparator.charAt(0))
                                .withLineSeparator("\r\n")
                                .withUseHeader(true);

            ObjectReader reader = mapper.readerFor(ApplicationImport.class)
                    .with(csvSchema);

            return reader.readValues(inputFileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePortionOfMessage(String content)
    {
        try
        {
            System.out.println("Input Message:" + content);

            ObjectNode node = new ObjectMapper().readValue(content, ObjectNode.class);
            String fileContent = node.get("file").asText();

            System.out.println("File Portion Of Message:" + fileContent);
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mapImportsToApplication(List<ApplicationImport> importList, Set<TagType> tagTypes, Set<BusinessService> businessServices)
    {
        ApplicationMapper mapper = new ApplicationInventoryAPIMapper(tagTypes, businessServices);
        importList.forEach(importedApplication -> {
            System.out.println("Mapping :" + importedApplication.id);
            Response response = mapper.map(importedApplication);
            System.out.println("Response Status :" + response.getStatus());
            if (response.getStatus() != Response.Status.OK.getStatusCode())
            {
                markFailedImportAsInvalid(importedApplication);
                System.out.println(importedApplication.id + " Import Mapping Failed");
            }
        });
    }

    private void markFailedImportAsInvalid(ApplicationImport importFile)
    {
        importFile.setValid(Boolean.FALSE);
        importFile.persistAndFlush();
    }




}
