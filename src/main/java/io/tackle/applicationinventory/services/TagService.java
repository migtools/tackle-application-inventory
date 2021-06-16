package io.tackle.applicationinventory.services;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;

import io.tackle.applicationinventory.Tag;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.quarkus.oidc.token.propagation.AccessToken;

import java.util.Set;



@RegisterRestClient()
@AccessToken
@ApplicationScoped
public interface TagService {

    @GET
    Set<Tag> getListOfTags();
}


