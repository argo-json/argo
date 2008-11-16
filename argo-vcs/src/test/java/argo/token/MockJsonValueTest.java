package argo.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public final class MockJsonValueTest {
    @Test
    public void testEquals() {
        assertEquals(new MockJsonValue(0), new MockJsonValue(0));
        assertFalse(new MockJsonValue(0).equals(new MockJsonValue(1)));
    }

    @Test
    public void testHashCode() {
        assertEquals(new MockJsonValue(0).hashCode(), new MockJsonValue(0).hashCode());
    }
}
