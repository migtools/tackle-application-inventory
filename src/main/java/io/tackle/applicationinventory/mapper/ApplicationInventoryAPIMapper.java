package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.services.ImportService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public class ApplicationInventoryAPIMapper extends ApplicationMapper{

    public ApplicationInventoryAPIMapper( Set<Tag> tags, Set<BusinessService> businessServices) {
        super(tags, businessServices);
    }

    @Override
    public Response map(ApplicationImport importApp, Long parentId)
    {
        Application newApp = new Application();
        Set<String> tags = new HashSet<>();


        if (importApp.getApplicationName() == null || importApp.getApplicationName().strip().isEmpty()) {
            importApp.setErrorMessage("Application Name is mandatory");
            return Response.serverError().build();
        }

        try {
            if (importApp.getBusinessService() != null && !importApp.getBusinessService().isEmpty()) {
                newApp.businessService = addBusinessService(importApp.getBusinessService());
            }

        }
        catch(NoSuchElementException nsee1){
            nsee1.printStackTrace();
            importApp.setErrorMessage("Business Service: " + importApp.getBusinessService() + " does not exist");
            return Response.serverError().build();
        }
        newApp.comments = importApp.getComments();

        if (importApp.getDescription() != null && !importApp.getDescription().isEmpty()) {
            newApp.description = importApp.getDescription();
        }
        String whitespaceMinimized = ImportService.minimiseWhitespace(importApp.getApplicationName());

        // check for duplicates on table
        long count = Application.count("name",whitespaceMinimized);
        if(count>0)
        {
            importApp.setErrorMessage("Duplicate ApplicationName in table: " + importApp.getApplicationName());
            return Response.serverError().build();
        }
        newApp.name = whitespaceMinimized;
        String currentTag = "";
        String currentTagType = "";
        try{
                for(int i=1;i<=20;i++)
                {
                    String tagMethodName = "getTag" + i;
                    String tagTypeMethodName = "getTagType" + i;
                    java.lang.reflect.Method tagMethod;
                    java.lang.reflect.Method tagTypeMethod;
                        tagMethod = importApp.getClass().getMethod(tagMethodName);
                        tagTypeMethod = importApp.getClass().getMethod(tagTypeMethodName);
                        currentTag = (String)tagMethod.invoke(importApp);
                        currentTagType = (String)tagTypeMethod.invoke(importApp);
                        if ((currentTag == null || currentTag.isEmpty())
                                && (currentTagType == null || currentTagType.isEmpty())) {
                            //don't validate and add tag/tagtype if both aren't present
                        }
                        else{
                            tags.add(addTag(currentTag, currentTagType));
                        }

                }

        }
        catch(NoSuchElementException nsee3)
        {
            nsee3.printStackTrace();
            importApp.setErrorMessage("Tag Type " + currentTagType + " and Tag " + currentTag + " combination does not exist");
            return Response.serverError().build();
        }
        catch (SecurityException |NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
            importApp.setErrorMessage("Tag Type " + currentTagType + " and Tag " + currentTag + " unable to perform validation");
            return Response.serverError().build();
        }
  //      try {
            newApp.tags = tags;
            newApp.persistAndFlush();
  /**      }
        catch(Exception e){
            e.printStackTrace();
            importApp.setErrorMessage("Duplicate ApplicationName in table: " + importApp.getApplicationName());
            return Response.serverError().build();
        }*/
        return Response.ok().build();
    }


    private String addBusinessService(String businessServiceName) throws NoSuchElementException
    {
        Optional<BusinessService> businessServiceOptional = businessServices.stream().filter(businessServiceControls -> businessServiceControls.name.equals(businessServiceName))
                        .findFirst();


        return businessServiceOptional.orElseThrow().id;

    }

    private String addTag(String tagName, String tagTypeName) throws NoSuchElementException
    {
        List<Tag> tagList = tags.stream().filter(controlsTag -> controlsTag.name.equals(tagName)).collect(Collectors.toList());

        Optional<Tag> tagOptional = tagList.stream().filter(tagControls -> tagControls.tagType.name.equals(tagTypeName))
                .findFirst();

        return tagOptional.orElseThrow().id;
    }
}
