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

import static argo.TestingFactories.aString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonStringNodeTest {

    @Test
    void constructorRejectsNullValue() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> JsonNodeFactories.string(null));
        assertThat(nullPointerException.getMessage(), equalTo("Value is null"));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEqualsSameObject() {
        final JsonStringNode jsonStringNode = JsonNodeFactories.string("co");
        assertThat(jsonStringNode.equals(jsonStringNode), equalTo(true));
    }

    @Test
    void testEqualsEqualObject() {
        assertThat(JsonNodeFactories.string("co"), equalTo(JsonNodeFactories.string("co")));
    }

    @Test
    void testNotEqualsUnequalJsonStringNode() {
        assertThat(JsonNodeFactories.string("ho").equals(JsonNodeFactories.string("bo")), equalTo(false));
    }

    @Test
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void testNotEqualsNull() {
        assertThat(JsonNodeFactories.string(aString()).equals(null), equalTo(false));
    }

    @Test
    void testNotEqualsObjectOfDifferentType() {
        assertThat(JsonNodeFactories.string("42").equals(JsonNodeFactories.number("42")), equalTo(false));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testCompareToSameObjectReturnsZero() {
        final JsonStringNode jsonStringNode = JsonNodeFactories.string("co");
        assertThat(jsonStringNode.compareTo(jsonStringNode), equalTo(0));
    }

    @Test
    void testCompareToEqualObjectReturnsZero() {
        assertThat(JsonNodeFactories.string("co").compareTo(JsonNodeFactories.string("co")), equalTo(0));
    }

    @Test
    void testCompareToGreaterJsonStringNodeReturnsLessThanZero() {
        assertThat(JsonNodeFactories.string("a").compareTo(JsonNodeFactories.string("b")), lessThan(0));
    }

    @Test
    void testCompareToLesserJsonStringNodeReturnsGreaterThanZero() {
        assertThat(JsonNodeFactories.string("b").compareTo(JsonNodeFactories.string("a")), greaterThan(0));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testCompareToNullThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> JsonNodeFactories.string(aString()).compareTo(null));
    }

    @Test
    void testHashCode() {
        assertEquals(JsonNodeFactories.string("po").hashCode(), JsonNodeFactories.string("po").hashCode());
    }

    @Test
    void testToString() {
        JsonNodeFactories.string("lo");
    }

    @Test
    void emptyStringsAreAlwaysTheSameInstance() {
        assertThat(JsonNodeFactories.string(""), sameInstance(JsonNodeFactories.string("")));
    }

    @Test
    void getTypeReturnsString() {
        assertThat(JsonNodeFactories.string(aString()).getType(), equalTo(JsonNodeType.STRING));
    }

    @Test
    void hasTextReturnsTrue() {
        assertThat(JsonNodeFactories.string(aString()).hasText(), equalTo(true));
    }

    @Test
    void getTextReturnsCorrectValue() {
        final String value = aString();
        assertThat(JsonNodeFactories.string(value).getText(), equalTo(value));
    }

    @Test
    void hasFieldsReturnsFalse() {
        assertThat(JsonNodeFactories.string(aString()).hasFields(), equalTo(false));
    }

    @Test
    void getFieldsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.string(aString()).getFields());
    }

    @Test
    void getFieldListThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.string(aString()).getFieldList());
    }

    @Test
    void hasElementsReturnsFalse() {
        assertThat(JsonNodeFactories.string(aString()).hasElements(), equalTo(false));
    }

    @Test
    void getElementsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.string(aString()).getElements());
    }

}
