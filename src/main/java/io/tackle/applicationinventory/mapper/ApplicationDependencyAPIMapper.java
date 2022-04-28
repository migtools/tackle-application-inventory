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
package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationImport;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.exceptions.ApplicationsInventoryException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

@ApplicationScoped
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

        ApplicationsDependency dependency = new ApplicationsDependency();

        if (importApp.getDependencyDirection() != null && importApp.getDependencyDirection().equalsIgnoreCase(FROM_DIRECTION))
        {
            dependency.from = application;
            dependency.to = applicationDependency;
        }
        else if (importApp.getDependencyDirection() != null && importApp.getDependencyDirection().equalsIgnoreCase(TO_DIRECTION))
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

        try{
            ApplicationsDependency.validate(dependency.from, dependency.to);
        }catch(ApplicationsInventoryException aie)
        {
            importApp.setErrorMessage(aie.getMessage());
            return aie.getResponse();
        }

        dependency.persistAndFlush();

        return Response.ok().build();
    }
}
