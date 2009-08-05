package argo.jax;

import argo.jdom.JsonNode;
import argo.jdom.JsonObject;
import argo.jdom.JsonString;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonObjectTest {

    @Test
    public void testImmutablility() {
        final JsonString baseJsonKey = new JsonString("Test");
        final JsonNode baseJsonNode = new MockJsonNode(0);
        final Map<JsonString, JsonNode> baseElements = new HashMap<JsonString, JsonNode>();
        baseElements.put(baseJsonKey, baseJsonNode);
        final JsonObject jsonObject = new JsonObject(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        baseElements.put(new JsonString("Another key"), new MockJsonNode(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        jsonObject.getFields().put(new JsonString("Another key"), new MockJsonNode(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonNode>()), new JsonObject(new HashMap<JsonString, JsonNode>()));
        assertEquals(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))), new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))));
        assertFalse(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))).equals(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(1)))));
        assertFalse(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))).equals(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Another test"), new MockJsonNode(0)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonObject(new HashMap<JsonString, JsonNode>()), new JsonObject(new HashMap<JsonString, JsonNode>()));
        assertEquals(new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))).hashCode(), new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonObject(Collections.<JsonString, JsonNode>singletonMap(new JsonString("Test"), new MockJsonNode(0))).toString();
    }
}
