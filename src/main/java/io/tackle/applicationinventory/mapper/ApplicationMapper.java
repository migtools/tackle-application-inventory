package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.Set;

public abstract class ApplicationMapper {
    @ApplicationScoped
    Set<Tag> tags;
    @ApplicationScoped
    Set<BusinessService> businessServices;

    public ApplicationMapper(Set<Tag> tags, Set<BusinessService> businessServices)
    {
        this.tags = tags;
        this.businessServices = businessServices;
    }

    public abstract Response map(ApplicationImport importApp);
}
