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

public final class JsonNumberNodeTest {

    @Test
    public void handlesZero() {
        JsonNumberNode.jsonNumberNode("0");
    }

    @Test
    public void handlesMinusZero() {
        JsonNumberNode.jsonNumberNode("-0");
    }

    @Test
    public void handlesSingleDigitInteger() {
        JsonNumberNode.jsonNumberNode("2");
    }

    @Test
    public void handlesMultiDigitInteger() {
        JsonNumberNode.jsonNumberNode("101");
    }

    @Test
    public void handlesMultiDigitNegativeInteger() {
        JsonNumberNode.jsonNumberNode("-101");
    }

    @Test
    public void rejectsLeadingZeros() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("01"));
    }

    @Test
    public void handlesNonInteger() {
        JsonNumberNode.jsonNumberNode("0.1");
    }

    @Test
    public void handlesNonIntegerWithSeveralDecimalPlaces() {
        JsonNumberNode.jsonNumberNode("-1.112");
    }

    @Test
    public void handlesIntegerWithExponent() {
        JsonNumberNode.jsonNumberNode("-1e10");
    }

    @Test
    public void handlesIntegerWithNegativeExponent() {
        JsonNumberNode.jsonNumberNode("-1E-10");
    }

    @Test
    public void handlesIntegerWithPositiveExponent() {
        JsonNumberNode.jsonNumberNode("12e+10");
    }

    @Test
    public void handlesNonIntegerWithPositiveExponent() {
        JsonNumberNode.jsonNumberNode("-12.55e+10");
    }

    @Test
    public void handlesFacetiousZeroWithExponent() {
        JsonNumberNode.jsonNumberNode("-0E+99");
    }

    @Test
    public void handlesZeroExponent() {
        JsonNumberNode.jsonNumberNode("12.231E0");
    }

    @Test
    public void rejectsNonIntegerWithNothingBeforeTheDecimalPoint() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode(".1"));
    }

    @Test
    public void rejectsNumberWithDecimalPointButNothingAfter() {
        assertThrows(IllegalArgumentException.class, () -> JsonNumberNode.jsonNumberNode("1."));
    }

}
