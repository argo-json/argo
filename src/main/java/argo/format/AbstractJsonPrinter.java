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
import argo.jdom.JsonNodeVisitor;

import java.io.IOException;
import java.io.Writer;

import static argo.format.JsonEscapedString.escapeStringTo;

abstract class AbstractJsonPrinter implements JsonNodeVisitor {

    final Writer writer;

    AbstractJsonPrinter(final Writer writer) {
        this.writer = writer;
    }

    final void write(final WriteableJsonArray writeableJsonArray) throws IOException {
        try {
            runtimeExceptionThrowingWrite(writeableJsonArray);
        } catch (final IORuntimeException e) {
            throw e.getCause();
        }
    }

    abstract void runtimeExceptionThrowingWrite(final WriteableJsonArray writeableJsonArray) throws IOException;

    final void write(final WriteableJsonObject writeableJsonObject) throws IOException {
        try {
            runtimeExceptionThrowingWrite(writeableJsonObject);
        } catch (final IORuntimeException e) {
            throw e.getCause();
        }
    }

    abstract void runtimeExceptionThrowingWrite(final WriteableJsonObject writeableJsonObject) throws IOException;

    final void write(final WriteableJsonString writeableJsonString) throws IOException {
        writer.write('"'); // TODO combine this implementation with the visitor implementation of string writing
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(writer);
        try {
            writeableJsonString.writeTo(jsonStringEscapingWriter);
        } finally {
            jsonStringEscapingWriter.close();
        }
        writer.write('"');
    }

    final void write(final WriteableJsonNumber writeableJsonNumber) throws IOException {
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

    public final void object(final Iterable<JsonField> fields) {
        try {
            throwingObject(fields);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    abstract void throwingObject(final Iterable<JsonField> fields) throws IOException;

    public final void array(final Iterable<JsonNode> elements) {
        try {
            throwingArray(elements);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    abstract void throwingArray(final Iterable<JsonNode> elements) throws IOException;

    public final void string(final String value) {
        try {
            writer.write('"');
            escapeStringTo(writer, value);
            writer.write('"');
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void number(final String value) {
        try {
            writer.write(value);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void trueNode() {
        try {
            writer.write("true");
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void falseNode() {
        try {
            writer.write("false");
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public final void nullNode() {
        try {
            writer.write("null");
        } catch (IOException e) {
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
