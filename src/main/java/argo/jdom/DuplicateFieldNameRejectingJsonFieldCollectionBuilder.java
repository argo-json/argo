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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.string;

class DuplicateFieldNameRejectingJsonFieldCollectionBuilder implements JsonFieldCollectionBuilder<NamedJsonFieldBuilder> {
    private final Map<String, Builder<? extends JsonField>> fieldBuilders = new LinkedHashMap<String, Builder<? extends JsonField>>();

    public void add(final NamedJsonFieldBuilder jsonFieldBuilder) {
        final String key = jsonFieldBuilder.name();
        if (fieldBuilders.containsKey(key)) {
            throw new IllegalArgumentException("Attempt to add a field with pre-existing key [" + string(key) + "]");
        } else {
            fieldBuilders.put(key, jsonFieldBuilder);
        }
    }

    public Collection<JsonField> build() {
        return new BuildingCollection<JsonField>(fieldBuilders.values());
    }
}
