/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.HashMap;
import java.util.Map;

public final class JsonObjectBuilder implements JsonNodeBuilder<JsonRootNode> {

    private final Map<JsonNodeBuilder<JsonStringNode>, JsonNodeBuilder> fieldBuilders = new HashMap<JsonNodeBuilder<JsonStringNode>, JsonNodeBuilder>();

    JsonObjectBuilder() {}

    public JsonObjectBuilder withField(final String name, final JsonNodeBuilder value) {
        return withField(JsonNodeBuilders.aJsonString(name), value);
    }

    public JsonObjectBuilder withField(final JsonNodeBuilder<JsonStringNode> name, final JsonNodeBuilder value) {
        fieldBuilders.put(name, value);
        return this;
    }

    public JsonRootNode build() {
        final HashMap<JsonStringNode, JsonNode> fields = new HashMap<JsonStringNode, JsonNode>();
        for (Map.Entry<JsonNodeBuilder<JsonStringNode>, JsonNodeBuilder> fieldBuilder : fieldBuilders.entrySet()) {
            fields.put(fieldBuilder.getKey().build(), fieldBuilder.getValue().build());
        }
        return JsonNodeFactories.aJsonObject(fields);
    }
}
