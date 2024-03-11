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

import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonFieldNodeBuilder.aJsonFieldBuilder;

/**
 * Builder for {@code JsonNode}s representing JSON objects.
 */
public final class JsonObjectNodeBuilder implements JsonNodeBuilder<JsonNode> {

    private final JsonFieldCollectionBuilder<? super NamedJsonFieldBuilder> jsonFieldCollectionBuilder;

    private JsonObjectNodeBuilder(final JsonFieldCollectionBuilder<? super NamedJsonFieldBuilder> jsonFieldCollectionBuilder) {
        this.jsonFieldCollectionBuilder = jsonFieldCollectionBuilder;
    }

    static JsonObjectNodeBuilder duplicateFieldNamePermittingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new DuplicateFieldNamePermittingJsonFieldCollectionBuilder());
    }

    static JsonObjectNodeBuilder duplicateFieldNameRejectingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new DuplicateFieldNameRejectingJsonFieldCollectionBuilder());
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name  the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withField(final String name, final JsonNodeBuilder<?> value) {
        return withField(name == null ? null : string(name), value);
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name  a builder for the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withField(final JsonStringNode name, final JsonNodeBuilder<?> value) {
        return withFieldBuilder(aJsonFieldBuilder(name, value));
    }

    JsonObjectNodeBuilder withFieldBuilder(final NamedJsonFieldBuilder jsonFieldBuilder) {
        jsonFieldCollectionBuilder.add(jsonFieldBuilder);
        return this;
    }

    public JsonNode build() {
        return JsonObject.jsonObject(jsonFieldCollectionBuilder.build());
    }

}
