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

import java.util.*;

import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonFieldNodeBuilder.aJsonFieldBuilder;

/**
 * Builder for {@code JsonNode}s representing JSON objects.
 */
public final class JsonObjectNodeBuilder implements JsonNodeBuilder<JsonNode> {

    private final JsonFieldIteratorBuilder<NamedJsonFieldBuilder> jsonFieldIteratorBuilder;

    private JsonObjectNodeBuilder(final JsonFieldIteratorBuilder<NamedJsonFieldBuilder> jsonFieldIteratorBuilder) {
        this.jsonFieldIteratorBuilder = jsonFieldIteratorBuilder;
    }

    static JsonObjectNodeBuilder duplicateFieldPermittingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new DuplicateKeyPermittingJsonFieldIteratorBuilder<NamedJsonFieldBuilder>());
    }

    static JsonObjectNodeBuilder duplicateFieldRejectingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new JsonFieldIteratorBuilder<NamedJsonFieldBuilder>() {
            private final Map<String, NamedJsonFieldBuilder> fieldBuilders = new LinkedHashMap<String, NamedJsonFieldBuilder>();

            public void add(final NamedJsonFieldBuilder jsonFieldBuilder) {
                final String key = jsonFieldBuilder.name();
                if (fieldBuilders.containsKey(key)) {
                    throw new IllegalArgumentException("Attempt to add a field with pre-existing key [" + string(key) + "]");
                } else {
                    fieldBuilders.put(key, jsonFieldBuilder);
                }
            }

            public int size() {
                return fieldBuilders.size();
            }

            public Iterator<JsonField> build() {
                final Iterator<Map.Entry<String, NamedJsonFieldBuilder>> delegate = fieldBuilders.entrySet().iterator();
                return new UnmodifiableIterator<JsonField>() {
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    public JsonField next() {
                        return delegate.next().getValue().build();
                    }
                };
            }
        });
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
        jsonFieldIteratorBuilder.add(jsonFieldBuilder);
        return this;
    }

    public JsonNode build() {
        return JsonObject.jsonObject(jsonFieldIteratorBuilder.build(), jsonFieldIteratorBuilder.size());
    }

}
