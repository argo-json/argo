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

import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonFieldNodeBuilder.aJsonFieldBuilder;

/**
 * Builder for {@code JsonNode}s representing JSON objects.
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
public abstract class JsonObjectNodeBuilder implements JsonNodeBuilder<JsonNode> {

    private JsonObjectNodeBuilder() {
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name  the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public final JsonObjectNodeBuilder withField(final String name, final JsonNodeBuilder<?> value) {
        return withField(name == null ? null : string(name), value);
    }

    /**
     * Adds a field to the object that will be built.
     *
     * @param name  the name of the field
     * @param value a builder for the value of the field.
     * @return the modified object builder.
     */
    public final JsonObjectNodeBuilder withField(final JsonStringNode name, final JsonNodeBuilder<?> value) {
        return withFieldBuilder(aJsonFieldBuilder(name, value));
    }

    abstract JsonObjectNodeBuilder withFieldBuilder(NamedJsonFieldBuilder jsonFieldBuilder);

    static final class DuplicateFieldNamePermittingJsonObjectNodeBuilder extends JsonObjectNodeBuilder {

        private final List<Builder<? extends JsonField>> fieldBuilders = new LinkedList<Builder<? extends JsonField>>(); // TODO or ArrayList?

        JsonObjectNodeBuilder withFieldBuilder(final NamedJsonFieldBuilder jsonFieldBuilder) {
            fieldBuilders.add(jsonFieldBuilder);
            return this;
        }

        public JsonNode build() {
            return object(new BuildingCollection<JsonField>(fieldBuilders));
        }
    }

    static final class DuplicateFieldNameRejectingJsonObjectNodeBuilder extends JsonObjectNodeBuilder {

        private final Map<String, Builder<? extends JsonField>> fieldBuilders = new LinkedHashMap<String, Builder<? extends JsonField>>();

        JsonObjectNodeBuilder withFieldBuilder(final NamedJsonFieldBuilder jsonFieldBuilder) {
            final String key = jsonFieldBuilder.name();
            if (fieldBuilders.containsKey(key)) {
                throw new IllegalArgumentException("Attempt to add a field with pre-existing key [" + string(key) + "]");
            } else {
                fieldBuilders.put(key, jsonFieldBuilder);
            }
            return this;
        }

        public JsonNode build() {
            return object(new BuildingCollection<JsonField>(fieldBuilders.values()));
        }
    }
}
