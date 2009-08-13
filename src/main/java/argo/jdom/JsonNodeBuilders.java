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

/**
 * Builders for JsonNodes.
 */
public final class JsonNodeBuilders {

    private JsonNodeBuilders() {}

    public static JsonNodeBuilder<JsonNode> aJsonNull() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonNull();
            }
        };
    }

    public static JsonNodeBuilder<JsonNode> aJsonTrue() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonTrue();
            }
        };
    }

    public static JsonNodeBuilder<JsonNode> aJsonFalse() {
        return new JsonNodeBuilder<JsonNode>() {
            public JsonNode build() {
                return JsonNodeFactories.aJsonFalse();
            }
        };
    }

    public static JsonNumberNodeBuilder aJsonNumber(final String value) {
        return new JsonNumberNodeBuilder(value);
    }

    public static JsonStringNodeBuilder aJsonString(final String value) {
        return new JsonStringNodeBuilder(value);
    }

    public static JsonObjectBuilder aJsonObject() {
        return new JsonObjectBuilder();
    }

    public static JsonArrayNodeBuilder aJsonArray() {
        return new JsonArrayNodeBuilder();
    }
}
