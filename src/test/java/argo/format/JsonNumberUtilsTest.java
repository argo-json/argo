/*
 *  Copyright  2020 Mark Slater
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

import static argo.format.JsonNumberUtils.asBigDecimal;
import static argo.format.JsonNumberUtils.asBigInteger;
import static argo.format.JsonNumberUtils.asDouble;
import static argo.format.JsonNumberUtils.asInteger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNumberUtilsTest {

    @Test
    void canParseJsonNumberStringToBigDecimal() {
        assertThat(asBigDecimal(null), is(nullValue()));
        assertThat(asBigDecimal("-0"), equalTo(BigDecimal.ZERO));
        assertThat(asBigDecimal("+0"), equalTo(BigDecimal.ZERO));
        assertThat(asBigDecimal("0.0"), equalTo(new BigDecimal("0.0")));
        assertThat(asBigDecimal("0.0e-0"), equalTo(new BigDecimal("0.0")));
        assertThat(asBigDecimal("0.0e+0"), equalTo(new BigDecimal("0.0")));
        assertThat(asBigDecimal("0.0e0"), equalTo(new BigDecimal("0.0")));
        assertThat(asBigDecimal("0.0e-10"), equalTo(new BigDecimal("0.00000000000")));
        assertThat(asBigDecimal("0.0e+10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(asBigDecimal("0.0e10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(asBigDecimal("0.0E-10"), equalTo(new BigDecimal("0.00000000000")));
        assertThat(asBigDecimal("0.0E+10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(asBigDecimal("0.0E10"), equalTo(new BigDecimal("0.0E10")));
        assertThat(asBigDecimal("10"), equalTo(BigDecimal.TEN));
        assertThat(asBigDecimal("-10"), equalTo(new BigDecimal("-10")));
        assertThat(asBigDecimal("10.2"), equalTo(new BigDecimal("10.2")));
        assertThat(asBigDecimal("-0.1234"), equalTo(new BigDecimal("-0.1234")));
    }

    @Test
    void canParseJsonNumberStringToBigInteger() {
        assertThat(asBigInteger(null), is(nullValue()));
        assertThat(asBigInteger("-0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("+0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0E10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e-0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e+0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e0"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e-10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e+10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0e10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0E-10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("0.0E+10"), equalTo(BigInteger.ZERO));
        assertThat(asBigInteger("10"), equalTo(BigInteger.TEN));
        assertThat(asBigInteger("-10"), equalTo(new BigInteger("-10")));
        assertThat(asBigInteger("10E5"), equalTo(new BigInteger("1000000")));
    }

    @Test
    void canParseJsonNumberStringToDouble() {
        assertThat(asDouble(null), is(nullValue()));
        assertThat(asDouble("-0"), equalTo(0.0));
        assertThat(asDouble("+0"), equalTo(0.0));
        assertThat(asDouble("0.0"), equalTo(0.0));
        assertThat(asDouble("0.0e-0"), equalTo(0.0));
        assertThat(asDouble("0.0e+0"), equalTo(0.0));
        assertThat(asDouble("0.0e0"), equalTo(0.0));
        assertThat(asDouble("0.0e-10"), equalTo(0.0));
        assertThat(asDouble("0.0e+10"), equalTo(0.0));
        assertThat(asDouble("0.0e10"), equalTo(0.0));
        assertThat(asDouble("0.0E-10"), equalTo(0.0));
        assertThat(asDouble("0.0E+10"), equalTo(0.0));
        assertThat(asDouble("0.0E10"), equalTo(0.0));
        assertThat(asDouble("10"), equalTo(10.0));
        assertThat(asDouble("-10"), equalTo(-10.0));
        assertThat(asDouble("10.2"), equalTo(10.2));
        assertThat(asDouble("-0.1234"), equalTo(-0.1234));
    }

    @Test
    void canParseJsonNumberStringToInteger() {
        assertThat(asInteger(null), is(nullValue()));
        assertThat(asInteger("-0"), equalTo(0));
        assertThat(asInteger("+0"), equalTo(0));
        assertThat(asInteger("0.0"), equalTo(0));
        assertThat(asInteger("0.0e-0"), equalTo(0));
        assertThat(asInteger("0.0e+0"), equalTo(0));
        assertThat(asInteger("0.0e0"), equalTo(0));
        assertThat(asInteger("0.0e-10"), equalTo(0));
        assertThat(asInteger("0.0e+10"), equalTo(0));
        assertThat(asInteger("0.0e10"), equalTo(0));
        assertThat(asInteger("0.0E-10"), equalTo(0));
        assertThat(asInteger("0.0E+10"), equalTo(0));
        assertThat(asInteger("0.0E10"), equalTo(0));
        assertThat(asInteger("10"), equalTo(10));
        assertThat(asInteger("-10"), equalTo(-10));
        assertThat(asInteger("10E5"), equalTo(1_000_000));
    }

    @Test
    void parseJsonNumberStringToBigIntegerRejectsNonIntegerNumbers() {
        assertThrows(NumberFormatException.class, () -> asBigInteger("10.2"));
    }

    @Test
    void parseJsonNumberStringToIntegerRejectsDecimalNumbers() {
        assertThrows(NumberFormatException.class, () -> asInteger("10.2"));
    }
}
