/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static argo.jdom.JsonNodeFactories.aJsonNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public final class JsonArrayTest {

    @Test
    public void testImmutablility() {
        final JsonNode baseJsonNode = aJsonNumber("0");
        final List<JsonNode> baseElements = new LinkedList<JsonNode>(Arrays.<JsonNode>asList(baseJsonNode));
        final JsonNode jsonArray = JsonNodeFactories.aJsonArray(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        baseElements.add(aJsonNumber("1"));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        jsonArray.getElements().add(aJsonNumber("1"));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactories.aJsonArray(new LinkedList<JsonNode>()), JsonNodeFactories.aJsonArray(new LinkedList<JsonNode>()));
        assertEquals(JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))), JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))));
        assertFalse(JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))).equals(JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("1")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactories.aJsonArray(new LinkedList<JsonNode>()).hashCode(), JsonNodeFactories.aJsonArray(new LinkedList<JsonNode>()).hashCode());
        assertEquals(JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))).hashCode(), JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))).hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactories.aJsonArray(Collections.<JsonNode>singletonList(aJsonNumber("0"))).toString();
    }
}
