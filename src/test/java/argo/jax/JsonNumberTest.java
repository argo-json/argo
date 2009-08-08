package argo.jax;

import argo.jdom.JsonNodeFactory;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JsonNumberTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            JsonNodeFactory.aJsonNumber(null);
            fail("Constructing a JsonNumber with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactory.aJsonNumber("0"), JsonNodeFactory.aJsonNumber("0"));
        assertFalse(JsonNodeFactory.aJsonNumber("0").equals(JsonNodeFactory.aJsonNumber("1")));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactory.aJsonNumber("0").hashCode(), JsonNodeFactory.aJsonNumber("0").hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactory.aJsonNumber("0").toString();
    }
}
