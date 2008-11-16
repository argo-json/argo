package argo.token;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonObjectTest {

    @Test
    public void testImmutablility() {
        final JsonString baseJsonKey = new JsonString("Test");
        final JsonValue baseJsonValue = new MockJsonValue(0);
        final Map<JsonString, JsonValue> baseElements = new HashMap<JsonString, JsonValue>();
        baseElements.put(baseJsonKey, baseJsonValue);
        final JsonObject jsonObject = new JsonObject(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonValue, jsonObject.getFields().get(baseJsonKey));
        baseElements.put(new JsonString("Another key"), new MockJsonValue(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonValue, jsonObject.getFields().get(baseJsonKey));
        jsonObject.getFields().put(new JsonString("Another key"), new MockJsonValue(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonValue, jsonObject.getFields().get(baseJsonKey));
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonValue>()), new JsonObject(new HashMap<JsonString, JsonValue>()));
        assertEquals(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))), new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))));
        assertFalse(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))).equals(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(1)))));
        assertFalse(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))).equals(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Another test"), new MockJsonValue(0)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonValue>()), new JsonObject(new HashMap<JsonString, JsonValue>()));
        assertEquals(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))).hashCode(), new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new MockJsonValue(0))).toString();
    }
}
