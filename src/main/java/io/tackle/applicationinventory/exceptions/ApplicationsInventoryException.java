package io.tackle.applicationinventory.exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class ApplicationsInventoryException extends ClientErrorException {

    public ApplicationsInventoryException(final String message, final Response.Status status) {
        super(message, status);
    }
}
