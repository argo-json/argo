/*
 * Copyright 2015 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

final class ImmutableListFactories {

    private ImmutableListFactories() {
    }

    static <T> List<T> immutableListOf(final Iterable<? extends T> elements) {
        if (elements instanceof Collection) {
            return unmodifiableList(new ArrayList<T>((Collection<? extends T>) elements));
        } else {
            return immutableListOf(elements.iterator());
        }
    }

    static <T> List<T> immutableListOf(Iterator<? extends T> elements) {
        final List<T> copy = new ArrayList<T>();
        while (elements.hasNext()) {
            copy.add(elements.next());
        }
        return unmodifiableList(copy);
    }
}
