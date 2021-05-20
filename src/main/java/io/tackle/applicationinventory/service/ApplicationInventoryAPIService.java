package io.tackle.applicationinventory.service;

import io.tackle.applicationinventory.entities.Application;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/v2")
public interface ApplicationInventoryAPIService {

    @POST
    Response submitApplication(@PathParam Application app);
}
