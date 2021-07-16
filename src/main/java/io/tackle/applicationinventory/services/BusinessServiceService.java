package io.tackle.applicationinventory.services;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import io.quarkus.oidc.token.propagation.AccessToken;
import io.tackle.applicationinventory.BusinessService;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Set;



@RegisterRestClient(baseUri = "http://tackle-controls:8080")
@AccessToken
@ApplicationScoped
public interface BusinessServiceService {

    @GET
    @Path("/controls/business-service")
    @QueryParam("page=0&size=1000")
    Set<BusinessService> getListOfBusinessServices();
}
