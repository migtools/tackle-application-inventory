package io.tackle.applicationinventory.services;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;

import io.quarkus.oidc.token.propagation.AccessToken;
import io.tackle.applicationinventory.BusinessService;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Set;



@RegisterRestClient()
@AccessToken
@ApplicationScoped
public interface BusinessServiceService {

    @GET
    Set<BusinessService> getListOfBusinessServices();
}
