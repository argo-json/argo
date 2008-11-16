package argo.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;

public final class JsonArrayTest {

    @Test
    public void testEquals() {
        assertEquals(new JsonArray(new LinkedList<JsonValue>()), new JsonArray(new LinkedList<JsonValue>()));
        assertEquals(new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))), new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))));
        assertFalse(new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))).equals(new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(1)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonArray(new LinkedList<JsonValue>()).hashCode(), new JsonArray(new LinkedList<JsonValue>()).hashCode());
        assertEquals(new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))).hashCode(), new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonArray(Collections.singletonList((JsonValue) new MockJsonValue(0))).toString();
    }
}
