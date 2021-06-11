package io.tackle.applicationinventory.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.tackle.applicationinventory.TagType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.quarkus.oidc.token.propagation.AccessToken;

import java.util.Set;



@RegisterRestClient()
@AccessToken
@ApplicationScoped
public interface TagTypeService {

    @GET
    @Path("?page=0&size=1000&sort=rank")
    Set<TagType> getListOfTagTypes();
}


