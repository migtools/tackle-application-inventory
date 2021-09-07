package io.tackle.applicationinventory.exceptions;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ArcUndeclaredThrowableExceptionMapperTest {

    @Test
    public void testExceptionWithNullCause() {
        final ArcUndeclaredThrowableExceptionMapper mapper = new ArcUndeclaredThrowableExceptionMapper();
        final ArcUndeclaredThrowableException exception = new ArcUndeclaredThrowableException();
        assertEquals(500, mapper.toResponse(exception).getStatus());
    }

    @Test
    public void testExceptionWithGenericCause() {
        final ArcUndeclaredThrowableExceptionMapper mapper = new ArcUndeclaredThrowableExceptionMapper();
        final Throwable cause = new Throwable();
        final ArcUndeclaredThrowableException exception = new ArcUndeclaredThrowableException(cause);
        assertEquals(500, mapper.toResponse(exception).getStatus());
    }

    @Test
    public void testPSQLExceptionWithoutMessage() {
        final PSQLException psqlException = new PSQLException(null, PSQLState.UNKNOWN_STATE);
        final ArcUndeclaredThrowableException exception = new ArcUndeclaredThrowableException(psqlException);
        final ArcUndeclaredThrowableExceptionMapper mapper = new ArcUndeclaredThrowableExceptionMapper();
        assertEquals(409, mapper.toResponse(exception).getStatus());
    }

    @Test
    public void testPSQLExceptionWithDuplicateKeyMessage() {
        final String message = "ERROR: duplicate key value violates unique constraint \"ukanfyz2r9rvrprvwscg5ukqqokjxz2n\"\n" +
                "  Detail: Key (role)=(test unique role) already exists.";
        final PSQLException psqlException = new PSQLException(message, PSQLState.UNIQUE_VIOLATION);
        final ArcUndeclaredThrowableException exception = new ArcUndeclaredThrowableException(psqlException);
        final ArcUndeclaredThrowableExceptionMapper mapper = new ArcUndeclaredThrowableExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(409, response.getStatus());
        assertEquals("{errorMessage=ERROR: duplicate key value violates unique constraint}", response.getEntity().toString());
    }

    @Test
    public void testPSQLExceptionWithoutDuplicateKeyMessage() {
        final String message = "ERROR: null value in column \"tagtype_id\" violates not-null constraint\n" +
                "  Detail: Failing row contains (79, 2021-04-29 10:26:57.634816, alice, f, 2021-04-29 10:26:57.634822, alice, test, null).";
        final PSQLException psqlException = new PSQLException(message, PSQLState.NOT_NULL_VIOLATION);
        final ArcUndeclaredThrowableException exception = new ArcUndeclaredThrowableException(psqlException);
        final ArcUndeclaredThrowableExceptionMapper mapper = new ArcUndeclaredThrowableExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(409, response.getStatus());
        assertNull(response.getEntity());
    }
}
