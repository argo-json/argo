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

import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class UnmodifiableListArrayView<T> extends AbstractList<T> {

    private final T[] elements;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    UnmodifiableListArrayView(final T[] elements) {
        if (elements == null) {
            throw new NullPointerException();
        }
        this.elements = elements;
    }

    public T get(final int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return elements[index];
    }

    public int size() {
        return elements.length;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Iterator<T> iterator() {
        return new ArrayIterator<T>(elements);
    }

    private static final class ArrayIterator<T> extends UnmodifiableIterator<T> {

        private final T[] elements;
        private int cursor = 0;

        private ArrayIterator(final T[] elements) {
            this.elements = elements;
        }

        public boolean hasNext() {
            return cursor < elements.length;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elements[cursor++];
        }

    }

}
