/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factories for <code>JsonNode</code>s.
 */
public final class JsonNodeFactories {

    private JsonNodeFactories() {
    }

    /**
     * @return a JSON null
     * @deprecated Use {@link #nullNode()} instead
     */
    @Deprecated
    public static JsonNode aJsonNull() {
        return nullNode();
    }

    public static JsonNode nullNode() {
        return JsonConstants.NULL;
    }

    /**
     * @return a JSON true
     * @deprecated Use {@link #trueNode()} instead
     */
    @Deprecated
    public static JsonNode aJsonTrue() {
        return trueNode();
    }

    public static JsonNode trueNode() {
        return JsonConstants.TRUE;
    }

    /**
     * @return a JSON false
     * @deprecated Use {@link #falseNode()} instead
     */
    @Deprecated
    public static JsonNode aJsonFalse() {
        return falseNode();
    }

    public static JsonNode falseNode() {
        return JsonConstants.FALSE;
    }

    /**
     * @param value the Java String to represent as a JSON string
     * @return a JSON string representation of the given String
     * @deprecated Use {@link #string(String)} instead
     */
    @Deprecated
    public static JsonStringNode aJsonString(final String value) {
        return string(value);
    }

    public static JsonStringNode string(final String value) {
        return new JsonStringNode(value);
    }

    /**
     * @param value a Java String to represent as a JSON number
     * @return a JSON number representation of the given String
     * @deprecated Use {@link #number(String)} instead
     */
    @Deprecated
    public static JsonNode aJsonNumber(final String value) {
        return number(value);
    }

    public static JsonNode number(final String value) {
        return new JsonNumberNode(value);
    }

    /**
     * @param value a Java BigDecimal to represent as a JSON number
     * @return a JSON number representation of the given BigDecimal
     * @deprecated Use {@link #number(java.math.BigDecimal)} instead
     */
    @Deprecated
    public static JsonNode aJsonNumber(final BigDecimal value) {
        return number(value);
    }

    public static JsonNode number(final BigDecimal value) {
        return new JsonNumberNode(value.toString());
    }

    /**
     * @param value a Java BigInteger to represent as a JSON number
     * @return a JSON number representation of the given BigInteger
     * @deprecated Use {@link #number(java.math.BigInteger)} instead
     */
    @Deprecated
    public static JsonNode aJsonNumber(final BigInteger value) {
        return number(value);
    }

    public static JsonNode number(final BigInteger value) {
        return new JsonNumberNode(value.toString());
    }

    public static JsonNode number(final long value) {
        return new JsonNumberNode(Long.toString(value));
    }

    /**
     * @param elements <code>JsonNode</code>s that will populate the array
     * @return a JSON array of the given <code>JsonNode</code>s
     * @deprecated Use {@link #array(Iterable)} instead
     */
    @Deprecated
    public static JsonRootNode aJsonArray(final Iterable<JsonNode> elements) {
        return array(elements);
    }

    public static JsonRootNode array(final Iterable<JsonNode> elements) {
        return new JsonArray(elements);
    }

    /**
     * @param elements <code>JsonNode</code>s that will populate the array
     * @return a JSON array of the given <code>JsonNode</code>s
     * @deprecated Use {@link #array(JsonNode...)} instead
     */
    @Deprecated
    public static JsonRootNode aJsonArray(final JsonNode... elements) {
        return array(elements);
    }

    public static JsonRootNode array(final JsonNode... elements) {
        return array(Arrays.asList(elements));
    }

    /**
     * @param fields <code>JsonField</code>s that the object will contain
     * @return a JSON object containing the given fields
     * @deprecated Use {@link #object(java.util.Map)} instead
     */
    @Deprecated
    public static JsonRootNode aJsonObject(final Map<JsonStringNode, JsonNode> fields) {
        return object(fields);
    }

    public static JsonRootNode object(final Map<JsonStringNode, JsonNode> fields) {
        return new JsonObject(fields);
    }

    /**
     * @param fields <code>JsonField</code>s that the object will contain
     * @return a JSON object containing the given fields
     * @deprecated Use {@link #object(JsonField...)} instead
     */
    @Deprecated
    public static JsonRootNode aJsonObject(final JsonField... fields) {
        return object(fields);
    }

    public static JsonRootNode object(final JsonField... fields) {
        return object(Arrays.asList(fields));
    }

    /**
     * @param fields <code>JsonField</code>s that the object will contain
     * @return a JSON object containing the given fields
     * @deprecated Use {@link #object(Iterable)} instead
     */
    @Deprecated
    public static JsonRootNode aJsonObject(final Iterable<JsonField> fields) {
        return object(fields);
    }

    public static JsonRootNode object(final Iterable<JsonField> fields) {
        return object(new HashMap<JsonStringNode, JsonNode>() {{
            for (final JsonField field : fields) {
                put(field.getName(), field.getValue());
            }
        }});
    }

    /**
     * @param name the name of the field
     * @param value the value of the field
     * @return a JSON field with the given name and value
     * @deprecated Use {@link #field(String, JsonNode)} instead
     */
    @Deprecated
    public static JsonField aJsonField(final String name, final JsonNode value) {
        return field(name, value);
    }

    public static JsonField field(final String name, final JsonNode value) {
        return new JsonField(string(name), value);
    }

    /**
     * @param name the name of the field
     * @param value the value of the field
     * @return a JSON field with the given name and value
     * @deprecated Use {@link #field(JsonStringNode, JsonNode)} instead
     */
    @Deprecated
    public static JsonField aJsonField(final JsonStringNode name, final JsonNode value) {
        return field(name, value);
    }

    public static JsonField field(final JsonStringNode name, final JsonNode value) {
        return new JsonField(name, value);
    }

    /**
     * @param value the Java boolean to represent as a JSON Boolean
     * @return a JSON Boolean representation of the given boolean
     * @deprecated Use {@link #booleanNode(boolean)} instead
     */
    @Deprecated
    public static JsonNode aJsonBoolean(final boolean value) {
        return booleanNode(value);
    }

    public static JsonNode booleanNode(final boolean value) {
        return value ? trueNode() : falseNode();
    }
}
