package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ApplicationsDependency;

import javax.ws.rs.core.Response;
import java.util.Set;

public class ApplicationDependencyAPIMapper extends ApplicationMapper {

    private static final String FROM_DIRECTION = "FROM";
    private static final String TO_DIRECTION = "TO";

    public ApplicationDependencyAPIMapper(Set<Tag> tags, Set<BusinessService> businessServices) {
        super(tags, businessServices);
    }

    @Override
    public Response map(ApplicationImport importApp, Long parentId)
    {
        ApplicationsDependency dependency = new ApplicationsDependency();

        Application application = Application.find("name", importApp.getApplicationName()).firstResult();
        Application applicationDependency = Application.find("name", importApp.getDependency()).firstResult();
        if (importApp.getDependencyDirection().equals(FROM_DIRECTION))
        {
            dependency.from = application;
            dependency.to = applicationDependency;
        }
        else if (importApp.getDependencyDirection().equals(TO_DIRECTION))
        {
            dependency.from = applicationDependency;
            dependency.to = application;
        }
        else
        {
            importApp.setErrorMessage("Invalid Dependency Direction");
            return Response.serverError().build();
        }
        dependency.persistAndFlush();
        return Response.ok().build();
    }
}
