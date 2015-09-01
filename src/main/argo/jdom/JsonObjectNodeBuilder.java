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

import java.util.*;

import static argo.jdom.JsonFieldBuilder.aJsonFieldBuilder;

/**
 * Builder for {@code JsonRootNode}s representing JSON objects.
 */
public final class JsonObjectNodeBuilder implements JsonNodeBuilder<JsonRootNode> {

    private final FieldCollector fieldCollector;

    private JsonObjectNodeBuilder(final FieldCollector fieldCollector) {
        this.fieldCollector = fieldCollector;
    }

    static JsonObjectNodeBuilder duplicateFieldPermittingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new FieldCollector() {
            private final List<JsonFieldBuilder> fieldBuilders = new LinkedList<JsonFieldBuilder>();

            public void add(final JsonFieldBuilder jsonFieldBuilder) {
                fieldBuilders.add(jsonFieldBuilder);
            }

            public int size() {
                return fieldBuilders.size();
            }

            public Iterator<JsonField> iterator() {
                final Iterator<JsonFieldBuilder> delegate = fieldBuilders.iterator();
                return new Iterator<JsonField>() {
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    public JsonField next() {
                        return delegate.next().build();
                    }

                    public void remove() {
                        delegate.remove();
                    }
                };
            }
        });
    }

    static JsonObjectNodeBuilder duplicateFieldRejectingJsonObjectNodeBuilder() {
        return new JsonObjectNodeBuilder(new FieldCollector() {
            private final Map<JsonStringNode, JsonFieldBuilder> fieldBuilders = new LinkedHashMap<JsonStringNode, JsonFieldBuilder>();

            public void add(final JsonFieldBuilder jsonFieldBuilder) {
                final JsonStringNode key = jsonFieldBuilder.buildKey();
                if (fieldBuilders.containsKey(key)) {
                    throw new IllegalArgumentException("Attempt to add a field with pre-existing key [" + key + "]");
                } else {
                    fieldBuilders.put(key, jsonFieldBuilder);
                }
            }

            public int size() {
                return fieldBuilders.size();
            }

            public Iterator<JsonField> iterator() {
                final Iterator<Map.Entry<JsonStringNode, JsonFieldBuilder>> delegate = fieldBuilders.entrySet().iterator();
                return new Iterator<JsonField>() {
                    public boolean hasNext() {
                        return delegate.hasNext();
                    }

                    public JsonField next() {
                        return delegate.next().getValue().build();
                    }

                    public void remove() {
                        delegate.remove();
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
    public JsonObjectNodeBuilder withField(final String name, final JsonNodeBuilder value) {
        return withField(JsonNodeFactories.string(name), value);
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name  a builder for the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withField(final JsonStringNode name, final JsonNodeBuilder value) {
        return withFieldBuilder(aJsonFieldBuilder().withKey(name).withValue(value));
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param jsonFieldBuilder a builder for the field.
     * @return the modified object builder.
     */
    public JsonObjectNodeBuilder withFieldBuilder(final JsonFieldBuilder jsonFieldBuilder) {
        fieldCollector.add(jsonFieldBuilder);
        return this;
    }

    public JsonRootNode build() {
        return JsonNodeFactories.object(new ArrayList<JsonField>(fieldCollector.size()) {{
            for (final JsonField field : fieldCollector) {
                this.add(field);
            }
        }});
    }

    private interface FieldCollector extends Iterable<JsonField> {
        void add(JsonFieldBuilder jsonFieldBuilder);

        int size();
    }
}
