/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
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

import io.quarkus.rest.data.panache.RestDataPanacheException;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApplicationsInventoryExceptionMapperTest {

    @Test
    public void testPSQLExceptionWithoutMessage() {
        final PSQLException psqlException = new PSQLException(null, PSQLState.UNKNOWN_STATE);
        final RestDataPanacheException exception = new RestDataPanacheException("message", psqlException);
        final ApplicationsInventoryExceptionMapper mapper = new ApplicationsInventoryExceptionMapper();
        assertEquals(500, mapper.toResponse(exception).getStatus());
    }

    @Test
    public void testPSQLExceptionWithDuplicateKeyMessage() {
        final String message = "ERROR: duplicate key value violates unique constraint \"ukzludrzegwoz9cylsy6dnfgig6meaio\"\n" +
                "  Detail: Key (name)=(application) already exists.";
        final PSQLException psqlException = new PSQLException(message, PSQLState.UNIQUE_VIOLATION);
        final RestDataPanacheException exception = new RestDataPanacheException("message", psqlException);
        final ApplicationsInventoryExceptionMapper mapper = new ApplicationsInventoryExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(409, response.getStatus());
        assertEquals("{errorMessage=ERROR: duplicate key value violates unique constraint}", response.getEntity().toString());
    }

    @Test
    public void testPSQLExceptionWithoutDuplicateKeyMessage() {
        final String message = "ERROR: null value in column \"review_id\" violates not-null constraint\n" +
                "  Detail: Failing row contains (79, 2021-04-29 10:26:57.634816, alice, f, 2021-04-29 10:26:57.634822, alice, test, null).";
        final PSQLException psqlException = new PSQLException(message, PSQLState.NOT_NULL_VIOLATION);
        final RestDataPanacheException exception = new RestDataPanacheException("message", psqlException);
        final ApplicationsInventoryExceptionMapper mapper = new ApplicationsInventoryExceptionMapper();
        final Response response = mapper.toResponse(exception);
        assertEquals(500, response.getStatus());
        assertNull(response.getEntity());
    }

}
