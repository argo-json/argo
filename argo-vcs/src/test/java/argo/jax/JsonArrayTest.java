package argo.jax;

import argo.jdom.JsonArray;
import argo.jdom.JsonValue;
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
        final JsonValue baseJsonValue = new MockJsonValue(0);
        final List<JsonValue> baseElements = new LinkedList<JsonValue>(Arrays.<JsonValue>asList(baseJsonValue));
        final JsonArray jsonArray = new JsonArray(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonValue, jsonArray.getElements().get(0));
        baseElements.add(new MockJsonValue(1));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonValue, jsonArray.getElements().get(0));
        jsonArray.getElements().add(new MockJsonValue(1));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonValue, jsonArray.getElements().get(0));
    }

    @Test
    public void testEquals() {
        assertEquals(new JsonArray(new LinkedList<JsonValue>()), new JsonArray(new LinkedList<JsonValue>()));
        assertEquals(new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))), new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))));
        assertFalse(new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))).equals(new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(1)))));
    }

    @Test
    public void testHashCode() {
        assertEquals(new JsonArray(new LinkedList<JsonValue>()).hashCode(), new JsonArray(new LinkedList<JsonValue>()).hashCode());
        assertEquals(new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))).hashCode(), new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))).hashCode());
    }

    @Test
    public void testToString() {
        new JsonArray(Collections.<JsonValue>singletonList(new MockJsonValue(0))).toString();
    }
}
