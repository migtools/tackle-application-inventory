package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.TagType;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import java.util.*;


public class ApplicationInventoryAPIMapper extends ApplicationMapper{

    public ApplicationInventoryAPIMapper( Set<TagType> tagTypes, Set<BusinessService> businessServices) {
        super(tagTypes, businessServices);
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
            if (importApp.getTagType1() != null && importApp.getTag1() != null) {
                tags.add(addTag(importApp.getTag1(), importApp.getTagType1()));
            }
            //increment so potential error message refers to correct tag
            currentTag = importApp.getTag2();
            currentTagType = importApp.getTagType2();
            if (importApp.getTagType2() != null && importApp.getTag2() != null) {
                tags.add(addTag(importApp.getTag2(), importApp.getTagType2()));
            }
            currentTag = importApp.getTag3();
            currentTagType = importApp.getTagType3();
            if (importApp.getTagType3() != null && importApp.getTag3() != null) {

                tags.add(addTag(importApp.getTag3(), importApp.getTagType3()));
            }
            currentTag = importApp.getTag4();
            currentTagType = importApp.getTagType4();
            if (importApp.getTagType4() != null && importApp.getTag4() != null) {
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
        Optional<TagType> tagTypeOptional = tagTypes.stream().filter(tagTypeControls -> tagTypeControls.name.equals(tagTypeName))
                .findFirst();
        tagTypeOptional.orElseThrow();
        Optional<TagType.Tag> tagOptional = tagTypeOptional.get().tags.stream().filter(tagControls -> tagControls.name.equals(tagName))
                .findFirst();

        return tagOptional.orElseThrow().id;
    }
}
