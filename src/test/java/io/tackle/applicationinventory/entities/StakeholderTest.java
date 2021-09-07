package io.tackle.applicationinventory.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StakeholderTest {
    @Test
    public void testEquals() {
        Stakeholder x = new Stakeholder();
        x.id = 0L;
        Stakeholder y = new Stakeholder();
        y.id = 0L;
        Stakeholder z = new Stakeholder();
        z.id = 0L;
        // Reflexive
        assertEquals(x, x);
        // Symmetric
        assertEquals(x.equals(y), y.equals(x));
        // Transitive
        assertEquals(x.equals(y) && y.equals(z), x.equals(z));
        // Consistent
        assertEquals(x, y);
        y.email = "y";
        y.displayName = "y";
        assertEquals(x, y);
        // Non-nullity
        assertFalse(x.equals(null));

        assertNotEquals( "test", z);
    }

    @Test
    public void testHashCode() {
        Stakeholder x = new Stakeholder();
        x.id = 0L;
        int initial = x.hashCode();
        x.email = "x";
        x.displayName = "x";
        assertEquals(initial, x.hashCode());
        Stakeholder y = new Stakeholder();
        y.id = 0L;
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

}
