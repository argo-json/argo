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

public final class JsonNumberNodeTest {

    @Test
    public void handlesZero() throws Exception {
        JsonNumberNode.jsonNumberNode("0");
    }

    @Test
    public void handlesMinusZero() throws Exception {
        JsonNumberNode.jsonNumberNode("-0");
    }

    @Test
    public void handlesSingleDigitInteger() throws Exception {
        JsonNumberNode.jsonNumberNode("2");
    }

    @Test
    public void handlesMultiDigitInteger() throws Exception {
        JsonNumberNode.jsonNumberNode("101");
    }

    @Test
    public void handlesMultiDigitNegativeInteger() throws Exception {
        JsonNumberNode.jsonNumberNode("-101");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsLeadingZeros() throws Exception {
        JsonNumberNode.jsonNumberNode("01");
    }

    @Test
    public void handlesNonInteger() throws Exception {
        JsonNumberNode.jsonNumberNode("0.1");
    }

    @Test
    public void handlesNonIntegerWithSeveralDecimalPlaces() throws Exception {
        JsonNumberNode.jsonNumberNode("-1.112");
    }

    @Test
    public void handlesIntegerWithExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("-1e10");
    }

    @Test
    public void handlesIntegerWithNegativeExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("-1E-10");
    }

    @Test
    public void handlesIntegerWithPositiveExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("12e+10");
    }

    @Test
    public void handlesNonIntegerWithPositiveExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("-12.55e+10");
    }

    @Test
    public void handlesFacetiousZeroWithExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("-0E+99");
    }

    @Test
    public void handlesZeroExponent() throws Exception {
        JsonNumberNode.jsonNumberNode("12.231E0");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsNonIntegerWithNothingBeforeTheDecimalPoint() throws Exception {
        JsonNumberNode.jsonNumberNode(".1");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsNumberWithDecimalPointButNothingAfter() throws Exception {
        JsonNumberNode.jsonNumberNode("1.");
    }

}
