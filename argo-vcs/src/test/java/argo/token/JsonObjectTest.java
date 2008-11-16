package argo.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

public final class JsonObjectTest {

    @Test
    public void testEquals() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonValue>()), new JsonObject(new HashMap<JsonString, JsonValue>()));
        assertEquals(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))), new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))));
        assertFalse(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))).equals(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Another value")))));
        assertFalse(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))).equals(new JsonObject(Collections.singletonMap(new JsonString("Another test"), (JsonValue) new JsonString("Value")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonValue>()), new JsonObject(new HashMap<JsonString, JsonValue>()));
        assertEquals(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))).hashCode(), new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue) new JsonString("Value"))).toString();
    }
}
