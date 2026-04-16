/*
 *  Copyright 2026 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

final class ArrayFactories {

    private ArrayFactories() {
    }

    static <T> T[] nullFreeArrayOf(final Iterable<? extends T> elements, final T[] a) {
        final T[] result;
        if (elements instanceof Collection) {
            final Collection<? extends T> elementsCollection = (Collection<? extends T>) elements;
            if (elementsCollection.isEmpty()) {
                if (a == null) {
                    throw new NullPointerException();
                }
                result = a;
            } else {
                final T[] array = elementsCollection.toArray(a);
                for (final T element : array) {
                    if (element == null) {
                        throw new NullPointerException();
                    }
                }
                result = array;
            }
        } else {
            result = nullFreeArrayOf(elements.iterator(), a);
        }
        return result;
    }

    static <T> T[] nullFreeArrayOf(final Iterator<? extends T> elements, final T[] a) {
        final List<T> arrayBuildingList = new ArrayList<T>();
        while (elements.hasNext()) {
            final T next = elements.next();
            if (next == null) {
                throw new NullPointerException();
            }
            arrayBuildingList.add(next);
        }
        return arrayBuildingList.toArray(a);
    }

}