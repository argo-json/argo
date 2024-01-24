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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class DuplicateKeyPermittingJsonFieldIteratorBuilder<T extends JsonFieldBuilder> implements JsonFieldIteratorBuilder<T> {
    private final List<JsonFieldBuilder> fieldBuilders = new LinkedList<JsonFieldBuilder>();

    public void add(final JsonFieldBuilder jsonFieldBuilder) {
        fieldBuilders.add(jsonFieldBuilder);
    }

    public int size() {
        return fieldBuilders.size();
    }

    public Iterator<JsonField> build() {
        final Iterator<JsonFieldBuilder> delegate = fieldBuilders.iterator();
        return new UnmodifiableIterator<JsonField>() {
            public boolean hasNext() {
                return delegate.hasNext();
            }

            public JsonField next() {
                return delegate.next().build();
            }
        };
    }
}
