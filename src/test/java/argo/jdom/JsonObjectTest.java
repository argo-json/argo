/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public final class JsonObjectTest {

    @Test
    public void testImmutability() {
        final JsonStringNode baseJsonKey = string("Test");
        final JsonNode baseJsonNode = number("0");
        final Map<JsonStringNode, JsonNode> baseElements = new HashMap<>();
        baseElements.put(baseJsonKey, baseJsonNode);
        final JsonNode jsonObject = object(baseElements);
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
        assertEquals(object(new HashMap<>()), object(new HashMap<>()));
        assertEquals(object(singletonMap(string("Test"), number("0"))), object(singletonMap(string("Test"), number("0"))));
        assertEquals(object(field(string("Test"), number("0"))), object(singletonMap(string("Test"), number("0"))));
        assertEquals(object(field(string("Test"), number("0"))), object(field(string("Test"), number("0"))));
        assertEquals(object(singletonList(field(string("Test"), number("0")))), object(field(string("Test"), number("0"))));
        assertEquals(object(singletonList(field(string("Test"), number("0")))), object(singletonList(field(string("Test"), number("0")))));
        assertEquals(object(singletonList(field(string("Test"), number("0")))), object(singletonList(field(string("Test"), number("0")))));
        assertEquals(object(singletonList(field(string("Test"), number("0"))).iterator()), object(singletonList(field(string("Test"), number("0")))));
        assertEquals(object(singletonList(field(string("Test"), number("0"))).iterator()), object(singletonList(field(string("Test"), number("0"))).iterator()));
        assertFalse(object(singletonMap(string("Test"), number("0"))).equals(object(singletonMap(string("Test"), number("1")))));
        assertFalse(object(singletonMap(string("Test"), number("0"))).equals(object(singletonMap(string("Another test"), number("0")))));
    }

    @Test
    public void testHashCode() {
        assertEquals(object(new HashMap<>()), object(new HashMap<>()));
        assertEquals(object(singletonMap(string("Test"), number("0"))).hashCode(), object(singletonMap(string("Test"), number("0"))).hashCode());
        assertEquals(object(field(string("Test"), number("0"))).hashCode(), object(singletonMap(string("Test"), number("0"))).hashCode());
        assertEquals(object(field(string("Test"), number("0"))).hashCode(), object(field(string("Test"), number("0"))).hashCode());
        assertEquals(object(singletonList(field(string("Test"), number("0")))).hashCode(), object(field(string("Test"), number("0"))).hashCode());
        assertEquals(object(singletonList(field(string("Test"), number("0")))).hashCode(), object(singletonList(field(string("Test"), number("0")))).hashCode());
        assertEquals(object(singletonList(field(string("Test"), number("0")))).hashCode(), object(singletonList(field(string("Test"), number("0")))).hashCode());
        assertEquals(object(singletonList(field(string("Test"), number("0"))).iterator()).hashCode(), object(singletonList(field(string("Test"), number("0")))).hashCode());
        assertEquals(object(singletonList(field(string("Test"), number("0"))).iterator()).hashCode(), object(singletonList(field(string("Test"), number("0"))).iterator()).hashCode());
    }

    @Test
    public void testToString() {
        assertThat(object(singletonMap(string("Test"), number("0"))).toString(), is(not(nullValue())));
    }
}
