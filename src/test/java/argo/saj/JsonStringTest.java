package argo.saj;

import argo.jdom.JsonNodeFactory;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JsonStringTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            JsonNodeFactory.aJsonString(null);
            fail("Constructing a JsonTextNode with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactory.aJsonString("co"), JsonNodeFactory.aJsonString("co"));
        assertFalse(JsonNodeFactory.aJsonString("ho").equals(JsonNodeFactory.aJsonString("bo")));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactory.aJsonString("po").hashCode(), JsonNodeFactory.aJsonString("po").hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactory.aJsonString("lo");
    }
}
