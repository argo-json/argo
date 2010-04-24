/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.List;
import java.util.Map;

/**
 * <p>An node (leaf or otherwise) in a JSON document.</p>
 *
 * <p>Supplies methods for examining the node, and also navigating the hierarchy at and below this node.
 * Methods for navigating the hierarchy are of the form <code>aXXXValue(Object... pathElements)</code>.
 * <p>For example,
 * <code>aStringValue(Object... pathElements)</code> takes a series of <code>String</code>s and
 * <code>Integer</code>s as its argument which tell it how to navigate down a hierarchy to a particular JSON string.
 * The <code>String</code>s tell it to select a field with the given name from an object, and the <code>Integer</code>s
 * tell it to select an element with the given index from an array.</p> If no field of that name exists, or the field
 * exists, but it isn't a JSON string, an <code>IllegalArgumentException</code> is thrown.</p> 
 */
public abstract class JsonNode {

    // Only extendable by classes in this package
    JsonNode() {
    }

    public abstract JsonNodeType getType();

    public abstract boolean hasText();

    /**
     * @return the text associated with this node
     * @throws IllegalStateException if hasText() returns false, indicating this type of node doesn't have text.
     */
    public abstract String getText();

    public abstract boolean hasFields();

    /**
     * @return the fields associated with this node
     * @throws IllegalStateException if hasFields() returns false, indicating this type of node doesn't support fields.
     */
    public abstract Map<JsonStringNode, JsonNode> getFields();

    public abstract boolean hasElements();

    /**
     * @return the elements associated with this node
     * @throws IllegalStateException if hasElements() returns false, indicating this type of node doesn't support elements.
     */
    public abstract List<JsonNode> getElements();

    public final boolean isBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aBooleanNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>Boolean</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the <code>Boolean</code> at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON boolean.
     */
    public final Boolean aBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aBooleanNode(pathElements).getValue(this);
    }

    public final boolean isNullableBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableBooleanNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>Boolean</code> or <code>null</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the <code>Boolean</code> at the path given, or null, if there is a JSON null at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON boolean or a JSON null.
     */
    public final Boolean aNullableBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableBooleanNode(pathElements).getValue(this);
    }

    public final boolean isStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aStringNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>String</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the <code>String</code> at the path given
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON string.
     */
    public final String aStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aStringNode(pathElements).getValue(this);
    }

    public final boolean isNullableStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableStringNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>String</code> or <code>null</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the <code>String</code> at the path given, or null, if there is a JSON null at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON string or a JSON null.
     */
    public final String aNullableStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableStringNode(pathElements).getValue(this);
    }

    public final boolean isNumberValue(final Object... pathElements) {
        return JsonNodeSelectors.aNumberNode(pathElements).matches(this);
    }

    /**
     * Gets a numeric <code>String</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the numeric <code>String</code> at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON number.
     */
    public final String aNumberValue(final Object... pathElements) {
        return JsonNodeSelectors.aNumberNode(pathElements).getValue(this);
    }

    public final boolean isNullableNumberNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullableNumberNode(pathElements).matches(this);
    }

    /**
     * Gets a numeric <code>String</code> or <code>null</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return the numeric <code>String</code> at the path given, or null, if there is a JSON null at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON number or a JSON null.
     */
    public final String aNullableNumberValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableNumberNode(pathElements).getValue(this);
    }

    public final boolean isNullNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>JsonNode</code> representing null by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return a <code>JsonNode</code> representing null.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON null.
     */
    public final JsonNode aNullNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullNode(pathElements).getValue(this);
    }

    public final boolean isObjectNode(final Object... pathElements) {
        return JsonNodeSelectors.anObjectNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>Map</code> of <code>String</code> field names to <code>JsonNode</code>s, representing a JSON object, by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return a <code>Map</code> of <code>String</code> field names to <code>JsonNode</code>s representing a JSON object.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON object.
     */
    public final Map<JsonStringNode, JsonNode> anObjectNode(final Object... pathElements) {
        return JsonNodeSelectors.anObjectNode(pathElements).getValue(this);
    }

    public final boolean isNullableObjectNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullableObjectNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>Map</code> of <code>String</code> field names to <code>JsonNode</code>s, representing a JSON object, or <code>null</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return a <code>Map</code> of <code>String</code> field names to <code>JsonNode</code>s representing a JSON object at the path given, or null, if there is a JSON null at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON object or a JSON null.
     */
    public final Map<JsonStringNode, JsonNode> aNullableObjectNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullableObjectNode(pathElements).getValue(this);
    }

    public final boolean isArrayNode(final Object... pathElements) {
        return JsonNodeSelectors.anArrayNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>List</code> of <code>JsonNode</code>s, representing a JSON array, by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return a <code>List</code> of <code>JsonNode</code>s representing a JSON array.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON array.
     */
    public final List<JsonNode> anArrayNode(final Object... pathElements) {
        return JsonNodeSelectors.anArrayNode(pathElements).getValue(this);
    }

    public final boolean isNullableArrayNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullableArrayNode(pathElements).matches(this);
    }

    /**
     * Gets a <code>List</code> of <code>JsonNode</code>s, representing a JSON array, or <code>null</code> by navigating the hierarchy below this node.
     *
     * @param pathElements a series of <code>String</code>s, representing the names of fields on objects, and <code>Integer</code>s, representing elements of arrays indicating how to navigate from this node.
     * @return a <code>List</code> of <code>JsonNode</code>s representing a JSON array, or null, if there is a JSON null at the path given.
     * @throws IllegalArgumentException if there is no node at the given path, or the node at the given path is not a JSON array or a JSON null.
     */
    public final List<JsonNode> aNullableArrayNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullableArrayNode(pathElements).getValue(this);
    }
}