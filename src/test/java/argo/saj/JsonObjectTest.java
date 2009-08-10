/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import argo.jdom.JsonNode;
import static argo.jdom.JsonNodeFactory.*;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonObjectTest {

    @Test
    public void testImmutablility() {
        final JsonStringNode baseJsonKey = aJsonString("Test");
        final JsonNode baseJsonNode = aJsonNumber("0");
        final Map<JsonStringNode, JsonNode> baseElements = new HashMap<JsonStringNode, JsonNode>();
        baseElements.put(baseJsonKey, baseJsonNode);
        final JsonRootNode jsonObject = aJsonObject(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        baseElements.put(aJsonString("Another key"), aJsonNumber("1"));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        jsonObject.getFields().put(aJsonString("Another key"), aJsonNumber("1"));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
    }

    @Test
    public void testEquals() {
        assertEquals(aJsonObject(new HashMap<JsonStringNode, JsonNode>()), aJsonObject(new HashMap<JsonStringNode, JsonNode>()));
        assertEquals(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))), aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))));
        assertFalse(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))).equals(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("1")))));
        assertFalse(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))).equals(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Another test"), aJsonNumber("0")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(aJsonObject(new HashMap<JsonStringNode, JsonNode>()), aJsonObject(new HashMap<JsonStringNode, JsonNode>()));
        assertEquals(aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))).hashCode(), aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))).hashCode());
    }

    @Test
    public void testToString() {
        aJsonObject(Collections.<JsonStringNode, JsonNode>singletonMap(aJsonString("Test"), aJsonNumber("0"))).toString();
    }
}
