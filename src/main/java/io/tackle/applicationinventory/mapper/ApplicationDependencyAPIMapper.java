package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.BusinessService;
import io.tackle.applicationinventory.Tag;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ApplicationsDependency;

import javax.ws.rs.core.Response;
import java.util.Set;

public class ApplicationDependencyAPIMapper extends ApplicationMapper {

    private static final String FROM_DIRECTION = "SOUTHBOUND";
    private static final String TO_DIRECTION = "NORTHBOUND";

    public ApplicationDependencyAPIMapper() {
        super(null, null);
    }

    @Override
    public Response map(ApplicationImport importApp, Long parentId)
    {
        Application application = null;
        Application applicationDependency = null;
        if (importApp.getApplicationName() != null)
        {
            application = Application.find("name", importApp.getApplicationName()).firstResult();
        }

        if (application == null)
        {
            importApp.setErrorMessage("Invalid Application Name");
            return Response.serverError().build();
        }

        if (importApp.getDependency() != null)
        {
            applicationDependency = Application.find("name", importApp.getDependency()).firstResult();
        }

        if (applicationDependency == null)
        {
            importApp.setErrorMessage("Invalid Dependency");
            return Response.serverError().build();
        }

        if (applicationDependency == application)
        {
            importApp.setErrorMessage("Application cannot be a dependency of itself");
            return Response.serverError().build();
        }




        ApplicationsDependency dependency = new ApplicationsDependency();

        if (importApp.getDependencyDirection().equalsIgnoreCase(FROM_DIRECTION))
        {
            dependency.from = application;
            dependency.to = applicationDependency;
        }
        else if (importApp.getDependencyDirection().equalsIgnoreCase(TO_DIRECTION))
        {
            dependency.from = applicationDependency;
            dependency.to = application;
        }
        else
        {
            importApp.setErrorMessage("Invalid Dependency Direction");
            return Response.serverError().build();
        }

        ApplicationsDependency found = ApplicationsDependency.find("to_id = ?1 and from_id = ?2", dependency.to.id, dependency.from.id).firstResult();

        if(found != null)
        {
            importApp.setErrorMessage("Dependency already exists");
            return Response.serverError().build();
        }

        dependency.persistAndFlush();
        return Response.ok().build();
    }
}
