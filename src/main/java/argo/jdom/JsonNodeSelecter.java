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

import static argo.jdom.JsonNodeType.*;

import java.util.List;

public final class JsonNodeSelecter<T> {

    private final JsonNodeFunctor<T> jsonValueGetter;

    private JsonNodeSelecter(JsonNodeFunctor<T> jsonValueGetter) {
        this.jsonValueGetter = jsonValueGetter;
    }

    public static JsonNodeSelecter<String> aStringNode() {
        return new JsonNodeSelecter<String>(new JsonNodeFunctor<String>() {
            public boolean matchesValue(JsonNode jsonNode) {
                return STRING == jsonNode.getType();
            }

            public String getValue(final JsonNode jsonNode) {
                return jsonNode.getText();
            }
        });
    }

    public static JsonNodeSelecter<String> aNumberNode() {
        return new JsonNodeSelecter<String>(new JsonNodeFunctor<String>() {
            public boolean matchesValue(JsonNode jsonNode) {
                return NUMBER == jsonNode.getType();
            }

            public String getValue(final JsonNode jsonNode) {
                return jsonNode.getText();
            }
        });
    }

    public static JsonNodeSelecter<List<JsonNode>> anArrayNode() {
        return new JsonNodeSelecter<List<JsonNode>> (new JsonNodeFunctor<List<JsonNode>>() {
            public boolean matchesValue(JsonNode jsonNode) {
                return ARRAY == jsonNode.getType();
            }

            public List<JsonNode> getValue(final JsonNode jsonNode) {
                return jsonNode.getElements();
            }
        });
    }

    public static JsonNodeSelecter<JsonNode> anArrayNode(final int i) {
        return new JsonNodeSelecter<JsonNode>(new JsonNodeFunctor<JsonNode>() {
            public boolean matchesValue(JsonNode jsonNode) {
                return ARRAY == jsonNode.getType() && jsonNode.getElements().size() > i;
            }

            public JsonNode getValue(JsonNode jsonNode) {
                return jsonNode.getElements().get(i);
            }
        });
    }

    public boolean matches(final JsonNode jsonNode) {
        return jsonValueGetter.matchesValue(jsonNode);
    }

    public T getValue(JsonNode jsonNode) {
        if (!matches(jsonNode)) {
            throw new IllegalArgumentException("JsonNode [" + jsonNode + "] is not the expected type.");
        } else {
            return jsonValueGetter.getValue(jsonNode);
        }
    }

    private interface JsonNodeFunctor<T> {
        boolean matchesValue(JsonNode jsonNode);
        T getValue(JsonNode jsonNode);
    }
}
