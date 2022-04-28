/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.services;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.MultipartImportBody;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ImportSummary;
import io.tackle.applicationinventory.mapper.ApplicationDependencyAPIMapper;
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
import javax.validation.Validator;
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
    public static final String APPLICATION_IMPORT_TYPE = "1";
    public static final String DEPENDENCY_IMPORT_TYPE = "2";

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

    @Inject
    Validator validator;

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

            List<ApplicationImport> importList = writeFile(data.getFile(), data.getFileName(), parentRecord);

            List<ApplicationImport> applicationTypeImports = importList.stream().filter(anImportRow ->
                    anImportRow.getRecordType1().equals(APPLICATION_IMPORT_TYPE)).collect(Collectors.toList());

            if(!applicationTypeImports.isEmpty()) {

                //Only check for tags and business services for Application (Type 1) Imports
                Set<Tag> tags = tagService.getListOfTags(0, 1000);
                if (tags == null) {
                    String msg = "Unable to retrieve TagTypes from remote resource";
                    parentRecord.errorMessage = msg;
                    throw new Exception(msg);
                }
                Set<BusinessService> businessServices = businessServiceService.getListOfBusinessServices(0, 1000);
                if (businessServices == null) {
                    String msg = "Unable to retrieve BusinessServices from remote resource";
                    parentRecord.errorMessage = msg;
                    throw new Exception(msg);
                }


                //we're not allowed duplicate application names within the file
                Set<String> discreteAppNames = new HashSet();
                //make a list of all the duplicate app names
                List<ApplicationImport> importListMinusDuplicates = applicationTypeImports;
                List<ApplicationImport> duplicateAppNames = applicationTypeImports.stream().filter(importApp ->
                        !discreteAppNames.add(importApp.getApplicationName())).collect(Collectors.toList());
                if (!duplicateAppNames.isEmpty()) {
                    //find all the imported apps with a duplicate name and set appropriate error message
                    duplicateAppNames.forEach(app -> {
                        applicationTypeImports.stream().filter(importApp ->
                                app.getApplicationName().equals(importApp.getApplicationName())).collect(Collectors.toList())
                                .forEach(duplicateApp -> {
                                    importListMinusDuplicates.remove(duplicateApp);
                                    duplicateApp.setErrorMessage("Duplicate Application Name within file: " + duplicateApp.getApplicationName());
                                    markFailedImportAsInvalid(duplicateApp);
                                });

                    });
                }
                mapImportsToApplication(importListMinusDuplicates, tags, businessServices, parentRecord);
            }

            List<ApplicationImport> dependencyTypeImports = importList.stream().filter(anImportRow ->
                    anImportRow.getRecordType1().equals(DEPENDENCY_IMPORT_TYPE)).collect(Collectors.toList());

            if(!dependencyTypeImports.isEmpty()) {
                    mapImportsToDependency(dependencyTypeImports, parentRecord);
            }

            List<ApplicationImport> noTypeImports = importList.stream().filter(anImportRow ->
                    !anImportRow.getRecordType1().equals(APPLICATION_IMPORT_TYPE)
                    && !anImportRow.getRecordType1().equals(DEPENDENCY_IMPORT_TYPE)).collect(Collectors.toList());

            noTypeImports.forEach(noTypeImport -> {
                noTypeImport.setErrorMessage("Invalid Record Type");
                markFailedImportAsInvalid(noTypeImport);
            });

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
        List<ApplicationImport> importList = new ArrayList<>();
        while (iter.hasNext())
        {
            ApplicationImport importedApplication = iter.next();

            ApplicationImport appToPersist;
            if (validator.validate(importedApplication).isEmpty()) {
                appToPersist = importedApplication;

                importList.add(appToPersist);
            } else {
                String truncatedAppName = StringUtils.truncate(importedApplication.getApplicationName().trim(), ApplicationImport.APP_NAME_MAX_LENGTH);;

                appToPersist = new ApplicationImport();
                appToPersist.setApplicationName(truncatedAppName);
                appToPersist.setValid(false);
                appToPersist.setErrorMessage("Max length error: one or more column's max length were exceeded");
            }

            appToPersist.setFilename(filename);
            appToPersist.importSummary = parentObject;
            appToPersist.persistAndFlush();
        }
        return importList;
    }



    private MappingIterator<ApplicationImport> decode(String inputFileContent) throws IOException{
        CsvMapper mapper = new CsvMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);

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

    public void mapImportsToDependency(List<ApplicationImport> importList,ImportSummary parentRecord)
    {
        ApplicationMapper dependencyMapper = new ApplicationDependencyAPIMapper();
        importList.forEach(importedApplication -> {
            Response response = dependencyMapper.map(importedApplication, parentRecord.id);
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




}
