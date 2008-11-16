package argo.token;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Collections;

public final class JsonObjectTest {

    @Test
    public void testEquals() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonValue>()), new JsonObject(new HashMap<JsonString, JsonValue>()));
        assertEquals(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue)new JsonString("Value"))), new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue)new JsonString("Value"))));
        assertFalse(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue)new JsonString("Value"))).equals(new JsonObject(Collections.singletonMap(new JsonString("Test"), (JsonValue)new JsonString("Another value")))));
    }

    @Test
    public void testHashCode() {
        // Add your code here
    }

    @Test
    public void testToString() {
        // Add your code here
    }
}
