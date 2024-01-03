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

import java.util.List;
import java.util.Map;

/**
 * {@code JsonNode} that explicitly represents a JSON String.
 */
public final class JsonStringNode extends JsonNode implements Comparable<JsonStringNode>, JsonNodeBuilder<JsonStringNode> {

    private static final JsonStringNode EMPTY = new JsonStringNode("");

    private final String value;

    private JsonStringNode(final String value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        this.value = value;
    }

    static JsonStringNode jsonStringNode(final String value) {
        return "".equals(value) ? EMPTY : new JsonStringNode(value);
    }

    public JsonNodeType getType() {
        return JsonNodeType.STRING;
    }

    public boolean hasText() {
        return true;
    }

    public String getText() {
        return value;
    }

    public boolean hasFields() {
        return false;
    }

    public Map<JsonStringNode, JsonNode> getFields() {
        throw new UnsupportedOperationException("Strings do not have fields");
    }

    @Override
    public List<JsonField> getFieldList() {
        throw new UnsupportedOperationException("Strings do not have fields");
    }

    public boolean hasElements() {
        return false;
    }

    public List<JsonNode> getElements() {
        throw new UnsupportedOperationException("Strings do not have elements");
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        final JsonStringNode thatJsonTextNode = (JsonStringNode) that;
        return this.value.equals(thatJsonTextNode.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "JsonStringNode{value='" + value + "'}";
    }

    public int compareTo(final JsonStringNode that) {
        return this.value.compareTo(that.value);
    }

    public JsonStringNode build() {
        return this;
    }
}