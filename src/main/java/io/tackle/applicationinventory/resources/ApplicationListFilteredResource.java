/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
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
package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.entities.Application;
import io.tackle.commons.resources.ListFilteredResource;
import org.jboss.resteasy.links.LinkResource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("application")
public class ApplicationListFilteredResource implements ListFilteredResource<Application> {

    @Override
    public Class<Application> getPanacheEntityType() {
        return Application.class;
    }

    @GET
    @Path("")
    @Produces({"application/json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.Application",
            rel = "list"
    )
    public Response list(@QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) List var1,
                         @QueryParam(QUERY_PARAM_PAGE) @DefaultValue(DEFAULT_VALUE_PAGE) int var2,
                         @QueryParam(QUERY_PARAM_SIZE) @DefaultValue(DEFAULT_VALUE_SIZE) int var3,
                         @Context UriInfo var4) throws Exception {
        return ListFilteredResource.super.list(var1, var2, var3, var4, false);
    }

    @Path("")
    @GET
    @Produces({"application/hal+json"})
    public Response listHal(@QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) List var1,
                            @QueryParam(QUERY_PARAM_PAGE) @DefaultValue(DEFAULT_VALUE_PAGE) int var2,
                            @QueryParam(QUERY_PARAM_SIZE) @DefaultValue(DEFAULT_VALUE_SIZE) int var3,
                            @Context UriInfo var4) throws Exception {
        return ListFilteredResource.super.list(var1, var2, var3, var4, true);
    }
}
