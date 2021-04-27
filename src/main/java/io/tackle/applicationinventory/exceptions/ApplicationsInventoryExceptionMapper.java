package io.tackle.applicationinventory.exceptions;

import io.quarkus.hibernate.orm.rest.data.panache.runtime.RestDataPanacheExceptionMapper;
import io.quarkus.rest.data.panache.RestDataPanacheException;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

/**
 * {@link ExceptionMapper} created to provide a customized message for {@link ApplicationsInventoryException}
 * when REST method implementations come from {@link io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource}
 * and throws {@link RestDataPanacheException}
 */
@Provider
@Priority(Priorities.USER)
public class ApplicationsInventoryExceptionMapper extends RestDataPanacheExceptionMapper implements ExceptionMapper<RestDataPanacheException> {
    private static final Logger LOGGER = Logger.getLogger(ApplicationsInventoryExceptionMapper.class);

    @Override
    public Response toResponse(RestDataPanacheException exception) {
        if (exception.getCause() instanceof ApplicationsInventoryException) {
            ApplicationsInventoryException adce = (ApplicationsInventoryException) exception.getCause();
            LOGGER.warnf(adce, "Mapping an %s", ApplicationsInventoryException.class.getSimpleName());
            return Response.status(adce.getResponse().getStatus()).entity(Collections.singletonMap("errorMessage", adce.getMessage())).build();
        } else {
            return super.toResponse(exception);
        }
    }

}
