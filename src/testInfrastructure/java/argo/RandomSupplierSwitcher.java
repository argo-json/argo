/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import com.google.common.base.Supplier;

import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.asList;

public final class RandomSupplierSwitcher<T> implements Supplier<T> {

    private static final Random RANDOM = new Random();

    private final List<Supplier<T>> suppliers;

    public RandomSupplierSwitcher(final Supplier<T> supplier, final Supplier<T>... suppliers) {
        this.suppliers = asList(supplier, suppliers);
    }

    public T get() {
        return suppliers.get(RANDOM.nextInt(suppliers.size())).get();
    }
}
