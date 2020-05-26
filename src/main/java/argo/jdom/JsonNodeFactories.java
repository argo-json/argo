/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Factories for {@code JsonNode}s.
 */
public final class JsonNodeFactories {

    private JsonNodeFactories() {
    }

    /**
     * @return a JSON null
     */
    public static JsonNode nullNode() {
        return JsonConstants.NULL;
    }

    /**
     * @return a JSON true
     */
    public static JsonNode trueNode() {
        return JsonConstants.TRUE;
    }

    /**
     * @return a JSON false
     */
    public static JsonNode falseNode() {
        return JsonConstants.FALSE;
    }

    /**
     * @param value the Java String to represent as a JSON string
     * @return a JSON string representation of the given String
     */
    public static JsonStringNode string(final String value) {
        return JsonStringNode.jsonStringNode(value);
    }

    /**
     * @param value the Java String to represent as a JSON string
     * @return a JSON string representation of the given String
     */
    public static JsonNode nullableString(final String value) {
        return value == null ? nullNode() : JsonStringNode.jsonStringNode(value);
    }

    /**
     * @param value a Java String to represent as a JSON number
     * @return a JSON number representation of the given String
     */
    public static JsonNode number(final String value) {
        return JsonNumberNode.jsonNumberNode(value);
    }

    /**
     * @param value a Java BigDecimal to represent as a JSON number
     * @return a JSON number representation of the given BigDecimal
     */
    public static JsonNode number(final BigDecimal value) {
        return JsonNumberNode.jsonNumberNode(value.toString());
    }

    /**
     * @param value a Java BigInteger to represent as a JSON number
     * @return a JSON number representation of the given BigInteger
     */
    public static JsonNode number(final BigInteger value) {
        return JsonNumberNode.jsonNumberNode(value.toString());
    }

    /**
     * @param value a Java long to represent as a JSON number
     * @return a JSON number representation of the given BigInteger
     */
    public static JsonNode number(final long value) {
        return JsonNumberNode.jsonNumberNode(Long.toString(value));
    }

    /**
     * @param value a Java String to represent as a JSON number
     * @return a JSON number representation of the given String or a JSON null if the Java String is null
     */
    public static JsonNode nullableNumber(final String value) {
        return value == null ? nullNode() : JsonNumberNode.jsonNumberNode(value);
    }

    /**
     * @param value a Java BigDecimal to represent as a JSON number
     * @return a JSON number representation of the given BigDecimal or a JSON null if the Java BigDecimal is null
     */
    public static JsonNode nullableNumber(final BigDecimal value) {
        return value == null ? nullNode() : JsonNumberNode.jsonNumberNode(value.toString());
    }

    /**
     * @param value a Java BigInteger to represent as a JSON number
     * @return a JSON number representation of the given BigInteger or a JSON null if the Java BigInteger is null
     */
    public static JsonNode nullableNumber(final BigInteger value) {
        return value == null ? nullNode() : JsonNumberNode.jsonNumberNode(value.toString());
    }

