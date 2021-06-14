package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;


public class ApplicationInventoryAPIMapper extends ApplicationMapper{

    public ApplicationInventoryAPIMapper( Set<Tag> tags, Set<BusinessService> businessServices) {
        super(tags, businessServices);
    }

    @Override
    public Response map(ApplicationImport importApp)
    {
        System.out.println("Call to Mapper");

        Application newApp = new Application();
        Set<String> tags = new HashSet<>();

        try {
            if (importApp.getBusinessService() != null && !importApp.getBusinessService().isEmpty()) {
                newApp.businessService = addBusinessService(importApp.getBusinessService());
            } else {
                importApp.setErrorMessage("Business Service is Mandatory");
                return Response.serverError().build();
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
        } else {
            importApp.setErrorMessage("Description is Mandatory");
            return Response.serverError().build();
        }


        newApp.name = importApp.getApplicationName();
        String currentTag = importApp.getTag1();
        String currentTagType = importApp.getTagType1();
        try{
            if (importApp.getTagType1() != null && !importApp.getTagType1().isEmpty()
                    && importApp.getTag1() != null && !importApp.getTag1().isEmpty()) {
                tags.add(addTag(importApp.getTag1(), importApp.getTagType1()));
            }
            //update so potential error message refers to correct tag
            currentTag = importApp.getTag2();
            currentTagType = importApp.getTagType2();
            if (importApp.getTagType2() != null && !importApp.getTagType2().isEmpty()
                    && importApp.getTag2() != null && !importApp.getTag2().isEmpty()) {
                tags.add(addTag(importApp.getTag2(), importApp.getTagType2()));
            }
            currentTag = importApp.getTag3();
            currentTagType = importApp.getTagType3();
            if (importApp.getTagType3() != null && !importApp.getTagType3().isEmpty()
                    && importApp.getTag3() != null && !importApp.getTag3().isEmpty()) {

                tags.add(addTag(importApp.getTag3(), importApp.getTagType3()));
            }
            currentTag = importApp.getTag4();
            currentTagType = importApp.getTagType4();
            if (importApp.getTagType4() != null && !importApp.getTagType4().isEmpty()
                    && importApp.getTag4() != null && !importApp.getTag4().isEmpty()) {
                tags.add(addTag(importApp.getTag4(), importApp.getTagType4()));
            }
        }
        catch(NoSuchElementException nsee3)
        {
            nsee3.printStackTrace();
            importApp.setErrorMessage("Tag Type " + currentTagType + " and Tag " + currentTag + " combination does not exist");
            return Response.serverError().build();
        }

        newApp.tags = tags;
        newApp.persistAndFlush();
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
