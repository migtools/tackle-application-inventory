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
            newApp.businessService = addBusinessService(importApp.getBusinessService());
            newApp.comments = importApp.getComments();
            newApp.description = importApp.getDescription();
            newApp.name = importApp.getApplicationName();
            if (importApp.getTagType1() != null && importApp.getTag1() != null) {
                tags.add(addTag(importApp.getTag1(), importApp.getTagType1()));
            }
            if (importApp.getTagType2() != null && importApp.getTag2() != null) {
                tags.add(addTag(importApp.getTag2(), importApp.getTagType2()));
            }
            if (importApp.getTagType3() != null && importApp.getTag3() != null) {
                tags.add(addTag(importApp.getTag3(), importApp.getTagType3()));
            }
            if (importApp.getTagType4() != null && importApp.getTag4() != null) {
                tags.add(addTag(importApp.getTag4(), importApp.getTagType4()));
            }
        }
        catch(NoSuchElementException nsee)
        {
            nsee.printStackTrace();
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
        if(!tagTypeOptional.isPresent())
        {
            return null;
        }
        Optional<TagType.Tag> tagOptional = tagTypeOptional.get().tags.stream().filter(tagControls -> tagControls.name.equals(tagName))
                .findFirst();

        return tagOptional.orElseThrow().id;
    }
}
