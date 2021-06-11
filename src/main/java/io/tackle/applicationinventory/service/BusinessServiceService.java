package io.tackle.applicationinventory.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.tackle.applicationinventory.BusinessService;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Set;



@RegisterRestClient()
@ApplicationScoped
public interface BusinessServiceService {

    @GET
    @Path("?page=0&size=1000&sort=name")
    Set<BusinessService> getListOfBusinessServices();
}
