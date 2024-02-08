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

final class PrettyJsonPrinter extends AbstractJsonPrinter {

    private final String lineSeparator;
    private int depth = 0;

    PrettyJsonPrinter(final Writer writer, final String lineSeparator) {
        super(writer);
        this.lineSeparator = lineSeparator;
    }

    private void addTabs() throws IOException {
        for (int i = 0; i < depth; i++) {
            writer.write('\t');
        }
    }

    @Override
    void throwingObject(final Iterable<JsonField> fields) throws IOException {
        boolean first = true;
        writer.write('{');
        depth++;
        for (final JsonField field : fields) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            writer.write(lineSeparator);
            addTabs();
            write(field.getName());
            writer.write(": ");
            write(field.getValue());
        }
        depth--;
        if (!first) {
            writer.write(lineSeparator);
            addTabs();
        }
        writer.write('}');
    }

    @Override
    void throwingArray(final Iterable<JsonNode> elements) throws IOException {
        boolean first = true;
        writer.write('[');
        depth++;
        for (final JsonNode element : elements) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            writer.write(lineSeparator);
            addTabs();
            write(element);
        }
        depth--;
        if (!first) {
            writer.write(lineSeparator);
            addTabs();
        }
        writer.write(']');
    }

    @Override
    void write(final WriteableJsonArray writeableJsonArray) throws IOException {
        writer.write('[');
        depth++;
        final boolean[] isFirst = {true};
        writeableJsonArray.writeTo(new ArrayWriter() {

            public void writeElement(final WriteableJsonObject element) throws IOException {
                writePreamble();
                write(element);
            }

            public void writeElement(final WriteableJsonArray element) throws IOException {
                writePreamble();
                write(element);
            }

            public void writeElement(final WriteableJsonString element) throws IOException {
                writePreamble();
                write(element);
            }

            public void writeElement(final WriteableJsonNumber element) throws IOException {
                writePreamble();
                write(element);
            }

            public void writeElement(final JsonNode element) throws IOException {
                writePreamble();
                write(element);
            }

            private void writePreamble() throws IOException {
                if (!isFirst[0]) {
                    writer.write(',');
                }
                isFirst[0] = false;
                writer.write(lineSeparator);
                addTabs();
            }

        });
        depth--;
        if (!isFirst[0]) {
            writer.write(lineSeparator);
            addTabs();
        }
        writer.write(']');
    }

    @Override
    void write(final WriteableJsonObject writeableJsonObject) throws IOException {
        writer.write('{');
        depth++;
        final boolean[] isFirst = {true};
        writeableJsonObject.writeTo(new ObjectWriter() {
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
                writePreamble();
                write(name);
                writer.write(": ");
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
                writePreamble();
                write(name);
                writer.write(": ");
            }

            public void writeField(final JsonField jsonField) throws IOException {
                writeField(jsonField.getName(), jsonField.getValue());
            }

            private void writePreamble() throws IOException {
                if (!isFirst[0]) {
                    writer.write(',');
                }
                isFirst[0] = false;
                writer.write(lineSeparator);
                addTabs();
            }
        });
        depth--;
        if (!isFirst[0]) {
            writer.write(lineSeparator);
            addTabs();
        }
        writer.write('}');
    }
}
