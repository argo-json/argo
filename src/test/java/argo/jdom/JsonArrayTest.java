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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public final class JsonArrayTest {

    @Test
    public void testImmutability() {
        final JsonNode baseJsonNode = JsonNodeFactories.number("0");
        final List<JsonNode> baseElements = new LinkedList<JsonNode>(Arrays.<JsonNode>asList(baseJsonNode));
        final JsonNode jsonArray = JsonNodeFactories.array(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        baseElements.add(JsonNodeFactories.number("1"));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        try {
            jsonArray.getElements().add(JsonNodeFactories.number("1"));
            fail("modifying the elements retrieved from a JsonArray should result in an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), is(nullValue()));
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactories.array(new LinkedList<JsonNode>()), JsonNodeFactories.array(new LinkedList<JsonNode>()));
        assertEquals(JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))), JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))));
        assertFalse(JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))).equals(JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("1")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactories.array(new LinkedList<JsonNode>()).hashCode(), JsonNodeFactories.array(new LinkedList<JsonNode>()).hashCode());
        assertEquals(JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))).hashCode(), JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))).hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactories.array(Collections.<JsonNode>singletonList(JsonNodeFactories.number("0"))).toString();
    }
}
