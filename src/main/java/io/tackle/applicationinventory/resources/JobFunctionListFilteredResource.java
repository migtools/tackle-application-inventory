package io.tackle.applicationinventory.resources;

import io.tackle.commons.resources.ListFilteredResource;
import io.tackle.applicationinventory.entities.JobFunction;
import org.jboss.resteasy.links.LinkResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("job-function")
public class JobFunctionListFilteredResource implements ListFilteredResource<JobFunction> {

    @Override
    public Class<JobFunction> getPanacheEntityType() {
        return JobFunction.class;
    }

    /**
     * workaround to have pagination and sorting as in REST Data Panache with also filtering.
     * <p>
     * The methods are copied from the `BusinessServiceResourceJaxRs_*` class created from REST Data Panache
     * at build time and "enhanced" here to manage also filtering.
     * <p>
     * This must be improved.
     */

    @GET
    @Path("")
    @Produces({"application/json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.JobFunction",
            rel = "list"
    )
    public Response list(@QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) List var1,
                         @QueryParam(QUERY_PARAM_PAGE) @DefaultValue(DEFAULT_VALUE_PAGE) int var2,
                         @QueryParam(QUERY_PARAM_SIZE) @DefaultValue(DEFAULT_VALUE_SIZE) int var3,
                         @Context UriInfo var4) throws Exception {
        return ListFilteredResource.super.list(var1, var2, var3, var4, false);
    }

    // reported because HAL implementation was not able to find inherited @Path
    // in https://github.com/resteasy/Resteasy/blob/8e20aa272c828ebdf2ba5d0c874f5eb655029b87/resteasy-core/src/main/java/org/jboss/resteasy/specimpl/ResteasyUriBuilderImpl.java#L374
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
