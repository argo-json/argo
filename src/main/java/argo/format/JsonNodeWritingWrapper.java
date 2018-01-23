/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

abstract class JsonNodeWritingWrapper {

    static final JsonNodeWritingWrapper STRAIGHT_THROUGH_JSON_NODE_WRITING_WRAPPER = new StraightThroughJsonNodeWritingWrapper();
    static final JsonNodeWritingWrapper FIELD_ORDER_NORMALISING_JSON_NODE_WRITING_WRAPPER = new FieldOrderNormalisingJsonNodeWritingWrapper();

    abstract void write(Writer writer, JsonNode jsonNode, JsonWriter jsonWriter) throws IOException;

    private JsonNodeWritingWrapper() {
    }

    private static final class StraightThroughJsonNodeWritingWrapper extends JsonNodeWritingWrapper {
        void write(final Writer writer, final JsonNode jsonNode, final JsonWriter jsonWriter) throws IOException {
            jsonWriter.write(writer, jsonNode);
        }
    }

    private static final class FieldOrderNormalisingJsonNodeWritingWrapper extends JsonNodeWritingWrapper {
        private static final Comparator<JsonField> JSON_FIELD_COMPARATOR = new Comparator<JsonField>() {
            public int compare(final JsonField jsonField, final JsonField jsonField1) {
                return jsonField.getNameText().compareTo(jsonField1.getNameText());
            }
        };


        void write(final Writer writer, final JsonNode jsonNode, final JsonWriter jsonWriter) throws IOException {
            switch (jsonNode.getType()) {
                case ARRAY:
                    jsonWriter.write(writer, new FieldSortingWriteableJsonArray(jsonNode));
                    break;
                case OBJECT:
                    jsonWriter.write(writer, new FieldSortingWriteableJsonObject(jsonNode));
                    break;
                default:
                    jsonWriter.write(writer, jsonNode);
            }
        }

        private static final class FieldSortingWriteableJsonObject implements WriteableJsonObject {
            private final JsonNode jsonNode;

            FieldSortingWriteableJsonObject(final JsonNode jsonNode) {
                this.jsonNode = jsonNode;
            }

            public void writeTo(final ObjectWriter objectWriter) throws IOException {
                final List<JsonField> sorted = new ArrayList<JsonField>(jsonNode.getFieldList());
                Collections.sort(sorted, JSON_FIELD_COMPARATOR);
                for (final JsonField jsonField : sorted) {
                    switch (jsonField.getValue().getType()) {
                        case ARRAY:
                            objectWriter.writeField(jsonField.getName(), new FieldSortingWriteableJsonArray(jsonField.getValue()));
                            break;
                        case OBJECT:
                            objectWriter.writeField(jsonField.getName(), new FieldSortingWriteableJsonObject(jsonField.getValue()));
                            break;
                        default:
                            objectWriter.writeField(jsonField);
                    }
                }
            }
        }

        private static final class FieldSortingWriteableJsonArray implements WriteableJsonArray {
            private final JsonNode jsonNode;

            FieldSortingWriteableJsonArray(final JsonNode jsonNode) {
                this.jsonNode = jsonNode;
            }

            public void writeTo(final ArrayWriter arrayWriter) throws IOException {
                for (final JsonNode element : jsonNode.getElements()) {
                    switch (element.getType()) {
                        case ARRAY:
                            arrayWriter.writeElement(new FieldSortingWriteableJsonArray(element));
                            break;
                        case OBJECT:
                            arrayWriter.writeElement(new FieldSortingWriteableJsonObject(element));
                            break;
                        default:
                            arrayWriter.writeElement(element);
                    }
                }
            }
        }
    }

}
