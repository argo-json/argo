/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public final class JsonObjectTest {

    @Test
    public void testImmutability() {
        final JsonStringNode baseJsonKey = string("Test");
        final JsonNode baseJsonNode = number("0");
        final Map<JsonStringNode, JsonNode> baseElements = new HashMap<JsonStringNode, JsonNode>();
        baseElements.put(baseJsonKey, baseJsonNode);
        final JsonRootNode jsonObject = object(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        baseElements.put(string("Another key"), number("1"));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        try {
            jsonObject.getFields().put(string("Another key"), number("1"));
            fail("modifying the fields retrieved from a JsonObject should result in an UnsupportedOperationException");
        } catch (Exception e) {
            assertThat(e.getMessage(), is(nullValue()));
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(object(new HashMap<JsonStringNode, JsonNode>()), object(new HashMap<JsonStringNode, JsonNode>()));
        assertEquals(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))), object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))));
        assertFalse(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))).equals(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("1")))));
        assertFalse(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))).equals(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Another test"), number("0")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(object(new HashMap<JsonStringNode, JsonNode>()), object(new HashMap<JsonStringNode, JsonNode>()));
        assertEquals(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))).hashCode(), object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))).hashCode());
    }

    @Test
    public void testToString() {
        assertThat(object(Collections.<JsonStringNode, JsonNode>singletonMap(string("Test"), number("0"))).toString(), is(not(nullValue())));
    }
}
