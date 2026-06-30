/*
 *  Copyright 2026 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.internal;

public final class FastStack<T> {

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private Object[] elements = new Object[16];
    private int current = -1;

    public void push(final T element) {
        current++;
        ensureIndex(current);
        elements[current] = element;
    }

    private void ensureIndex(final int index) {
        if (elements.length <= index) {
            // inspired by java.util.ArrayDeque, grow small stacks by doubling, and larger by increasing by 50%
            final int newSize;
            if (elements.length < 64) {
                newSize = elements.length << 1;
            } else {
                final int unvalidatedSize = elements.length + (elements.length >> 1);
                if (unvalidatedSize <= MAX_ARRAY_SIZE && unvalidatedSize > 0) {
                    newSize = unvalidatedSize;
                } else {
                    if (index < MAX_ARRAY_SIZE) {
                        newSize = MAX_ARRAY_SIZE;
                    } else {
                        throw new IllegalStateException("Stack depth exceeded maximum: " + MAX_ARRAY_SIZE);
                    }
                }
            }
            final Object[] newElements = new Object[newSize];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) elements[current];
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        final T result = (T) elements[current];
        elements[current] = null;
        current--;
        return result;
    }
}
