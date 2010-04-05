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
 * An node (leaf or otherwise) in a JSON document.
 *
 * Supplies methods for accessing the contents of this node, and its subnodes, of the form
 * <code>aXXXValue(Object... pathElements)<code>, e.g. <code>aStringValue("bob")</code> will return the JSON String
 * in a field called "bob", if such a node exists at the root of the <code>JsonNode</code> on which the method is
 * called.  If no field of that name exists, or the field exists, but it isn't a JSON String, an
 * <code>IllegalArgumentException</code> is thrown. 
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

    public String aStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aStringNode(pathElements).getValue(this);
    }

    public String aNullableStringValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableStringNode(pathElements).getValue(this);
    }

    public Boolean aBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aBooleanNode(pathElements).getValue(this);
    }

    public Boolean aNullableBooleanValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableBooleanNode(pathElements).getValue(this);
    }

    public String aNumberValue(final Object... pathElements) {
        return JsonNodeSelectors.aNumberNode(pathElements).getValue(this);
    }

    public String aNullableNumberValue(final Object... pathElements) {
        return JsonNodeSelectors.aNullableNumberNode(pathElements).getValue(this);
    }

    public JsonNode aNullNode(final Object... pathElements) {
        return JsonNodeSelectors.aNullNode(pathElements).getValue(this);
    }

    public Map<JsonStringNode, JsonNode> anObjectNode(final Object... pathElements) {
        return JsonNodeSelectors.anObjectNode(pathElements).getValue(this);
    }

    public List<JsonNode> anArrayNode(final Object... pathElements) {
        return JsonNodeSelectors.anArrayNode(pathElements).getValue(this);
    }
}