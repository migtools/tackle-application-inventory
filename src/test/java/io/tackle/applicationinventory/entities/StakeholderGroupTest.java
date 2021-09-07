package io.tackle.applicationinventory.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StakeholderGroupTest {
    @Test
    public void testEquals() {
        StakeholderGroup x = new StakeholderGroup();
        x.id = 0L;
        StakeholderGroup y = new StakeholderGroup();
        y.id = 0L;
        StakeholderGroup z = new StakeholderGroup();
        z.id = 0L;
        // Reflexive
        assertEquals(x, x);
        // Symmetric
        assertEquals(x.equals(y), y.equals(x));
        // Transitive
        assertEquals(x.equals(y) && y.equals(z), x.equals(z));
        // Consistent
        assertEquals(x, y);
        y.name = "y";
        y.description = "y";
        assertEquals(x, y);
        // Non-nullity
        assertFalse(x.equals(null));

        assertNotEquals( "test", z);
    }

    @Test
    public void testHashCode() {
        StakeholderGroup x = new StakeholderGroup();
        x.id = 0L;
        int initial = x.hashCode();
        x.name = "x";
        x.description = "x";
        assertEquals(initial, x.hashCode());
        StakeholderGroup y = new StakeholderGroup();
        y.id = 0L;
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

}
