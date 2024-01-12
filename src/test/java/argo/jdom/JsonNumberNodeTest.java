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

import static argo.jdom.JsonNumberNodeTestBuilder.aValidJsonNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNumberNodeTest {

    @Test
    void handlesZero() {
        JsonNumberNode.jsonNumberNode("0");
    }

    @Test
    void handlesMinusZero() {
        JsonNumberNode.jsonNumberNode("-0");
    }

    @Test
    void handlesSingleDigitInteger() {
        JsonNumberNode.jsonNumberNode("2");
    }

    @Test
    void handlesMultiDigitInteger() {
        JsonNumberNode.jsonNumberNode("101");
    }

    @Test
    void handlesMultiDigitNegativeInteger() {
        JsonNumberNode.jsonNumberNode("-101");
    }

    @Test
    void rejectsLeadingZeros() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("01"));
    }

    @Test
    void handlesNonInteger() {
        JsonNumberNode.jsonNumberNode("0.1");
    }

    @Test
    void handlesNonIntegerWithSeveralDecimalPlaces() {
        JsonNumberNode.jsonNumberNode("-1.112");
    }

    @Test
    void handlesIntegerWithExponent() {
        JsonNumberNode.jsonNumberNode("-1e10");
    }

    @Test
    void handlesIntegerWithNegativeExponent() {
        JsonNumberNode.jsonNumberNode("-1E-10");
    }

    @Test
    void handlesIntegerWithPositiveExponent() {
        JsonNumberNode.jsonNumberNode("12e+10");
    }

    @Test
    void handlesNonIntegerWithPositiveExponent() {
        JsonNumberNode.jsonNumberNode("-12.55e+10");
    }

    @Test
    void handlesFacetiousZeroWithExponent() {
        JsonNumberNode.jsonNumberNode("-0E+99");
    }

    @Test
    void handlesZeroExponent() {
        JsonNumberNode.jsonNumberNode("12.231E0");
    }

    @Test
    void rejectsEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode(""));
    }

    @Test
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> JsonNumberNode.jsonNumberNode(null));
    }

    @Test
    void rejectsNonIntegerWithNothingBeforeTheDecimalPoint() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode(".1"));
    }

    @Test
    void rejectsNumberWithDecimalPointButNothingAfter() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("1."));
    }

    @Test
    void rejectsNonNumericCharacters() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("a"));
    }

    @Test
    void rejectsMultilingualPlaneCharacters() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("€"));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEqualsSameObject() {
        final JsonNode jsonNode = JsonNodeFactories.number("42");
        assertThat(jsonNode.equals(jsonNode), equalTo(true));
    }

    @Test
    void testEqualsEqualObject() {
        assertThat(JsonNodeFactories.number("42"), equalTo(JsonNodeFactories.number("42")));
    }

    @Test
    void testNotEqualsUnequalJsonStringNode() {
        assertThat(JsonNodeFactories.number("42").equals(JsonNodeFactories.number("64")), equalTo(false));
    }

    @Test
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void testNotEqualsNull() {
        assertThat(JsonNodeFactories.number(aValidJsonNumber()).equals(null), equalTo(false));
    }

    @Test
    void testNotEqualsObjectOfDifferentType() {
        assertThat(JsonNodeFactories.number("42").equals(JsonNodeFactories.string("42")), equalTo(false));
    }

    @Test
    void testHashCode() {
        assertEquals(JsonNodeFactories.number("42").hashCode(), JsonNodeFactories.number("42").hashCode());
    }

    @Test
    void testToString() {
        assertThat(JsonNodeFactories.number("42").toString(), equalTo("JsonNumberNode{value='42'}"));
    }

    @Test
    void zeroIsAlwaysTheSameInstance() {
        assertThat(JsonNodeFactories.number("0"), sameInstance(JsonNodeFactories.number("0")));
    }

    @Test
    void oneIsAlwaysTheSameInstance() {
        assertThat(JsonNodeFactories.number("1"), sameInstance(JsonNodeFactories.number("1")));
    }

    @Test
    void getTypeReturnsString() {
        assertThat(JsonNodeFactories.number(aValidJsonNumber()).getType(), equalTo(JsonNodeType.NUMBER));
    }

    @Test
    void hasTextReturnsTrue() {
        assertThat(JsonNodeFactories.number(aValidJsonNumber()).hasText(), equalTo(true));
    }

    @Test
    void getTextReturnsCorrectValue() {
        final String value = aValidJsonNumber();
        assertThat(JsonNodeFactories.number(value).getText(), equalTo(value));
    }

    @Test
    void hasFieldsReturnsFalse() {
        assertThat(JsonNodeFactories.number(aValidJsonNumber()).hasFields(), equalTo(false));
    }

    @Test
    void getFieldsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.number(aValidJsonNumber()).getFields());
    }

    @Test
    void getFieldListThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.number(aValidJsonNumber()).getFieldList());
    }

    @Test
    void hasElementsReturnsFalse() {
        assertThat(JsonNodeFactories.number(aValidJsonNumber()).hasElements(), equalTo(false));
    }

    @Test
    void getElementsThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNodeFactories.number(aValidJsonNumber()).getElements());
    }

}
