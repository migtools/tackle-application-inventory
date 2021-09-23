package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.exceptions.ApplicationsInventoryException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;

import java.util.Set;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

public class ApplicationDependencyAPIMapper extends ApplicationMapper {

    @Inject
    Validator validator;

    private static final String FROM_DIRECTION = "SOUTHBOUND";
    private static final String TO_DIRECTION = "NORTHBOUND";

    public ApplicationDependencyAPIMapper() {
        super(null, null);
    }

    @Override
    @Transactional(REQUIRES_NEW)
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

        if (applicationDependency.equals(application))
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


        System.out.println("Validating application: " + dependency.from + ", dependency: " + dependency.to);
        System.out.println("Validator instantiated: " + validator);

        Set<ConstraintViolation<ApplicationsDependency>> constraintViolations = validator.validate( dependency );

        if (constraintViolations.size() > 0)
        {
            importApp.setErrorMessage(constraintViolations.iterator().next().getMessage());
            System.out.println(constraintViolations.iterator().next().getMessage());
            return Response.serverError().build();
        }

        try {
            dependency.persistAndFlush();
        }
        catch(ApplicationsInventoryException aie)
        {
            importApp.setErrorMessage("Dependency cycle would be created");
            return Response.serverError().build();
        }
        System.out.println("Success for application: " + dependency.from + ", dependency: " + dependency.to);
        return Response.ok().build();
    }
}
