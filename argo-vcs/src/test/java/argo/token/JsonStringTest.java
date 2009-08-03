package argo.token;

import argo.jdom.JsonString;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JsonStringTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            new JsonString(null);
            fail("Constructing a JsonString with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonString("co"), new JsonString("co"));
        assertFalse(new JsonString("ho").equals(new JsonString("bo")));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonString("po").hashCode(), new JsonString("po").hashCode());
    }

    @Test
    public void testToString() {
        new JsonString("lo");
    }
}
