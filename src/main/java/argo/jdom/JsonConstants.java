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

final class JsonConstants extends JsonNode implements JsonNodeBuilder<JsonNode> {

    static final JsonConstants NULL = new JsonConstants(JsonNodeType.NULL);
    static final JsonConstants TRUE = new JsonConstants(JsonNodeType.TRUE);
    static final JsonConstants FALSE = new JsonConstants(JsonNodeType.FALSE);

    private final JsonNodeType jsonNodeType;

    private JsonConstants(final JsonNodeType jsonNodeType) {
        this.jsonNodeType = jsonNodeType;
    }

    @Override
    public JsonNodeType getType() {
        return jsonNodeType;
    }

    @Override
    public boolean hasText() {
        return false;
    }

    @Override
    public String getText() {
        throw new UnsupportedOperationException("Does not have text");
    }

    @Override
    public boolean hasFields() {
        return false;
    }

    @Override
    public Map<JsonStringNode, JsonNode> getFields() {
        throw new UnsupportedOperationException("Does not have fields");
    }

    @Override
    public List<JsonField> getFieldList() {
        throw new UnsupportedOperationException("Does not have fields");
    }

    @Override
    public boolean hasElements() {
        return false;
    }

    @Override
    public List<JsonNode> getElements() {
        throw new UnsupportedOperationException("Does not have elements");
    }

    public JsonNode build() {
        return this;
    }

    @Override
    public String toString() {
        return "JsonNode{jsonNodeType=" + jsonNodeType + '}';
    }
}
