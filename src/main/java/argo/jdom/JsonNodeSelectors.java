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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class JsonNodeSelectors {

    private JsonNodeSelectors() {}

    public static JsonNodeSelector<JsonNode, String> aStringNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return STRING == jsonNode.getType();
            }

            public String applyTo(final JsonNode jsonNode) {
                return jsonNode.getText();
            }

            @Override
            public String toString() {
                return ("a value that is a string");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, String> aNullableStringNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return STRING == jsonNode.getType() || NULL == jsonNode.getType();
            }

            public String applyTo(final JsonNode jsonNode) {
                return NULL == jsonNode.getType() ? null : jsonNode.getText();
            }

            @Override
            public String toString() {
                return ("a value that is a string");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, String> aNumberNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return NUMBER == jsonNode.getType();
            }

            public String applyTo(final JsonNode jsonNode) {
                return jsonNode.getText();
            }

            @Override
            public String toString() {
                return ("a value that is a number");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, String> aNullableNumberNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, String>(new Functor<JsonNode, String>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return NUMBER == jsonNode.getType() || NULL == jsonNode.getType();
            }

            public String applyTo(final JsonNode jsonNode) {
                return NULL == jsonNode.getType() ? null : jsonNode.getText();
            }

            @Override
            public String toString() {
                return ("a value that is a number");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, Boolean> aBooleanNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, Boolean>(new Functor<JsonNode, Boolean>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return TRUE == jsonNode.getType() || FALSE == jsonNode.getType();
            }

            public Boolean applyTo(final JsonNode jsonNode) {
                return TRUE == jsonNode.getType();
            }

            @Override
            public String toString() {
                return ("a true or false");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, Boolean> aNullableBooleanNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, Boolean>(new Functor<JsonNode, Boolean>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return TRUE == jsonNode.getType() || FALSE == jsonNode.getType() || NULL == jsonNode.getType();
            }

            public Boolean applyTo(final JsonNode jsonNode) {
                final Boolean result;
                if (TRUE == jsonNode.getType()) {
                    result = Boolean.TRUE;
                } else if (FALSE == jsonNode.getType()) {
                    result = Boolean.FALSE;
                } else {
                    result = null;
                }
                return result;
            }

            @Override
            public String toString() {
                return ("a true or false");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, JsonNode> aNullNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, JsonNode>(new Functor<JsonNode, JsonNode>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return argo.jdom.JsonNodeType.NULL == jsonNode.getType();
            }

            public JsonNode applyTo(final JsonNode jsonNode) {
                return jsonNode;
            }

            @Override
            public String toString() {
                return ("a null");
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, List<JsonNode>> anArrayNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, List<JsonNode>>(new Functor<JsonNode, List<JsonNode>>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return argo.jdom.JsonNodeType.ARRAY == jsonNode.getType();
            }

            public List<JsonNode> applyTo(final JsonNode jsonNode) {
                return jsonNode.getElements();
            }

            @Override
            public String toString() {
                return "an array";
            }
        }));
    }

    public static JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> anObjectNode(final Object... pathElements) {
        return chainOn(pathElements, new JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>>(new Functor<JsonNode, Map<JsonStringNode, JsonNode>>() {
            public boolean matchesNode(final JsonNode jsonNode) {
                return argo.jdom.JsonNodeType.OBJECT == jsonNode.getType();
            }

            public Map<JsonStringNode, JsonNode> applyTo(final JsonNode jsonNode) {
                return jsonNode.getFields();
            }

            @Override
            public String toString() {
                return "an object";
            }
        }));
    }

    public static JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> aField(final String fieldName) {
        return aField(argo.jdom.JsonNodeFactories.aJsonString(fieldName));
    }

    public static JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> aField(final JsonStringNode fieldName) {
        return new JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode>(new Functor<Map<JsonStringNode, JsonNode>, JsonNode>() {
            public boolean matchesNode(Map<JsonStringNode, JsonNode> jsonNode) {
                return jsonNode.containsKey(fieldName);
            }

            public JsonNode applyTo(Map<JsonStringNode, JsonNode> jsonNode) {
                return jsonNode.get(fieldName);
            }

            @Override
            public String toString() {
                return "a field called [" + fieldName.getText() + "]";
            }
        });
    }

    public static JsonNodeSelector<JsonNode, JsonNode> anObjectNodeWithField(final JsonStringNode fieldName) {
        return anObjectNode().with(aField(fieldName));
    }

    public static JsonNodeSelector<JsonNode, JsonNode> anObjectNodeWithField(final String fieldName) {
        return anObjectNode().with(aField(fieldName));
    }

    public static JsonNodeSelector<List<JsonNode>, JsonNode> anElement(final int index) {
        return new JsonNodeSelector<List<JsonNode>, JsonNode>(new Functor<List<JsonNode>, JsonNode>() {
            public boolean matchesNode(final List<JsonNode> jsonNode) {
                return jsonNode.size() > index;
            }

            public JsonNode applyTo(final List<JsonNode> jsonNode) {
                return jsonNode.get(index);
            }

            @Override
            public String toString() {
                return "an element at index [" + index + "]";
            }
        });
    }

    public static JsonNodeSelector<JsonNode, JsonNode> anArrayNodeWithElement(final int index) {
        return anArrayNode().with(anElement(index));
    }

    private static <T> JsonNodeSelector<JsonNode, T> chainOn(final Object[] pathElements, JsonNodeSelector<JsonNode, T> result) {
        for (int i = pathElements.length - 1; i >= 0; i--) {
            if (pathElements[i] instanceof Integer) {
                result = chainedJsonNodeSelector(anArrayNodeWithElement((Integer) pathElements[i]), result);
            } else if (pathElements[i] instanceof String) {
                result = chainedJsonNodeSelector(anObjectNodeWithField((String)pathElements[i]), result);
            } else {
                throw new IllegalArgumentException("Element [" + pathElements[i] + "] of path elements" +
                        " [" + Arrays.toString(pathElements) + "] was of illegal type [" + pathElements[i].getClass().getCanonicalName()
                        + "]; only Integer and String are valid.");
            }
        }
        return result;
    }

    private static <T, U, V> JsonNodeSelector<T, V> chainedJsonNodeSelector(final JsonNodeSelector<T, U> parent, final JsonNodeSelector<U, V> child) {
        return new JsonNodeSelector<T, V>(new ChainedFunctor<T, U, V>(parent, child));
    }
}
