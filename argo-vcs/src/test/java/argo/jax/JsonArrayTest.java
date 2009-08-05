package argo.jax;

import argo.jdom.JsonArray;
import argo.jdom.JsonNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class JsonArrayTest {

    @Test
    public void testImmutablility() {
        final JsonNode baseJsonNode = new MockJsonNode(0);
        final List<JsonNode> baseElements = new LinkedList<JsonNode>(Arrays.<JsonNode>asList(baseJsonNode));
        final JsonArray jsonArray = new JsonArray(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        baseElements.add(new MockJsonNode(1));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        jsonArray.getElements().add(new MockJsonNode(1));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonArray(new LinkedList<JsonNode>()), new JsonArray(new LinkedList<JsonNode>()));
        assertEquals(new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))), new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))));
        assertFalse(new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))).equals(new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(1)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonArray(new LinkedList<JsonNode>()).hashCode(), new JsonArray(new LinkedList<JsonNode>()).hashCode());
        assertEquals(new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))).hashCode(), new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonArray(Collections.<JsonNode>singletonList(new MockJsonNode(0))).toString();
    }
}
