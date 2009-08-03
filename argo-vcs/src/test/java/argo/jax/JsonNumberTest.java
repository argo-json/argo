package argo.jax;

import argo.jdom.JsonNumber;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JsonNumberTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            new JsonNumber(null);
            fail("Constructing a JsonNumber with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonNumber("0"), new JsonNumber("0"));
        assertFalse(new JsonNumber("0").equals(new JsonNumber("1")));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonNumber("0").hashCode(), new JsonNumber("0").hashCode());
    }

    @Test
    public void testToString() {
        new JsonNumber("0").toString();
    }
}
