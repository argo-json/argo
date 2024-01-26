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

    @SuppressWarnings("PMD.ExceptionAsFlowControl") // TODO this is apparently fixed in PMD 7.0.0
    protected final void write(final Writer writer, final JsonNode jsonNode, final int depth) throws IOException {
        try {
            jsonNode.visit(new JsonNodeVisitor() {
                public void object(final Iterable<JsonField> fields) {
                    try {
                        writeObject(writer, fields, depth);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void array(final Iterable<JsonNode> elements) {
                    try {
                        writeArray(writer, elements, depth);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void string(final String value) {
                    try {
                        writer.write('"');
                        escapeStringTo(writer, value);
                        writer.write('"');
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void number(final String value) {
                    try {
                        writer.write(value);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void trueNode() {
                    try {
                        writer.write("true");
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void falseNode() {
                    try {
                        writer.write("false");
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void nullNode() {
                    try {
                        writer.write("null");
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }
            });
        } catch (IORuntimeException e) {
            throw e.getCause();
        }
    }

    private static final class IORuntimeException extends RuntimeException {

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

    abstract void writeObject(Writer writer, Iterable<JsonField> fields, int depth) throws IOException;

    abstract void writeArray(Writer writer, Iterable<JsonNode> elements, int depth) throws IOException;
}
