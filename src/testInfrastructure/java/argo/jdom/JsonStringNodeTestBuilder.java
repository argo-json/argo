/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import net.sourceforge.ickles.RandomSupplierSwitcher;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.function.Supplier;

import static argo.jdom.JsonNodeFactories.string;

public final class JsonStringNodeTestBuilder {

    private static final String ESCAPED_DOUBLE_QUOTE = "\\\"";
    private static final String ESCAPED_REVERSE_SOLIDUS = "\\\\";
    private static final Random RANDOM = new Random();

    private static final Supplier<String> RANDOM_ESCAPED_STRING = new RandomSupplierSwitcher<>(
            () -> ESCAPED_DOUBLE_QUOTE,
            () -> ESCAPED_REVERSE_SOLIDUS,
            () -> "\\/",
            () -> "\\b",
            () -> "\\f",
            () -> "\\n",
            () -> "\\r",
            () -> "\\t",
            () -> {
                final char c = (char) RANDOM.nextInt();
                return "\\u" + String.format("%04x", (long) c);
            }
    );

    private static final Supplier<String> RANDOM_JSON_STRING_SINGLE_TOKEN = new RandomSupplierSwitcher<>(
            RANDOM_ESCAPED_STRING,
            () -> {
                final String candidate = RandomStringUtils.random(1);
                if ("\"".equals(candidate)) {
                    return ESCAPED_DOUBLE_QUOTE;
                }
                if ("\\".equals(candidate)) {
                    return ESCAPED_REVERSE_SOLIDUS;
                }
                return candidate;
            }
    );

    public static JsonStringNode aStringNode() {
        return string(aValidJsonString());
    }

    public static String aValidJsonString() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < RANDOM.nextInt(20); i++) {
            result.append(RANDOM_JSON_STRING_SINGLE_TOKEN.get());
        }
        return result.toString();
    }
}
