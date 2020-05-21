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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static argo.jdom.JsonNodeType.NUMBER;

final class JsonNumberNode extends JsonNode implements JsonNodeBuilder<JsonNode> {

    private static final JsonNumberNode ZERO = new JsonNumberNode("0");
    private static final JsonNumberNode ONE = new JsonNumberNode("1");

    private final String value;

    private JsonNumberNode(final String value) {
        if (value == null) {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        final Reader reader = new StringReader(value);
        final JsonNumberValidator jsonNumberValidator = new JsonNumberValidator();
        try {
            int nextCharacter = reader.read();
            while (nextCharacter != -1) {
                jsonNumberValidator.appendCharacter(nextCharacter);
                nextCharacter = reader.read();
            }
            if (!jsonNumberValidator.isEndState()) {
                throw new JsonNumberValidator.ParsingFailedException();
            }
        } catch (IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter threw an IOException", e);
        } catch (JsonNumberValidator.ParsingFailedException e) {
            throw new IllegalArgumentException("Attempt to construct a JsonNumber with a String [" + value + "] that does not match the JSON number specification.");
        }
        this.value = value;
    }

    static JsonNumberNode jsonNumberNode(final String value) {
        if ("0".equals(value)) {
            return ZERO;
        } else if ("1".equals(value)) {
            return ONE;
        } else {
            return new JsonNumberNode(value);
        }
    }

    public JsonNodeType getType() {
        return NUMBER;
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
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    @Override
    public List<JsonField> getFieldList() {
        throw new IllegalStateException("Attempt to get fields on a JsonNode without fields.");
    }

    public boolean hasElements() {
        return false;
    }

    public List<JsonNode> getElements() {
        throw new IllegalStateException("Attempt to get elements on a JsonNode without elements.");
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonNumberNode thatJsonNumberNode = (JsonNumberNode) that;
        return this.value.equals(thatJsonNumberNode.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "JsonNumberNode{value='" + value + "'}";
    }

    public JsonNode build() {
        return this;
    }
}
