package io.tackle.applicationinventory.resources;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Test;

@NativeImageTest
public class NativeBusinessServiceIT extends BusinessServiceTest {

    @Test
    public void testBusinessServiceCreateUpdateAndDeleteEndpointNative() {
        testBusinessServiceCreateUpdateAndDeleteEndpoint(true);
    }

}
