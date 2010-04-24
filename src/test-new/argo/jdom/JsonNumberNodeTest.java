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
        new JsonNumberNode("0");
    }

    @Test
    public void handlesMinusZero() throws Exception {
        new JsonNumberNode("-0");
    }

    @Test
    public void handlesSingleDigitInteger() throws Exception {
        new JsonNumberNode("2");
    }

    @Test
    public void handlesMultiDigitInteger() throws Exception {
        new JsonNumberNode("101");
    }

    @Test
    public void handlesMultiDigitNegativeInteger() throws Exception {
        new JsonNumberNode("-101");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsLeadingZeros() throws Exception {
        new JsonNumberNode("01");
    }

    @Test
    public void handlesNonInteger() throws Exception {
        new JsonNumberNode("0.1");
    }

    @Test
    public void handlesNonIntegerWithSeveralDecimalPlaces() throws Exception {
        new JsonNumberNode("-1.112");
    }

    @Test
    public void handlesIntegerWithExponent() throws Exception {
        new JsonNumberNode("-1e10");
    }

    @Test
    public void handlesIntegerWithNegativeExponent() throws Exception {
        new JsonNumberNode("-1E-10");
    }

    @Test
    public void handlesIntegerWithPositiveExponent() throws Exception {
        new JsonNumberNode("12e+10");
    }

    @Test
    public void handlesNonIntegerWithPositiveExponent() throws Exception {
        new JsonNumberNode("-12.55e+10");
    }

    @Test
    public void handlesFacetiousZeroWithExponent() throws Exception {
        new JsonNumberNode("-0E+99");
    }

    @Test
    public void handlesZeroExponent() throws Exception {
        new JsonNumberNode("12.231E0");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsNonIntegerWithNothingBeforeTheDecimalPoint() throws Exception {
        new JsonNumberNode(".1");
    }

    @Test (expected = IllegalArgumentException.class)
    public void rejectsNumberWithDecimalPointButNothingAfter() throws Exception {
        new JsonNumberNode("1.");
    }

}
