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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder for {@code JsonNode}s representing JSON arrays.
 */
public final class JsonArrayNodeBuilder implements JsonNodeBuilder<JsonNode> {

    private final Collection<Builder<? extends JsonNode>> elementBuilders = new ArrayList<Builder<? extends JsonNode>>();

    JsonArrayNodeBuilder() {
    }

    /**
     * Adds the given element to the array that will be built.
     *
     * @param elementBuilder a builder for the element to add to the array.
     * @return the modified builder.
     */
    public JsonArrayNodeBuilder withElement(final JsonNodeBuilder<?> elementBuilder) {
        elementBuilders.add(elementBuilder);
        return this;
    }

    public JsonNode build() {
        return JsonArray.jsonArray(new BuildingCollection<JsonNode>(elementBuilders));
    }
}
