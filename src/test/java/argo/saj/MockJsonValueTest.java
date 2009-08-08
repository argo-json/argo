package argo.saj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public final class MockJsonValueTest {
    @Test
    public void testEquals() {
        assertEquals(new MockJsonNode(0), new MockJsonNode(0));
        assertFalse(new MockJsonNode(0).equals(new MockJsonNode(1)));
    }

    @Test
    public void testHashCode() {
        assertEquals(new MockJsonNode(0).hashCode(), new MockJsonNode(0).hashCode());
    }
}
