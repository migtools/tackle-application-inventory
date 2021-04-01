package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.entities.ApplicationsDependency;
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

@Path("applications-dependency")
public class ApplicationsDependencyListFilteredResource implements ListFilteredResource<ApplicationsDependency> {

    @Override
    public Class<ApplicationsDependency> getPanacheEntityType() {
        return ApplicationsDependency.class;
    }

    @GET
    @Path("")
    @Produces({"application/json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.ApplicationsDependency",
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
