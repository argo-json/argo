/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("deprecation")
final class JsonNumberUtilsTest {

    @Test
    @SuppressWarnings("ConstantValue")
    void canParseJsonNumberStringToBigDecimal() {
        assertThat(JsonNumberUtils.asBigDecimal(null), is(nullValue()));
        assertThat(JsonNumberUtils.asBigDecimal("-0"), equalTo(BigDecimal.ZERO));
        assertThat(JsonNumberUtils.asBigDecimal("+0"), equalTo(BigDecimal.ZERO));
        assertThat(JsonNumberUtils.asBigDecimal("0.0"), equalTo(new BigDecimal("0.0")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e-0"), equalTo(new BigDecimal("0.0")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e+0"), equalTo(new BigDecimal("0.0")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e0"), equalTo(new BigDecimal("0.0")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e-10"), equalTo(new BigDecimal("0.00000000000")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e+10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0e10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0E-10"), equalTo(new BigDecimal("0.00000000000")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0E+10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(JsonNumberUtils.asBigDecimal("0.0E10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(JsonNumberUtils.asBigDecimal("10"), equalTo(BigDecimal.TEN));
        assertThat(JsonNumberUtils.asBigDecimal("-10"), equalTo(new BigDecimal("-10")));
        assertThat(JsonNumberUtils.asBigDecimal("10.2"), equalTo(new BigDecimal("10.2")));
        assertThat(JsonNumberUtils.asBigDecimal("-0.1234"), equalTo(new BigDecimal("-0.1234")));
    }

    @Test
    void canParseJsonNumberStringToBigInteger() {
        assertThat(JsonNumberUtils.asBigInteger(null), is(nullValue()));
        assertThat(JsonNumberUtils.asBigInteger("-0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("+0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0E10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e-0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e+0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e0"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e-10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e+10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0e10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0E-10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("0.0E+10"), equalTo(BigInteger.ZERO));
        assertThat(JsonNumberUtils.asBigInteger("10"), equalTo(BigInteger.TEN));
        assertThat(JsonNumberUtils.asBigInteger("-10"), equalTo(new BigInteger("-10")));
        assertThat(JsonNumberUtils.asBigInteger("10E5"), equalTo(new BigInteger("1000000")));
    }

    @Test
    void canParseJsonNumberStringToDouble() {
        assertThat(JsonNumberUtils.asDouble(null), is(nullValue()));
        assertThat(JsonNumberUtils.asDouble("-0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("+0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e-0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e+0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e0"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e-10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e+10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0e10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0E-10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0E+10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("0.0E10"), equalTo(0.0));
        assertThat(JsonNumberUtils.asDouble("10"), equalTo(10.0));
        assertThat(JsonNumberUtils.asDouble("-10"), equalTo(-10.0));
        assertThat(JsonNumberUtils.asDouble("10.2"), equalTo(10.2));
        assertThat(JsonNumberUtils.asDouble("-0.1234"), equalTo(-0.1234));
    }

    @Test
    void canParseJsonNumberStringToInteger() {
        assertThat(JsonNumberUtils.asInteger(null), is(nullValue()));
        assertThat(JsonNumberUtils.asInteger("-0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("+0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e-0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e+0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e0"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e-10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e+10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0e10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0E-10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0E+10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("0.0E10"), equalTo(0));
        assertThat(JsonNumberUtils.asInteger("10"), equalTo(10));
        assertThat(JsonNumberUtils.asInteger("-10"), equalTo(-10));
        assertThat(JsonNumberUtils.asInteger("10E5"), equalTo(1_000_000));
    }

    @Test
    void parseJsonNumberStringToBigIntegerRejectsNonIntegerNumbers() {
        assertThrows(NumberFormatException.class, () -> JsonNumberUtils.asBigInteger("10.2"));
    }

    @Test
    void parseJsonNumberStringToIntegerRejectsDecimalNumbers() {
        assertThrows(NumberFormatException.class, () -> JsonNumberUtils.asInteger("10.2"));
    }
}