    /**
     * @param value a Java Long to represent as a JSON number
     * @return a JSON number representation of the given BigInteger or a JSON null if the Java Long is null
     */
    public static JsonNode nullableNumber(final Long value) {
        return value == null ? nullNode() : JsonNumberNode.jsonNumberNode(Long.toString(value));
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s
     */
    public static JsonNode array(final Iterator<? extends JsonNode> elements) {
        return JsonArray.jsonArray(elements);
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s
     */
    public static JsonNode array(final Iterable<? extends JsonNode> elements) {
        return JsonArray.jsonArray(elements);
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s
     */
    public static JsonNode array(final JsonNode... elements) {
        return array(asList(elements));
    }

    /**
     * Generates an array where the members are only evaluated on request.  This means that arrays generated
     * by this method might not be immutable, depending on the implementation of {@code List} used in the
     * argument.  If you supply a list of elements, and then add an item to the list, that item will also be
     * part of the array.
     *
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s
     */
    public static JsonNode lazyArray(final List<? extends JsonNode> elements) {
        return new AbstractJsonArray() {
            @Override
            public List<JsonNode> getElements() {
                return new AbstractList<JsonNode>() {
                    @Override
                    public JsonNode get(final int i) {
                        return elements.get(i);
                    }

                    @Override
                    public int size() {
                        return elements.size();
                    }
                };
            }
        };
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s or a JSON null if the Iterator is null
     */
    public static JsonNode nullableArray(final Iterator<? extends JsonNode> elements) {
        return elements == null ? nullNode() : array(elements);
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s or a JSON null if the Iterable is null
     */
    public static JsonNode nullableArray(final Iterable<? extends JsonNode> elements) {
        return elements == null ? nullNode() : array(elements);
    }

    /**
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s or a JSON null if the Java array is null
     */
    public static JsonNode nullableArray(final JsonNode... elements) {
        return elements == null ? nullNode() : array(elements);
    }

    /**
     * Generates an array where the members are only evaluated on request.  This means that arrays generated
     * by this method might not be immutable, depending on the implementation of {@code List} used in the
     * argument.  If you supply a list of elements, and then add an item to the list, that item will also be
     * part of the array.
     *
     * @param elements {@code JsonNode}s that will populate the array
     * @return a JSON array of the given {@code JsonNode}s or a JSON null if the List is null
     */
    public static JsonNode nullableLazyArray(final List<? extends JsonNode> elements) {
        return elements == null ? nullNode() : lazyArray(elements);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields
     */
    public static JsonNode object(final Map<JsonStringNode, ? extends JsonNode> fields) {
        final List<JsonField> result = new ArrayList<JsonField>(fields.size());
        for (final Map.Entry<JsonStringNode, ? extends JsonNode> entry : fields.entrySet()) {
            result.add(field(entry.getKey(), entry.getValue()));
        }
        return JsonObject.jsonObject(result);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields
     */
    public static JsonNode object(final JsonField... fields) {
        return object(asList(fields));
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields
     */
    public static JsonNode object(final Iterator<JsonField> fields) {
        return JsonObject.jsonObject(fields);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields
     */
    public static JsonNode object(final Iterable<JsonField> fields) {
        return JsonObject.jsonObject(fields);
    }

    /**
     * Generates an object where the members are only evaluated on request.  This means that objects generated
     * by this method might not be immutable, depending on the implementation of {@code List} used in the
     * argument.  If you supply a list of fields, and then add an item to the list, that item will also be
     * part of the object.
     *
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields
     */
    public static JsonNode lazyObject(final List<JsonField> fields) {
        return new AbstractJsonObject() {
            @Override
            public Map<JsonStringNode, JsonNode> getFields() {
                final Iterator<JsonField> fieldIterator = fields.iterator();
                return new AbstractMap<JsonStringNode, JsonNode>() {
                    @Override
                    public Set<Entry<JsonStringNode, JsonNode>> entrySet() {
                        return new AbstractSet<Entry<JsonStringNode, JsonNode>>() {
                            @Override
                            public Iterator<Entry<JsonStringNode, JsonNode>> iterator() {
                                return new Iterator<Entry<JsonStringNode, JsonNode>>() {
                                    public boolean hasNext() {
                                        return fieldIterator.hasNext();
                                    }

                                    public Entry<JsonStringNode, JsonNode> next() {
                                        final JsonField jsonField = fieldIterator.next();
                                        return new JsonFieldEntry(jsonField.getName(), jsonField.getValue());
                                    }

                                    public void remove() {
                                        throw new UnsupportedOperationException("Removal not supported");
                                    }
                                };
                            }

                            @Override
                            public int size() {
                                return fields.size();
                            }
                        };
                    }
                };
            }

            @Override
            public List<JsonField> getFieldList() {
                return unmodifiableList(fields);
            }
        };
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields or a JSON null if the Map is null
     */
    public static JsonNode nullableObject(final Map<JsonStringNode, ? extends JsonNode> fields) {
        return fields == null ? nullNode() : object(fields);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields or a JSON null if the array is null
     */
    public static JsonNode nullableObject(final JsonField... fields) {
        return fields == null ? nullNode() : object(fields);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields or a JSON null if the Iterator is null
     */
    public static JsonNode nullableObject(final Iterator<JsonField> fields) {
        return fields == null ? nullNode() : object(fields);
    }

    /**
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields or a JSON null if the Iterable is null
     */
    public static JsonNode nullableObject(final Iterable<JsonField> fields) {
        return fields == null ? nullNode() : object(fields);
    }

    /**
     * Generates an object where the members are only evaluated on request.  This means that objects generated
     * by this method might not be immutable, depending on the implementation of {@code List} used in the
     * argument.  If you supply a list of fields, and then add an item to the list, that item will also be
     * part of the object.
     *
     * @param fields {@code JsonField}s that the object will contain
     * @return a JSON object containing the given fields or a JSON null if the List is null
     */
    public static JsonNode nullableLazyObject(final List<JsonField> fields) {
        return fields == null ? nullNode() : lazyObject(fields);
    }

    private static final class JsonFieldEntry implements Map.Entry<JsonStringNode, JsonNode> {
        private final JsonStringNode key;
        private final JsonNode value;

        private JsonFieldEntry(final JsonStringNode key, final JsonNode value) {
            this.key = key;
            this.value = value;
        }

        public JsonStringNode getKey() {
            return key;
        }

        public JsonNode getValue() {
            return value;
        }

        public JsonNode setValue(final JsonNode value) {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    /**
     * @param name  the name of the field
     * @param value the value of the field
     * @return a JSON field with the given name and value
     */
    public static JsonField field(final String name, final JsonNode value) {
        return new JsonField(name, value);
    }

    /**
     * @param name  the name of the field
     * @param value the value of the field
     * @return a JSON field with the given name and value
     */
    public static JsonField field(final JsonStringNode name, final JsonNode value) {
        return new JsonField(name, value);
    }

    /**
     * @param value the Java boolean to represent as a JSON Boolean
     * @return a JSON Boolean representation of the given boolean
     */
    public static JsonNode booleanNode(final boolean value) {
        return value ? trueNode() : falseNode();
    }

    /**
     * @param value the Java Boolean to represent as a JSON Boolean
     * @return a JSON Boolean representation of the given Boolean or a JSON null if the Java Boolean is null
     */
    public static JsonNode nullableBooleanNode(final Boolean value) {
        return value == null ? nullNode() : booleanNode(value);
    }
}
