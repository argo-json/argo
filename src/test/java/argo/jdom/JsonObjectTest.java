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
import java.util.stream.Collectors;

import static argo.MapBuilder.mapBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.someJsonFields;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class JsonObjectTest {

    @Test
    void factoryRejectsNullIterator() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject((Iterator<JsonField>) null));
    }

    @Test
    void factoryRejectsNullIteratorWithSize() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject(null, 1));
    }

    @Test
    void factoryRejectsNullIterable() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject((Iterable<JsonField>) null));
    }

    @Test
    void factoryRejectsIteratorContainingNullMember() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject(Collections.<JsonField>singletonList(null).iterator()));
    }

    @Test
    void factoryRejectsIteratorContainingNullMemberWithSize() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject(Collections.<JsonField>singletonList(null).iterator(), 1));
    }

    @Test
    void factoryRejectsIterableContainingNullMember() {
        assertThrows(NullPointerException.class, () -> JsonObject.jsonObject(singletonList(null)));
    }

    @Test
    void emptyObjectsAreAlwaysTheSameInstance() {
        assertThat(JsonObject.jsonObject(emptyList()), sameInstance(JsonObject.jsonObject(emptyIterator())));
        assertThat(JsonObject.jsonObject(emptyIterator()), sameInstance(JsonObject.jsonObject(emptyIterator(), 32)));
    }

    @Test
    void testImmutability() {
        final JsonStringNode baseJsonKey = string("Test");
        final JsonNode baseJsonNode = number("0");
        final Map<JsonStringNode, JsonNode> baseElements = mapBuilder(baseJsonKey, baseJsonNode).build();
        final JsonNode jsonObject = object(baseElements);
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        baseElements.put(string("Another key"), number("1"));
        assertEquals(1, jsonObject.getFields().size());
        assertTrue(jsonObject.getFields().containsKey(baseJsonKey));
        assertEquals(baseJsonNode, jsonObject.getFields().get(baseJsonKey));
        final UnsupportedOperationException unsupportedOperationException = assertThrows(UnsupportedOperationException.class, () -> jsonObject.getFields().put(string("Another key"), number("1")));
        assertThat(unsupportedOperationException.getMessage(), is(nullValue()));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEqualsSameObject() {
        final JsonObject jsonObject = JsonObject.jsonObject(someJsonFields());
        assertThat(jsonObject.equals(jsonObject), equalTo(true));
    }

    @Test
    void testEqualsEqualObject() {
        final Collection<JsonField> fields = someJsonFields();
        assertThat(JsonObject.jsonObject(fields), equalTo(JsonObject.jsonObject(fields)));
    }

    @Test
    void testNotEqualsUnequalJsonObject() {
        assertThat(JsonObject.jsonObject(singletonList(field("ho", string("ho")))).equals(JsonObject.jsonObject(singletonList(field("bo", string("bo"))))), equalTo(false));
    }

    @Test
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void testNotEqualsNull() {
        assertThat(JsonObject.jsonObject(someJsonFields()).equals(null), equalTo(false));
    }

    @Test
    void testNotEqualsObjectOfDifferentType() {
        assertThat(JsonObject.jsonObject(emptyList()).equals(JsonNodeFactories.array()), equalTo(false));
    }

    @Test
    void testHashCode() {
        final Collection<JsonField> fields = someJsonFields();
        assertEquals(JsonObject.jsonObject(fields).hashCode(), JsonObject.jsonObject(fields).hashCode());
    }

    @Test
    void testToString() {
        assertThat(object(singletonMap(string("Test"), number("0"))).toString(), equalTo("JsonObject{fields=[JsonField{name=JsonStringNode{value='Test'}, value=JsonNumberNode{value='0'}}]}"));
    }

    @Test
    void getTypeReturnsObject() {
        assertThat(JsonObject.jsonObject(someJsonFields()).getType(), equalTo(JsonNodeType.OBJECT));
    }

    @Test
    void hasTextThrowsUnsupportedOperationException() {
        assertThat(JsonObject.jsonObject(someJsonFields()).hasText(), equalTo(false));
    }

    @Test
    void getTextThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonObject.jsonObject(someJsonFields()).getText());
    }

    @Test
    void hasFieldsReturnsTrue() {
        assertThat(JsonObject.jsonObject(someJsonFields()).hasFields(), equalTo(true));
    }

    @Test
    void getFieldsReturnsCorrectValue() {
        final Collection<JsonField> fields = someJsonFields();
        assertThat(JsonObject.jsonObject(fields).getFields(), equalTo(fields.stream().collect(Collectors.toMap(JsonField::getName, JsonField::getValue)))); // TODO Flaky: java.lang.IllegalStateException: Duplicate key JsonNode{jsonNodeType=FALSE}
    }

    @Test
    void getFieldListReturnsCorrectValue() {
        final Collection<JsonField> fields = someJsonFields();
        assertThat(JsonObject.jsonObject(fields).getFieldList(), equalTo(fields));
    }

    @Test
    void hasElementsReturnsFalse() {
        assertThat(JsonObject.jsonObject(someJsonFields()).hasElements(), equalTo(false));
    }

    @Test
    void getElementsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonObject.jsonObject(someJsonFields()).getElements());
    }

}
