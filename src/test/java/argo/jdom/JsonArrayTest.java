/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static argo.jdom.JsonNodeFactories.number;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class JsonArrayTest {

    @Test
    void testImmutability() {
        final JsonNode baseJsonNode = number("0");
        final List<JsonNode> baseElements = new LinkedList<>(singletonList(baseJsonNode));
        final JsonNode jsonArray = JsonNodeFactories.array(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        baseElements.add(number("1"));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> jsonArray.getElements().add(number("1")));
        assertThat(exception.getMessage(), is(nullValue()));
    }

    @Test
    void testEquals() {
        assertEquals(JsonNodeFactories.array(new LinkedList<>()), JsonNodeFactories.array(new LinkedList<>()));
        assertEquals(JsonNodeFactories.array(singletonList(number("0"))), JsonNodeFactories.array(singletonList(number("0"))));
        assertEquals(JsonNodeFactories.array(singletonList(number("0")).iterator()), JsonNodeFactories.array(singletonList(number("0"))));
        assertEquals(JsonNodeFactories.array(singletonList(number("0")).iterator()), JsonNodeFactories.array(singletonList(number("0")).iterator()));
        assertNotEquals(JsonNodeFactories.array(singletonList(number("0"))), JsonNodeFactories.array(singletonList(number("1"))));
    }

    @Test
    void testHashCode() {
        assertEquals(JsonNodeFactories.array(new LinkedList<>()).hashCode(), JsonNodeFactories.array(new LinkedList<>()).hashCode());
        assertEquals(JsonNodeFactories.array(emptyIterator()).hashCode(), JsonNodeFactories.array(new LinkedList<>()).hashCode());
        assertEquals(JsonNodeFactories.array(singletonList(number("0"))).hashCode(), JsonNodeFactories.array(singletonList(number("0"))).hashCode());
    }

    @Test
    void testToString() {
        assertThat(JsonNodeFactories.array(singletonList(number("0"))).toString(), is(not(nullValue())));
    }
}
