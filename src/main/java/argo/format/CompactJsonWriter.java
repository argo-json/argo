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

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;

import java.io.IOException;
import java.io.Writer;

import static argo.jdom.JsonNodeFactories.string;

/**
 * JsonWriter that writes JSON as compactly as possible.  Instances of this class can safely be shared between threads.
 */
public final class CompactJsonWriter extends AbstractJsonWriter {

    @Override
    void write(final Writer writer, final WriteableJsonArray writeableJsonArray, final int depth) throws IOException {
        writer.write('[');
        writeableJsonArray.writeTo(new ArrayWriter() {
            private boolean isFirst = true;

            public void writeElement(final WriteableJsonObject element) throws IOException {
                writeCommaIfRequired();
                write(writer, element);
            }

            public void writeElement(final WriteableJsonArray element) throws IOException {
                writeCommaIfRequired();
                write(writer, element);
            }

            public void writeElement(final WriteableJsonString element) throws IOException {
                writeCommaIfRequired();
                write(writer, element);
            }

            public void writeElement(final WriteableJsonNumber element) throws IOException {
                writeCommaIfRequired();
                write(writer, element);
            }

            public void writeElement(final JsonNode element) throws IOException {
                writeCommaIfRequired();
                write(writer, element);
            }

            private void writeCommaIfRequired() throws IOException {
                if (!isFirst) {
                    writer.write(',');
                }
                isFirst = false;
            }
        });
        writer.write(']');
    }

    @Override
    void write(final Writer writer, final WriteableJsonObject writeableJsonObject, final int depth) throws IOException {
        writer.write('{');
        writeableJsonObject.writeTo(new ObjectWriter() {
            private boolean isFirst = true;

            public void writeField(final String name, final WriteableJsonObject value) throws IOException {
                writeField(string(name), value);
            }

            public void writeField(final String name, final WriteableJsonArray value) throws IOException {
                writeField(string(name), value);
            }

            public void writeField(final String name, final WriteableJsonString value) throws IOException {
                writeField(string(name), value);
            }

            public void writeField(final String name, final WriteableJsonNumber value) throws IOException {
                writeField(string(name), value);
            }

            public void writeField(final String name, final JsonNode value) throws IOException {
                writeField(string(name), value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonObject value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonArray value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonString value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonNumber value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final JsonStringNode name, final JsonNode value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            private void writeName(final JsonStringNode name) throws IOException {
                writeCommaIfRequired();
                write(writer, name);
                writer.write(':');
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonObject value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonArray value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonString value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonNumber value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            public void writeField(final WriteableJsonString name, final JsonNode value) throws IOException {
                writeName(name);
                write(writer, value);
            }

            private void writeName(final WriteableJsonString name) throws IOException {
                writeCommaIfRequired();
                write(writer, name);
                writer.write(':');
            }

            public void writeField(final JsonField jsonField) throws IOException {
                writeField(jsonField.getName(), jsonField.getValue());
            }

            private void writeCommaIfRequired() throws IOException {
                if (!isFirst) {
                    writer.write(',');
                }
                isFirst = false;
            }
        });
        writer.write('}');
    }

    @Override
    void writeObject(final Writer writer, final JsonNode jsonNode, final int depth) throws IOException {
        boolean first = true;
        writer.write('{');
        for (final JsonField field : jsonNode.getFieldList()) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            writeEscapedString(field.getNameText(), writer);
            writer.write(':');
            write(writer, field.getValue());
        }
        writer.write('}');
    }

    @Override
    void writeArray(final Writer writer, final JsonNode jsonNode, final int depth) throws IOException {
        boolean first = true;
        writer.write('[');
        for (final JsonNode node : jsonNode.getElements()) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            write(writer, node);
        }
        writer.write(']');
    }

}
