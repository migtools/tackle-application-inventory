package io.tackle.applicationinventory.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * ExceptionMapper created to provide a customized message for WebApplicationException
 * when REST method implementations come from PanacheEntityResource.
 */
@Provider
public class ResourcesExceptionHandler implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        return Response.status(exception.getResponse().getStatus()).entity(exception.getMessage()).build();
    }
}
