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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static argo.jdom.ImmutableListFactories.immutableListOf;

final class JsonArray extends JsonNode {

    private static final JsonArray EMPTY_ARRAY = new JsonArray(Collections.<JsonNode>emptyList());

    private final List<JsonNode> elements;

    private JsonArray(final List<JsonNode> elements) {
        this.elements = elements;
    }

    static JsonArray jsonArray(final Iterator<? extends JsonNode> elements) {
        return jsonArray(immutableListOf(elements));
    }

    static JsonArray jsonArray(final Iterable<? extends JsonNode> elements) {
        return jsonArray(immutableListOf(elements));
    }

    private static JsonArray jsonArray(final List<JsonNode> elementList) {
        return elementList.isEmpty() ? EMPTY_ARRAY : new JsonArray(elementList);
    }

    @Override
    public JsonNodeType getType() {
        return JsonNodeType.ARRAY;
    }

    @Override
    public boolean hasText() {
        return false;
    }

    @Override
    public String getText() {
        throw new UnsupportedOperationException("Arrays do not have text");
    }

    @Override
    public boolean hasFields() {
        return false;
    }

    @Override
    public Map<JsonStringNode, JsonNode> getFields() {
        throw new UnsupportedOperationException("Arrays do not have fields");
    }

    @Override
    public List<JsonField> getFieldList() {
        throw new UnsupportedOperationException("Arrays do not have fields");
    }

    @Override
    public boolean hasElements() {
        return true;
    }

    @Override
    public List<JsonNode> getElements() {
        return elements;
    }

    @Override
    public void visit(final JsonNodeVisitor jsonNodeVisitor) {
        jsonNodeVisitor.array(elements);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        final JsonArray thatJsonArray = (JsonArray) that;
        return elements.equals(thatJsonArray.elements);
    }

    @Override
    public int hashCode() {
        return getElements().hashCode();
    }

    @Override
    public String toString() {
        return "JsonArray{elements=" + getElements() + "}";
    }
}
