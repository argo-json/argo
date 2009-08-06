package argo.jax;

import argo.jdom.JsonNodeFactory;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JsonStringTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            JsonNodeFactory.aJsonStringNode(null);
            fail("Constructing a JsonTextNode with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactory.aJsonStringNode("co"), JsonNodeFactory.aJsonStringNode("co"));
        assertFalse(JsonNodeFactory.aJsonStringNode("ho").equals(JsonNodeFactory.aJsonStringNode("bo")));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactory.aJsonStringNode("po").hashCode(), JsonNodeFactory.aJsonStringNode("po").hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactory.aJsonStringNode("lo");
    }
}
