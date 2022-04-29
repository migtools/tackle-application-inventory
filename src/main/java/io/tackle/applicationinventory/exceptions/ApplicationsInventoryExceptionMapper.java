/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.exceptions;

import io.quarkus.hibernate.orm.rest.data.panache.runtime.RestDataPanacheExceptionMapper;
import io.quarkus.rest.data.panache.RestDataPanacheException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.logging.Logger;
import org.postgresql.util.PSQLException;

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
 * Added PSQLException's management to provide a consistent error message when duplicate key constraints violations
 * must be reported to users.
 */
@Provider
@Priority(Priorities.USER)
public class ApplicationsInventoryExceptionMapper extends RestDataPanacheExceptionMapper implements ExceptionMapper<RestDataPanacheException> {
    private static final Logger LOGGER = Logger.getLogger(ApplicationsInventoryExceptionMapper.class);
    private static final String PSQL_EXCEPTION_DUPLICATE_KEY_PREFIX = "ERROR: duplicate key value violates unique constraint";

    @Override
    public Response toResponse(RestDataPanacheException exception) {
        if (exception.getCause() instanceof ApplicationsInventoryException) {
            ApplicationsInventoryException adce = (ApplicationsInventoryException) exception.getCause();
            LOGGER.warnf(adce, "Mapping an %s", ApplicationsInventoryException.class.getSimpleName());
            return Response.status(adce.getResponse().getStatus()).entity(Collections.singletonMap("errorMessage", adce.getMessage())).build();
        } else if (ExceptionUtils.getRootCause(exception) instanceof PSQLException) {
            final String psqlMessage = ExceptionUtils.getRootCause(exception).getMessage();
            // if and only if a "duplicate key" PSQLException is found
            // then a JSON payload with the 'errorMessage' field is sent in the response
            if (psqlMessage != null && psqlMessage.startsWith(PSQL_EXCEPTION_DUPLICATE_KEY_PREFIX)) {
                return Response.status(Response.Status.CONFLICT.getStatusCode()).entity(Collections.singletonMap("errorMessage", PSQL_EXCEPTION_DUPLICATE_KEY_PREFIX)).build();
            }
            // otherwise the "Quarkus approach" is adopted to send out the HTTP code
            return super.toResponse(exception);
        } else {
            return super.toResponse(exception);
        }
    }

}
