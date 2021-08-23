package io.tackle.applicationinventory.services;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import io.tackle.applicationinventory.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.quarkus.oidc.token.propagation.AccessToken;

import java.util.Set;

@RegisterRestClient
@AccessToken
@ApplicationScoped
public interface TagService {

    @GET
    @Path("/controls/tag")
    Set<Tag> getListOfTags(@QueryParam("page") int page, @QueryParam("size") int size);
}


