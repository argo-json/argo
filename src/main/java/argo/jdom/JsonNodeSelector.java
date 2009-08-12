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

import static argo.jdom.JsonNodeFactory.aJsonString;
import static argo.jdom.JsonNodeType.*;

import java.util.List;
import java.util.Map;

public final class JsonNodeSelector<T, U> {

    private final Functor<T, U> valueGetter;

    private JsonNodeSelector(final Functor<T, U> valueGetter) {
        this.valueGetter = valueGetter;
    }

    public boolean matches(final T jsonNode) {
        return valueGetter.matchesValue(jsonNode);
    }

    public U getValue(final T argument) {
        if (!matches(argument)) {
            throw new IllegalArgumentException("Argument [" + argument + "] does not match the requirments of this selector.");
        } else {
            return valueGetter.getValue(argument);
        }
    }

    public static JsonNodeSelector<JsonNode, String> aStringNode() {
        return new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return STRING == jsonNode.getType();
            }

            public String getValue(final JsonNode jsonNode) {
                return jsonNode.getText();
            }
        });
    }

    public static JsonNodeSelector<JsonNode, String> aNumberNode() {
        return new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return NUMBER == jsonNode.getType();
            }

            public String getValue(final JsonNode jsonNode) {
                return jsonNode.getText();
            }
        });
    }

    public static JsonNodeSelector<JsonNode, Boolean> aBooleanNode() {
        return new JsonNodeSelector<JsonNode, Boolean>(new Functor<JsonNode, Boolean>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return TRUE == jsonNode.getType() || FALSE == jsonNode.getType();
            }

            public Boolean getValue(final JsonNode jsonNode) {
                return TRUE == jsonNode.getType();
            }
        });
    }

    public static JsonNodeSelector<JsonNode, JsonNode> aNullNode() {
        return new JsonNodeSelector<JsonNode, JsonNode>(new Functor<JsonNode, JsonNode>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return NULL == jsonNode.getType();
            }

            public JsonNode getValue(final JsonNode jsonNode) {
                return jsonNode;
            }
        });
    }

    public static JsonNodeSelector<JsonNode, List<JsonNode>> anArrayNode() {
        return new JsonNodeSelector<JsonNode, List<JsonNode>>(new Functor<JsonNode, List<JsonNode>>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return ARRAY == jsonNode.getType();
            }

            public List<JsonNode> getValue(final JsonNode jsonNode) {
                return jsonNode.getElements();
            }
        });
    }

    public static JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> anObjectNode() {
        return new JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>>(new Functor<JsonNode, Map<JsonStringNode, JsonNode>>() {
            public boolean matchesValue(final JsonNode jsonNode) {
                return OBJECT == jsonNode.getType();
            }

            public Map<JsonStringNode, JsonNode> getValue(final JsonNode jsonNode) {
                return jsonNode.getFields();
            }
        });
    }

    public static JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> aField(final String fieldName) {
        final JsonStringNode jsonStringFieldName = aJsonString(fieldName);
        return new JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode>(new Functor<Map<JsonStringNode, JsonNode>, JsonNode>() {
            public boolean matchesValue(Map<JsonStringNode, JsonNode> jsonNode) {
                return jsonNode.containsKey(jsonStringFieldName);
            }

            public JsonNode getValue(Map<JsonStringNode, JsonNode> jsonNode) {
                return jsonNode.get(jsonStringFieldName);
            }
        });
    }

    public static JsonNodeSelector<List<JsonNode>, JsonNode> anElement(final int index) {
        return new JsonNodeSelector<List<JsonNode>, JsonNode>(new Functor<List<JsonNode>, JsonNode>() {
            public boolean matchesValue(final List<JsonNode> jsonNode) {
                return jsonNode.size() > index;
            }

            public JsonNode getValue(final List<JsonNode> jsonNode) {
                return jsonNode.get(index);
            }
        });
    }

    public <V> JsonNodeSelector<T, V> withChild(final JsonNodeSelector<U, V> childJsonNodeSelector) {
        return new JsonNodeSelector<T,V>(new chainedFunctor<T, V, U>(this, childJsonNodeSelector));
    }

    private interface Functor<W, X> {

        boolean matchesValue(W jsonNode);
        X getValue(W jsonNode);
    }

    private static final class chainedFunctor<T, V, U> implements Functor<T, V> {
        private final JsonNodeSelector<T, U> parentJsonNodeSelector;
        private final JsonNodeSelector<U, V> childJsonNodeSelector;

        public chainedFunctor(final JsonNodeSelector<T, U> parentJsonNodeSelector, final JsonNodeSelector<U, V> childJsonNodeSelector) {
            this.parentJsonNodeSelector = parentJsonNodeSelector;
            this.childJsonNodeSelector = childJsonNodeSelector;
        }

        public boolean matchesValue(final T jsonNode) {
            return parentJsonNodeSelector.matches(jsonNode) && childJsonNodeSelector.matches(parentJsonNodeSelector.getValue(jsonNode));
        }

        public V getValue(final T jsonNode) {
            return childJsonNodeSelector.getValue(parentJsonNodeSelector.getValue(jsonNode));
        }
    }
}
