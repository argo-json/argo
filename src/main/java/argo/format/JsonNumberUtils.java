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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Helper methods for converting JSON number {@code String}s into Java numeric objects.
 *
 * @deprecated Replaced by {@link argo.jdom.JsonNumberUtils}.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class JsonNumberUtils {

    private JsonNumberUtils() {
    }

    /**
     * Helper method for converting JSON number {@code String}s into {@code BigDecimal}s.
     *
     * @param jsonNumberString a {@code String} representation of a JSON number.
     * @return a {@code BigDecimal} representing the given JSON number {@code String}.
     * @throws NumberFormatException if the given {@code String} is not a valid JSON number.
     * @deprecated Replaced by {@link argo.jdom.JsonNumberUtils#asBigDecimal(String)}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static BigDecimal asBigDecimal(final String jsonNumberString) {
        return argo.jdom.JsonNumberUtils.asBigDecimal(jsonNumberString);
    }

    /**
     * Helper method for converting JSON number {@code String}s into {@code BigInteger}s.
     *
     * @param jsonNumberString a {@code String} representation of an integer JSON number.
     * @return a {@code BigInteger} representing the given JSON number {@code String}.
     * @throws NumberFormatException if the given {@code String} is not a valid JSON number or is not an integer.
     * @deprecated Replaced by {@link argo.jdom.JsonNumberUtils#asBigInteger(String)}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static BigInteger asBigInteger(final String jsonNumberString) {
        return argo.jdom.JsonNumberUtils.asBigInteger(jsonNumberString);
    }

    /**
     * Helper method for converting JSON number {@code String}s into {@code Double}s.  Note that use of this
     * method is risky as java {@code Double}s have limited precision, whereas JSON numbers have unlimited
     * precision, i.e. converting a JSON number to a {@code Double} may result is a loss of accuracy.
     *
     * @param jsonNumberString a {@code String} representation of an integer JSON number.
     * @return a {@code Double} representing the given JSON number {@code String}.
     * @throws NumberFormatException if the given {@code String} is not a valid JSON number.
     * @deprecated Replaced by {@link argo.jdom.JsonNumberUtils#asDouble(String)}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static Double asDouble(final String jsonNumberString) {
        return argo.jdom.JsonNumberUtils.asDouble(jsonNumberString);
    }

    /**
     * Helper method for converting JSON number {@code String}s into {@code Integer}s.  Note that use of this
     * method is risky as java {@code Integer}s have limited precision (between {@code Integer.MIN_VALUE} and
     * {@code Integer.MAX_VALUE}) whereas JSON numbers have unlimited precision.
     *
     * @param jsonNumberString a {@code String} representation of an integer JSON number.
     * @return a {@code Integer} representing the given JSON number {@code String}.
     * @throws NumberFormatException if the given {@code String} is not a valid JSON number or is not an integer.
     * @deprecated Replaced by {@link argo.jdom.JsonNumberUtils#asInteger(String)}
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static Integer asInteger(final String jsonNumberString) {
        return argo.jdom.JsonNumberUtils.asInteger(jsonNumberString);
    }
}
