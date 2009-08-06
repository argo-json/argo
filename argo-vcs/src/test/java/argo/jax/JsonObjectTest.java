package argo.jax;

import argo.jdom.JsonNode;
import static argo.jdom.JsonNodeFactory.aJsonObject;
import static argo.jdom.JsonNodeFactory.aJsonString;
import argo.jdom.JsonRootNode;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonObjectTest {

    @Test
    public void testImmutablility() {
        final String baseKey = "Test";
        final JsonNode baseJsonKey = aJsonString(baseKey);
        final JsonNode baseJsonNode = new MockJsonNode(0);
        final Map<String, JsonNode> baseElements = new HashMap<String, JsonNode>();
        baseElements.put(baseKey, baseJsonNode);
        final JsonRootNode jsonObject = aJsonObject(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        baseElements.put("Another key", new MockJsonNode(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        jsonObject.getFields().put(aJsonString("Another key"), new MockJsonNode(1));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
    }

    @Test
    public void testEquals() {
        assertEquals(aJsonObject(new HashMap<String, JsonNode>()), aJsonObject(new HashMap<String, JsonNode>()));
        assertEquals(aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))), aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))));
        assertFalse(aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))).equals(aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(1)))));
        assertFalse(aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))).equals(aJsonObject(Collections.<String, JsonNode>singletonMap("Another test", new MockJsonNode(0)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(aJsonObject(new HashMap<String, JsonNode>()), aJsonObject(new HashMap<String, JsonNode>()));
        assertEquals(aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))).hashCode(), aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))).hashCode());
    }

    @Test
    public void testToString() {
        aJsonObject(Collections.<String, JsonNode>singletonMap("Test", new MockJsonNode(0))).toString();
    }
}
