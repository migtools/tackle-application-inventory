package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.TagType;
import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.Set;

public abstract class ApplicationMapper {
    @ApplicationScoped
    Set<TagType> tagTypes;
    @ApplicationScoped
    Set<BusinessService> businessServices;

    public ApplicationMapper(Set<TagType> tagTypes, Set<BusinessService> businessServices)
    {
        this.tagTypes = tagTypes;
        this.businessServices = businessServices;
    }

    public abstract Response map(ApplicationImport importApp);
}
