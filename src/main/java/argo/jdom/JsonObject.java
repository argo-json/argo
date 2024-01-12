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

import static argo.jdom.ImmutableListFactories.immutableListOf;
import static java.util.Collections.unmodifiableMap;

final class JsonObject extends AbstractJsonObject {

    private static final JsonObject EMPTY_OBJECT = new JsonObject(Collections.<JsonField>emptyList());

    private final List<JsonField> fields;
    private transient Map<JsonStringNode, JsonNode> fieldMap;

    static JsonObject jsonObject(final Iterator<JsonField> fields) {
        return jsonObject(immutableListOf(fields));
    }

    static JsonObject jsonObject(final Iterable<JsonField> fields) {
        return jsonObject(immutableListOf(fields));
    }

    static JsonObject jsonObject(final JsonFieldIteratorBuilder fields) {
        return jsonObject(immutableListOf(fields.build(), fields.size()));
    }

    private static JsonObject jsonObject(final List<JsonField> fieldList) {
        return fieldList.isEmpty() ? EMPTY_OBJECT : new JsonObject(fieldList);
    }

    private JsonObject(final List<JsonField> fields) {
        this.fields = fields;
    }

    @Override
    public Map<JsonStringNode, JsonNode> getFields() {
        if (fieldMap == null) {
            final Map<JsonStringNode, JsonNode> modifiableFieldMap = new LinkedHashMap<JsonStringNode, JsonNode>(fields.size() * 4 / 3 + 1);
            for (final JsonField field : fields) {
                modifiableFieldMap.put(field.getName(), field.getValue());
            }
            fieldMap = unmodifiableMap(modifiableFieldMap);
        }
        return fieldMap;
    }

    @Override
    public List<JsonField> getFieldList() {
        return fields;
    }
}
