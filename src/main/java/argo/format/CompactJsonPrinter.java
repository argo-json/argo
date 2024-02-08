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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;

import java.io.IOException;
import java.io.Writer;

final class CompactJsonPrinter extends AbstractJsonPrinter {

    CompactJsonPrinter(final Writer writer) {
        super(writer);
    }

    @Override
    void throwingObject(final Iterable<JsonField> fields) throws IOException {
        boolean first = true;
        writer.write('{');
        for (final JsonField field : fields) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            write(field.getName());
            writer.write(':');
            write(field.getValue());
        }
        writer.write('}');
    }

    @Override
    void throwingArray(final Iterable<JsonNode> elements) throws IOException {
        boolean first = true;
        writer.write('[');
        for (final JsonNode element : elements) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            write(element);
        }
        writer.write(']');
    }

    @Override
    void write(final WriteableJsonArray writeableJsonArray) throws IOException {
        writer.write('[');
        writeableJsonArray.writeTo(new ArrayWriter() {
            private boolean isFirst = true;

            public void writeElement(final WriteableJsonObject element) throws IOException {
                writeCommaIfRequired();
                write(element);
            }

            public void writeElement(final WriteableJsonArray element) throws IOException {
                writeCommaIfRequired();
                write(element);
            }

            public void writeElement(final WriteableJsonString element) throws IOException {
                writeCommaIfRequired();
                write(element);
            }

            public void writeElement(final WriteableJsonNumber element) throws IOException {
                writeCommaIfRequired();
                write(element);
            }

            public void writeElement(final JsonNode element) throws IOException {
                writeCommaIfRequired();
                write(element);
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
    void write(final WriteableJsonObject writeableJsonObject) throws IOException {
        writer.write('{');
        writeableJsonObject.writeTo(new ObjectWriter() {
            private boolean isFirst = true;

            public void writeField(final String name, final WriteableJsonObject value) throws IOException {
                writeField(JsonNodeFactories.string(name), value);
            }

            public void writeField(final String name, final WriteableJsonArray value) throws IOException {
                writeField(JsonNodeFactories.string(name), value);
            }

            public void writeField(final String name, final WriteableJsonString value) throws IOException {
                writeField(JsonNodeFactories.string(name), value);
            }

            public void writeField(final String name, final WriteableJsonNumber value) throws IOException {
                writeField(JsonNodeFactories.string(name), value);
            }

            public void writeField(final String name, final JsonNode value) throws IOException {
                writeField(JsonNodeFactories.string(name), value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonObject value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonArray value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonString value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final JsonStringNode name, final WriteableJsonNumber value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final JsonStringNode name, final JsonNode value) throws IOException {
                writeName(name);
                write(value);
            }

            private void writeName(final JsonStringNode name) throws IOException {
                writeCommaIfRequired();
                write(name);
                writer.write(':');
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonObject value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonArray value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonString value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final WriteableJsonString name, final WriteableJsonNumber value) throws IOException {
                writeName(name);
                write(value);
            }

            public void writeField(final WriteableJsonString name, final JsonNode value) throws IOException {
                writeName(name);
                write(value);
            }

            private void writeName(final WriteableJsonString name) throws IOException {
                writeCommaIfRequired();
                write(name);
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
}
