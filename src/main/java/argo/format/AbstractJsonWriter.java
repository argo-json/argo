/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Writer;

import static argo.format.JsonEscapedString.escapeStringTo;

abstract class AbstractJsonWriter implements JsonWriter {

    public final void write(final Writer writer, final WriteableJsonArray writeableJsonArray) throws IOException {
        write(writer, writeableJsonArray, 0);
    }

    abstract void write(Writer writer, WriteableJsonArray writeableJsonArray, int depth) throws IOException;

    public final void write(final Writer writer, final WriteableJsonObject writeableJsonObject) throws IOException {
        write(writer, writeableJsonObject, 0);
    }

    public final void write(final Writer writer, final WriteableJsonString writeableJsonString) throws IOException {
        writer.write('"');
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(writer);
        try {
            writeableJsonString.writeTo(jsonStringEscapingWriter);
        } finally {
            jsonStringEscapingWriter.close();
        }
        writer.write('"');
    }

    public final void write(final Writer writer, final WriteableJsonNumber writeableJsonNumber) throws IOException {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(writer);
        try {
            writeableJsonNumber.writeTo(jsonNumberValidatingWriter);
        } finally {
            jsonNumberValidatingWriter.close();
        }
        if (!jsonNumberValidatingWriter.isEndState()) {
            throw new IllegalArgumentException("Attempt to write an incomplete JSON number");
        }
    }

    public final void write(final Writer writer, final JsonNode jsonNode) throws IOException {
        write(writer, jsonNode, 0);
    }

    abstract void write(Writer writer, WriteableJsonObject writeableJsonObject, int depth) throws IOException;

    protected final void write(final Writer writer, final JsonNode jsonNode, final int depth) throws IOException {
        switch (jsonNode.getType()) { // TODO can this be replaced with polymorphism?
            case ARRAY:
                writeArray(writer, jsonNode, depth);
                break;
            case OBJECT:
                writeObject(writer, jsonNode, depth);
                break;
            case STRING:
                final String text = jsonNode.getText();
                writer.write('"');
                escapeStringTo(writer, text);
                writer.write('"');
                break;
            case NUMBER:
                writer.write(jsonNode.getText());
                break;
            case FALSE:
                writer.write("false");
                break;
            case TRUE:
                writer.write("true");
                break;
            case NULL:
                writer.write("null");
                break;
            default:
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + jsonNode.getType() + "]");
        }
    }

    abstract void writeObject(Writer writer, JsonNode jsonNode, int depth) throws IOException;

    abstract void writeArray(Writer writer, JsonNode jsonNode, int depth) throws IOException;
}
