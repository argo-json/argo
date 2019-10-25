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

import org.junit.jupiter.api.Test;

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
    void rejectsNonIntegerWithNothingBeforeTheDecimalPoint() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode(".1"));
    }

    @Test
    void rejectsNumberWithDecimalPointButNothingAfter() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("1."));
    }

}
