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

import java.util.*;

import static argo.jdom.ArrayFactories.nullFreeArrayOf;
import static java.util.Collections.unmodifiableMap;

final class JsonObject extends JsonNode {

    private static final JsonField[] EMPTY_FIELDS_ARRAY = new JsonField[0];
    private static final JsonObject EMPTY_OBJECT = new JsonObject(EMPTY_FIELDS_ARRAY);

    private final JsonField[] fields;

    private int cachedHashCode;

    private transient Map<JsonStringNode, JsonNode> fieldMap;

    private JsonObject(final JsonField[] fields) {
        this.fields = fields;
    }

    static JsonObject jsonObject(final Iterator<JsonField> fields) {
        return jsonObject(nullFreeArrayOf(fields, EMPTY_FIELDS_ARRAY));
    }

    static JsonObject jsonObject(final Iterable<JsonField> fields) {
        return jsonObject(nullFreeArrayOf(fields, EMPTY_FIELDS_ARRAY));
    }

    private static JsonObject jsonObject(final JsonField[] fields) {
        return fields.length == 0 ? EMPTY_OBJECT : new JsonObject(fields);
    }

    @Override
    public JsonNodeType getType() {
        return JsonNodeType.OBJECT;
    }

    @Override
    public boolean hasText() {
        return false;
    }

    @Override
    public String getText() {
        throw new UnsupportedOperationException("Objects do not have text");
    }

    @Override
    public boolean hasFields() {
        return true;
    }

    @Override
    public Map<JsonStringNode, JsonNode> getFields() {
        if (fieldMap == null) {
            final Map<JsonStringNode, JsonNode> modifiableFieldMap = new LinkedHashMap<JsonStringNode, JsonNode>((fields.length * 4 + 2) / 3);
            for (final JsonField field : fields) {
                modifiableFieldMap.put(field.getName(), field.getValue());
            }
            fieldMap = unmodifiableMap(modifiableFieldMap);
        }
        return fieldMap;
    }

    @Override
    public List<JsonField> getFieldList() {
        return new UnmodifiableListArrayView<JsonField>(fields);
    }

    @Override
    public boolean hasElements() {
        return false;
    }

    @Override
    public List<JsonNode> getElements() {
        throw new UnsupportedOperationException("Objects do not have elements");
    }

    @Override
    public void visit(final JsonNodeVisitor jsonNodeVisitor) {
        jsonNodeVisitor.object(getFieldList());
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        final JsonObject thatJsonObject = (JsonObject) that;
        return Arrays.equals(this.fields, thatJsonObject.fields);
    }

    @Override
    public int hashCode() {
        int hashCode = cachedHashCode;
        if (hashCode == 0 && fields.length != 0) {
            hashCode = Arrays.hashCode(fields);
            cachedHashCode = hashCode;
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "JsonObject{fields=" + getFieldList() + "}";
    }
}
