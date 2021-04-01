package io.tackle.applicationinventory.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ApplicationTest {

    @Test
    public void testEquals() {
        Application x = new Application();
        x.id = 0L;
        Application y = new Application();
        y.id = 0L;
        Application z = new Application();
        z.id = 0L;
        ApplicationsDependency ad = new ApplicationsDependency();
        ad.id = 0L;
        // Reflexive
        assertEquals(x, x);
        // Symmetric
        assertEquals(x.equals(y), y.equals(x));
        assertEquals(x.equals(ad), ad.equals(x));
        // Transitive
        assertEquals(x.equals(y) && y.equals(z), x.equals(z));
        // Consistent
        assertEquals(x, y);
        y.name = "y";
        y.description = "y";
        y.comments = "y";
        assertEquals(x, y);
        // Non-nullity
        assertFalse(x.equals(null));

        assertNotEquals( "test", z);
    }

    @Test
    public void testHashCode() {
        Application x = new Application();
        x.id = 0L;
        int initial = x.hashCode();
        x.name = "x";
        x.description = "x";
        x.comments = "x";
        assertEquals(initial, x.hashCode());
        Application y = new Application();
        y.id = 0L;
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }
}
