package io.tackle.applicationinventory.resources;

import io.tackle.applicationinventory.entities.Stakeholder;
import io.tackle.applicationinventory.entities.StakeholderGroup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.links.LinkResource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("stakeholder")
public class StakeholderUpdateResource {

    @Inject
    Logger logger;
/*
    These are the synthetic methods create from Quarkus REST Panache.
    They manage the 'PUT' verb both for creating and updating Stakeholder.
    In this implementation I've removed the creation part to let it be done using the 'POST' verb.

    @Path("/{id}")
    @Transactional
    @PUT
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.Stakeholder",
            rel = "update"
    )
    public Response update(@PathParam("id") Long var1, Stakeholder var2) {
        StakeholderResourceImpl_cdf008bf94ad72ddbf067ca9cadc332df33146e6 var3 = this.resource;
        if (var3.get(var1) == null) {
            Object var4 = var3.update(var1, var2);
            String var5 = (new ResourceLinksProvider()).getSelfLink(var4);
            if (var5 != null) {
                URI var7 = URI.create(var5);
                Response.ResponseBuilder var6 = Response.status(201);
                var6.entity(var4);
                var6.location(var7);
                return var6.build();
            } else {
                throw (Throwable)(new RuntimeException("Could not extract a new entity URL"));
            }
        } else {
            var3.update(var1, var2);
            return Response.status(204).build();
        }
    }

    @Path("/{id}")
    @Transactional
    @PUT
    @Consumes({"application/json"})
    @Produces({"application/hal+json"})
    public Response updateHal(@PathParam("id") Long var1, Stakeholder var2) {
        StakeholderResourceImpl_cdf008bf94ad72ddbf067ca9cadc332df33146e6 var3 = this.resource;
        if (var3.get(var1) == null) {
            Object var4 = var3.update(var1, var2);
            HalEntityWrapper var6 = new HalEntityWrapper(var4);
            String var5 = (new ResourceLinksProvider()).getSelfLink(var4);
            if (var5 != null) {
                URI var8 = URI.create(var5);
                Response.ResponseBuilder var7 = Response.status(201);
                var7.entity(var6);
                var7.location(var8);
                return var7.build();
            } else {
                throw (Throwable)(new RuntimeException("Could not extract a new entity URL"));
            }
        } else {
            var3.update(var1, var2);
            return Response.status(204).build();
        }
    }
*/

    @Path("/{id}")
    @Transactional
    @PUT
    @Consumes({"application/json"})
    @Produces({"application/json", "application/hal+json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.Stakeholder",
            rel = "update"
    )
    public Response update(@PathParam("id") Long id, Stakeholder stakeholder) {
        stakeholder.id = id;
        // update the many-to-many StakeholderGroup relation from the Stakeholder side, the non-owning one
        logger.debugf("Load list of current Stakeholder groups for %s", stakeholder);
        List<StakeholderGroup> currentStakeholderGroups = StakeholderGroup.list("SELECT table FROM StakeholderGroup table JOIN table.stakeholders stakeholder WHERE stakeholder.id = ?1", id);
        logger.debugf("Loaded %d Stakeholder groups for %s from DB", currentStakeholderGroups.size(), stakeholder);
        currentStakeholderGroups.forEach(currentStakeholderGroup -> {
            logger.debugf("Is %s still a stakeholder group for %s in the request?", currentStakeholderGroup, stakeholder);
            if (!stakeholder.stakeholderGroups.contains(currentStakeholderGroup)) {
                logger.debugf("No hence remove %s from stakeholders for %s", stakeholder, currentStakeholderGroup);
                currentStakeholderGroup.stakeholders.remove(stakeholder);
                logger.debugf("%s removed", stakeholder);
            }
            else {
                logger.debugf("Yes, no need to remove %s from %s", stakeholder, currentStakeholderGroup);
            }
        });

        // once https://github.com/quarkusio/quarkus/issues/15961 will be fixed, this should become useless
        Stakeholder.update("jobFunction = ?1 where id = ?2", stakeholder.jobFunction, id);

        stakeholder.getEntityManager().merge(stakeholder);
        return Response.status(NO_CONTENT).build();
    }
}
