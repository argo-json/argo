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
import argo.jdom.JsonStringNode;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import static argo.format.JsonEscapedString.escapeStringTo;
import static argo.jdom.JsonNodeFactories.string;

/**
 * JsonWriter that writes JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
public final class PrettyJsonWriter implements JsonWriter {

    private final String lineSeparator;

    public PrettyJsonWriter() {
        lineSeparator = System.getProperty("line.separator");

    }

    public void write(final Writer writer, final WriteableJsonArray writeableJsonArray) throws IOException {
        write(writer, writeableJsonArray, 0);
    }

    private void write(final Writer writer, final WriteableJsonArray writeableJsonArray, final int indent) throws IOException {
        writer.write('[');
        final PrettyArrayWriter prettyArrayWriter = new PrettyArrayWriter(writer, indent);
        writeableJsonArray.writeTo(prettyArrayWriter);
        if (prettyArrayWriter.wroteFields()) {
            writer.write(lineSeparator);
            addTabs(writer, indent);
        }
        writer.write(']');
    }

    public void write(final Writer writer, final WriteableJsonObject writeableJsonObject) throws IOException {
        write(writer, writeableJsonObject, 0);
    }

    public void write(Writer writer, WriteableJsonString writeableJsonString) throws IOException {
        writer.write('"');
        writeableJsonString.writeTo(new JsonStringEscapingWriter(writer));
        writer.write('"');
    }

    private void write(final Writer writer, final WriteableJsonObject writeableJsonObject, final int indent) throws IOException {
        writer.write('{');
        final PrettyObjectWriter prettyObjectWriter = new PrettyObjectWriter(writer, indent);
        writeableJsonObject.writeTo(prettyObjectWriter);
        if (prettyObjectWriter.wroteFields()) {
            writer.write(lineSeparator);
            addTabs(writer, indent);
        }
        writer.write('}');

    }

    public void write(final Writer writer, final JsonNode jsonNode) throws IOException {
        write(writer, jsonNode, 0);
    }

    private void write(final Writer writer, final JsonNode jsonNode, final int indent) throws IOException {
        switch (jsonNode.getType()) {
            case ARRAY:
                writer.write('[');
                final List<JsonNode> elements = jsonNode.getElements();
                final Iterator<JsonNode> elementsIterator = elements.iterator();
                while (elementsIterator.hasNext()) {
                    final JsonNode node = elementsIterator.next();
                    writer.write(lineSeparator);
                    addTabs(writer, indent + 1);
                    write(writer, node, indent + 1);
                    if (elementsIterator.hasNext()) {
                        writer.write(',');
                    }
                }
                if (!elements.isEmpty()) {
                    writer.write(lineSeparator);
                    addTabs(writer, indent);
                }
                writer.write(']');
                break;
            case OBJECT:
                writer.write('{');
                final List<JsonField> fields = jsonNode.getFieldList();
                final Iterator<JsonField> fieldsIterator = fields.iterator();
                while (fieldsIterator.hasNext()) {
                    final JsonField field = fieldsIterator.next();
                    writer.write(lineSeparator);
                    addTabs(writer, indent + 1);
                    writeEscapedString(field.getNameText(), writer);
                    writer.write(": ");
                    write(writer, field.getValue(), indent + 1);
                    if (fieldsIterator.hasNext()) {
                        writer.write(',');
                    }
                }
                if (!fields.isEmpty()) {
                    writer.write(lineSeparator);
                    addTabs(writer, indent);
                }
                writer.write('}');
                break;
            case STRING:
                writeEscapedString(jsonNode.getText(), writer);
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
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + jsonNode.getType() + "];");
        }
    }

    private static void writeEscapedString(final String text, final Writer writer) throws IOException {
        writer.write('"');
        escapeStringTo(writer, text);
        writer.write('"');
    }

    private static void addTabs(final Writer writer, final int tabs) throws IOException {
        for (int i = 0; i < tabs; i++) {
            writer.write('\t');
        }
    }

    private final class PrettyArrayWriter implements ArrayWriter {
        private final Writer writer;
        private final int indent;
        private boolean isFirst = true;

        PrettyArrayWriter(final Writer writer, final int indent) {
            this.writer = writer;
            this.indent = indent;
        }

        public void writeElement(final WriteableJsonObject element) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, element, indent + 1);
        }

        public void writeElement(final WriteableJsonArray element) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, element, indent + 1);
        }

        public void writeElement(final WriteableJsonString element) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, element);
        }

        public void writeElement(final JsonNode element) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, element, indent + 1);
        }

        boolean wroteFields() {
            return !isFirst;
        }
    }

    private final class PrettyObjectWriter implements ObjectWriter {
        private final Writer writer;
        private final int indent;
        private boolean isFirst = true;

        PrettyObjectWriter(final Writer writer, final int indent) {
            this.writer = writer;
            this.indent = indent;
        }

        public void writeField(final String name, final WriteableJsonObject value) throws IOException {
            writeField(string(name), value);
        }

        public void writeField(final String name, final WriteableJsonArray value) throws IOException {
            writeField(string(name), value);
        }

        public void writeField(final String name, final JsonNode value) throws IOException {
            writeField(string(name), value);
        }

        public void writeField(final String name, final WriteableJsonString value) throws IOException {
            writeField(string(name), value);
        }

        public void writeField(final JsonStringNode name, final WriteableJsonObject value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value, indent + 1);
        }

        public void writeField(final JsonStringNode name, final WriteableJsonArray value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value, indent + 1);
        }

        public void writeField(final JsonStringNode name, final WriteableJsonString value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final JsonStringNode name, final JsonNode value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final WriteableJsonString name, final WriteableJsonObject value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final WriteableJsonString name, final WriteableJsonArray value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final WriteableJsonString name, final WriteableJsonString value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final WriteableJsonString name, final JsonNode value) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, name);
            writer.write(": ");
            write(writer, value);
        }

        public void writeField(final JsonField jsonField) throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
            write(writer, jsonField.getName(), indent + 1);
            writer.write(": ");
            write(writer, jsonField.getValue(), indent + 1);
        }

        boolean wroteFields() {
            return !isFirst;
        }
    }
}
