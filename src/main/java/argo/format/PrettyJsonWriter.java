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
 * JsonWriter that writes JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
public final class PrettyJsonWriter extends AbstractJsonWriter {

    private final String lineSeparator;

    @SuppressWarnings("SystemGetProperty")
    public PrettyJsonWriter() {
        lineSeparator = System.getProperty("line.separator");
    }

    @Override
    void write(final Writer writer, final WriteableJsonArray writeableJsonArray, final int depth) throws IOException {
        writer.write('[');
        final PrettyArrayWriter prettyArrayWriter = new PrettyArrayWriter(writer, depth);
        writeableJsonArray.writeTo(prettyArrayWriter);
        if (prettyArrayWriter.wroteFields()) {
            writer.write(lineSeparator);
            addTabs(writer, depth);
        }
        writer.write(']');
    }

    @Override
    void write(final Writer writer, final WriteableJsonObject writeableJsonObject, final int depth) throws IOException {
        writer.write('{');
        final PrettyObjectWriter prettyObjectWriter = new PrettyObjectWriter(writer, depth);
        writeableJsonObject.writeTo(prettyObjectWriter);
        if (prettyObjectWriter.wroteFields()) {
            writer.write(lineSeparator);
            addTabs(writer, depth);
        }
        writer.write('}');

    }

    @Override
    void writeObject(final Writer writer, final Iterable<JsonField> fields, final int depth) throws IOException {
        boolean first = true;
        writer.write('{');
        for (final JsonField field : fields) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            writer.write(lineSeparator);
            addTabs(writer, depth + 1);
            write(writer, field.getName());
            writer.write(": ");
            write(writer, field.getValue(), depth + 1);
        }
        if (!first) {
            writer.write(lineSeparator);
            addTabs(writer, depth);
        }
        writer.write('}');
    }

    @Override
    void writeArray(final Writer writer, final Iterable<JsonNode> elements, final int depth) throws IOException {
        boolean first = true;
        writer.write('[');
        for (final JsonNode element : elements) {
            if (!first) {
                writer.write(',');
            }
            first = false;
            writer.write(lineSeparator);
            addTabs(writer, depth + 1);
            write(writer, element, depth + 1);
        }
        if (!first) {
            writer.write(lineSeparator);
            addTabs(writer, depth);
        }
        writer.write(']');
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
            writePreamble();
            write(writer, element, indent + 1);
        }

        public void writeElement(final WriteableJsonArray element) throws IOException {
            writePreamble();
            write(writer, element, indent + 1);
        }

        public void writeElement(final WriteableJsonString element) throws IOException {
            writePreamble();
            write(writer, element);
        }

        public void writeElement(final WriteableJsonNumber element) throws IOException {
            writePreamble();
            write(writer, element);
        }

        public void writeElement(final JsonNode element) throws IOException {
            writePreamble();
            write(writer, element, indent + 1);
        }

        private void writePreamble() throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
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
            write(writer, value, indent + 1);
        }

        public void writeField(final JsonStringNode name, final WriteableJsonArray value) throws IOException {
            writeName(name);
            write(writer, value, indent + 1);
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
            writePreamble();
            write(writer, name);
            writer.write(": ");
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
            writePreamble();
            write(writer, name);
            writer.write(": ");
        }

        public void writeField(final JsonField jsonField) throws IOException {
            writeField(jsonField.getName(), jsonField.getValue());
        }

        private void writePreamble() throws IOException {
            if (!isFirst) {
                writer.write(',');
            }
            isFirst = false;
            writer.write(lineSeparator);
            addTabs(writer, indent + 1);
        }

        boolean wroteFields() {
            return !isFirst;
        }
    }
}
