/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.format.WriteableJsonArray;
import argo.format.WriteableJsonNumber;
import argo.format.WriteableJsonObject;
import argo.format.WriteableJsonString;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeVisitor;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;

import static argo.JsonEscapedString.escapeCharBufferTo;

abstract class JsonPrinter implements JsonNodeVisitor {

    static final Comparator<JsonField> JSON_FIELD_COMPARATOR = new Comparator<JsonField>() {
        public int compare(final JsonField left, final JsonField right) {
            return left.getNameText().compareTo(right.getNameText());
        }
    };
    final Writer writer;
    private final WriteBufferHolder writeBufferHolder = new WriteBufferHolder();

    JsonPrinter(final Writer writer) {
        this.writer = writer;
    }

    abstract void write(WriteableJsonArray writeableJsonArray) throws IOException;

    abstract void write(WriteableJsonObject writeableJsonObject) throws IOException;

    final void write(final WriteableJsonString writeableJsonString) throws IOException {
        writer.write('"');
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(writer, writeBufferHolder);
        try {
            writeableJsonString.writeTo(jsonStringEscapingWriter);
        } finally {
            jsonStringEscapingWriter.close();
        }
        writer.write('"');
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    final void write(final WriteableJsonNumber writeableJsonNumber) throws IOException {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(writer, writeBufferHolder);
        try {
            writeableJsonNumber.writeTo(jsonNumberValidatingWriter);
        } catch (final RuntimeException e) {
            jsonNumberValidatingWriter.endExceptionally();
            throw e;
        } finally {
            jsonNumberValidatingWriter.close();
        }
    }

    final void write(final JsonNode jsonNode) throws IOException {
        try {
            jsonNode.visit(this);
        } catch (final JsonPrinter.IORuntimeException e) {
            throw e.getCause();
        }
    }

    public final void object(final List<JsonField> fields) {
        try {
            throwingObject(fields);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    abstract void throwingObject(List<JsonField> fields) throws IOException;

    public final void array(final List<JsonNode> elements) {
        try {
            throwingArray(elements);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    abstract void throwingArray(List<JsonNode> elements) throws IOException;

    public final void string(final String value) {
        final int length = value.length();
        final char[] cbuf = length <= WriteBufferHolder.WRITE_BUFFER_SIZE ? writeBufferHolder.writeBuffer() : new char[length];
        value.getChars(0, length, cbuf, 0);
        try {
            writer.write('"');
            escapeCharBufferTo(writer, cbuf, 0, length);
            writer.write('"');
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void number(final String value) {
        try {
            writer.write(value);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void trueNode() {
        try {
            writer.write("true");
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void falseNode() {
        try {
            writer.write("false");
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void nullNode() {
        try {
            writer.write("null");
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static final class IORuntimeException extends RuntimeException {

        private final IOException typedCause;

        public IORuntimeException(final IOException cause) {
            super(cause);
            this.typedCause = cause;
        }

        @Override
        public IOException getCause() {
            return typedCause;
        }
    }

}
