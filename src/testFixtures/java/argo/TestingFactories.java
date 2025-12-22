/*
 *  Copyright 2025 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import net.sourceforge.ickles.RandomSupplierSwitcher;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.function.Supplier;

public final class TestingFactories {

    private static final Random RANDOM = new Random();

    private TestingFactories() {
    }

    @SafeVarargs
    public static <T> Supplier<T> randomSupplierSwitcher(final Supplier<T> supplier, final Supplier<T>... suppliers) {
        return new RandomSupplierSwitcher<>(supplier, suppliers);
    }

    public static int aSmallNonNegativeInt() {
        return RANDOM.nextInt(20);
    }

    public static String aString() {
        return aStringOfLength(aSmallNonNegativeInt());
    }

    public static String aStringOfLength(final int length) {
        return RandomStringUtils.insecure().next(length);
    }
}
