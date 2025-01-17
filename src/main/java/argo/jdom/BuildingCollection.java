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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

final class BuildingCollection<T> extends AbstractCollection<T> {

    private final Collection<Builder<? extends T>> builders;

    BuildingCollection(final Collection<Builder<? extends T>> builders) {
        if (builders == null) {
            throw new NullPointerException();
        }
        this.builders = builders;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Iterator<T> iterator() {
        final Iterator<Builder<? extends T>> builderIterator = builders.iterator();
        return new UnmodifiableIterator<T>() {
            public boolean hasNext() {
                return builderIterator.hasNext();
            }

            public T next() {
                return builderIterator.next().build();
            }
        };
    }

    @Override
    public int size() {
        return builders.size();
    }
}
