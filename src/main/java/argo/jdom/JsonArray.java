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

import java.util.Collections;
import java.util.List;

import static argo.jdom.ImmutableListFactories.immutableListOf;

final class JsonArray extends AbstractJsonArray {

    private static final JsonArray EMPTY_ARRAY = new JsonArray(Collections.<JsonNode>emptyList());

    private final List<JsonNode> elements;

    static JsonArray jsonArray(final Iterable<? extends JsonNode> elements) {
        final List<JsonNode> elementList = immutableListOf(elements);
        return elementList.isEmpty() ? EMPTY_ARRAY : new JsonArray(elementList);
    }

    private JsonArray(List<JsonNode> elements) {
        this.elements = elements;
    }

    public List<JsonNode> getElements() {
        return elements;
    }

}
