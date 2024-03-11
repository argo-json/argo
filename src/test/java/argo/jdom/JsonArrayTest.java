/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.util.*;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestingFactories.someJsonNodes;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class JsonArrayTest {

    @Test
    void factoryRejectsNullIterator() {
        assertThrows(NullPointerException.class, () -> JsonArray.jsonArray((Iterator<? extends JsonNode>) null));
    }

    @Test
    void factoryRejectsNullIterable() {
        assertThrows(NullPointerException.class, () -> JsonArray.jsonArray((Iterable<? extends JsonNode>) null));
    }

    @Test
    void factoryRejectsIteratorContainingNullMember() {
        assertThrows(NullPointerException.class, () -> JsonArray.jsonArray(Collections.<JsonNode>singletonList(null).iterator()));
    }

    @Test
    void factoryRejectsIterableContainingNullMember() {
        assertThrows(NullPointerException.class, () -> JsonArray.jsonArray(singletonList(null)));
    }

    @Test
    void emptyArraysAreAlwaysTheSameInstance() {
        assertThat(JsonArray.jsonArray(emptyList()), sameInstance(JsonArray.jsonArray(emptyIterator())));
    }

    @Test
    void testImmutability() {
        final JsonNode baseJsonNode = number("0");
        final List<JsonNode> baseElements = new LinkedList<>(singletonList(baseJsonNode));
        final JsonNode jsonArray = JsonArray.jsonArray(baseElements);
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        baseElements.add(number("1"));
        assertEquals(1, jsonArray.getElements().size());
        assertEquals(baseJsonNode, jsonArray.getElements().get(0));
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> jsonArray.getElements().add(number("1")));
        assertThat(exception.getMessage(), is(nullValue()));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEqualsSameObject() {
        final JsonArray jsonArray = JsonArray.jsonArray(someJsonNodes());
        assertThat(jsonArray.equals(jsonArray), equalTo(true));
    }

    @Test
    void testEqualsEqualObject() {
        final Collection<JsonNode> elements = someJsonNodes();
        assertThat(JsonArray.jsonArray(elements), equalTo(JsonArray.jsonArray(elements)));
    }

    @Test
    void testNotEqualsUnequalJsonArray() {
        assertThat(JsonArray.jsonArray(singletonList(string("ho"))).equals(JsonArray.jsonArray(singletonList(string("bo")))), equalTo(false));
    }

    @Test
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void testNotEqualsNull() {
        assertThat(JsonArray.jsonArray(someJsonNodes()).equals(null), equalTo(false));
    }

    @Test
    void testNotEqualsObjectOfDifferentType() {
        assertThat(JsonArray.jsonArray(emptyList()).equals(object()), equalTo(false));
    }

    @Test
    void testHashCode() {
        final Collection<JsonNode> elements = someJsonNodes();
        assertEquals(JsonArray.jsonArray(elements).hashCode(), JsonArray.jsonArray(elements).hashCode());
    }

    @Test
    void testToString() {
        assertThat(JsonArray.jsonArray(singletonList(number("0"))).toString(), equalTo("JsonArray{elements=[JsonNumberNode{value='0'}]}"));
    }

    @Test
    void getTypeReturnsArray() {
        assertThat(JsonArray.jsonArray(someJsonNodes()).getType(), equalTo(JsonNodeType.ARRAY));
    }

    @Test
    void hasTextReturnsFalse() {
        assertThat(JsonArray.jsonArray(someJsonNodes()).hasText(), equalTo(false));
    }

    @Test
    void getTextThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonArray.jsonArray(someJsonNodes()).getText());
    }

    @Test
    void hasFieldsReturnsFalse() {
        assertThat(JsonArray.jsonArray(someJsonNodes()).hasFields(), equalTo(false));
    }

    @Test
    void getFieldsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonArray.jsonArray(someJsonNodes()).getFields());
    }

    @Test
    void getFieldListThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonArray.jsonArray(someJsonNodes()).getFieldList());
    }

    @Test
    void hasElementsReturnsTrue() {
        assertThat(JsonArray.jsonArray(someJsonNodes()).hasElements(), equalTo(true));
    }

    @Test
    void getElementsReturnsCorrectValue() {
        final Collection<JsonNode> elements = someJsonNodes();
        assertThat(JsonArray.jsonArray(elements).getElements(), equalTo(elements));
    }

}
