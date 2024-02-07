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

import java.util.Random;
import java.util.function.Supplier;

import static argo.TestingFactories.randomSupplierSwitcher;
import static argo.jdom.JsonNodeFactories.number;

public final class JsonNumberNodeTestingFactories {

    private static final Random RANDOM = new Random();

    private static final Supplier<String> RANDOM_NON_ZERO_DIGIT = () -> Integer.toString(RANDOM.nextInt(9) + 1);

    private static final Supplier<String> RANDOM_DIGIT = () -> Integer.toString(RANDOM.nextInt(10));

    private static final Supplier<String> RANDOM_DIGITS_WITHOUT_LEADING_ZERO = () -> {
        StringBuilder result = new StringBuilder(RANDOM_NON_ZERO_DIGIT.get());
        for (int i = 0; i < RANDOM.nextInt(10); i++) {
            result.append(RANDOM_DIGIT.get());
        }
        return result.toString();
    };

    private static final Supplier<String> RANDOM_DIGITS = () -> {
        StringBuilder result = new StringBuilder(RANDOM_DIGIT.get());
        for (int i = 0; i < RANDOM.nextInt(10); i++) {
            result.append(RANDOM_DIGIT.get());
        }
        return result.toString();
    };

    private static final Supplier<String> RANDOM_OPTIONAL_NEGATIVE_PREFIX = randomSupplierSwitcher(
            () -> "",
            () -> "-"
    );

    private static final Supplier<String> RANDOM_OPTIONAL_FRACTIONAL_PART = randomSupplierSwitcher(
            () -> "",
            () -> "." + RANDOM_DIGITS.get()
    );

    private static final Supplier<String> RANDOM_EXPONENT_SIGN = randomSupplierSwitcher(
            () -> "e",
            () -> "E"
    );

    private static final Supplier<String> RANDOM_PLUS_MINUS_OR_NOTHING = randomSupplierSwitcher(
            () -> "+",
            () -> "",
            () -> "-"
    );

    private static final Supplier<String> RANDOM_EXPONENTIAL_PART = () -> RANDOM_EXPONENT_SIGN.get() + RANDOM_PLUS_MINUS_OR_NOTHING.get() + RANDOM_DIGITS.get();

    private static final Supplier<String> RANDOM_OPTIONAL_EXPONENTIAL_PART = randomSupplierSwitcher(
            () -> "",
            RANDOM_EXPONENTIAL_PART
    );

    private static final Supplier<String> RANDOM_ZERO_OR_DIGITS = randomSupplierSwitcher(
            () -> "0",
            RANDOM_DIGITS_WITHOUT_LEADING_ZERO
    );

    public static JsonNode aNumberNode() {
        return number(aValidJsonNumber());
    }

    public static String aValidJsonNumber() {
        return RANDOM_OPTIONAL_NEGATIVE_PREFIX.get()
                + RANDOM_ZERO_OR_DIGITS.get()
                + RANDOM_OPTIONAL_FRACTIONAL_PART.get()
                + RANDOM_OPTIONAL_EXPONENTIAL_PART.get();
    }

    private JsonNumberNodeTestingFactories() {
    }
}
