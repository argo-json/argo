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

import argo.jdom.*;
import argo.saj.JsonListener;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import static argo.JsonStreamElement.NonTextJsonStreamElement.END_DOCUMENT;
import static argo.jdom.JsonNodeBuilders.*;
import static argo.jdom.JsonNodeFactories.prevalidatedNumberBuilder;

/**
 * Provides operations to parse JSON.
 * <p>
 * Instances of this class are immutable, reusable, and thread-safe.
 */
public final class JsonParser {

    /**
     * Parses the character stream from the given {@code Reader} into a {@code JsonNode} object.
     *
     * @param reader the {@code Reader} to parse.
     * @return a {@code JsonNode} representing the JSON read from the given {@code Reader}.
     * @throws InvalidSyntaxException if the characters streamed from the given {@code Reader} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public JsonNode parse(final Reader reader) throws InvalidSyntaxException, IOException {
        return parse(new ParseExecutor() {
            public void parseUsing(final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
                parseStreaming(reader, jsonListener);
            }
        });
    }

    /**
     * Parses the given JSON {@code String} into a {@code JsonNode} object.
     *
     * @param json the {@code String} to parse.
     * @return a {@code JsonNode} representing the JSON read from the given {@code String}.
     * @throws InvalidSyntaxException if the characters streamed from the given {@code String} do not represent valid JSON.
     */
    public JsonNode parse(final String json) throws InvalidSyntaxException {
        try {
            return parse(new StringReader(json));
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringReader threw an IOException");
        }
    }

    /**
     * Parses the character stream from the given {@code Reader} into an {@code Iterator} of {@code JsonStreamElement}s.
     *
     * @param reader the {@code Reader} to parse.
     * @return an {@code Iterator} of {@code JsonStreamElement}s reading from the given {@code Reader}.
     */
    public Iterator<JsonStreamElement> parseStreaming(final Reader reader) {
        return new Iterator<JsonStreamElement>() {
            private final PositionTrackingPushbackReader pushbackReader = new PositionTrackingPushbackReader(reader);  // TODO tolerate byte order mark?  See https://datatracker.ietf.org/doc/html/rfc8259#section-8.1
            private final Stack<JsonStreamElementType> stack = new Stack<JsonStreamElementType>();
            private JsonStreamElement current;
            private JsonStreamElement next;

            public boolean hasNext() {
                if (current != null && current == END_DOCUMENT) {
                    return false;
                } else if (next == null) {
                    next = getNextElement();
                }
                return true;
            }

            public JsonStreamElement next() {
                if (next == null) {
                    current = getNextElement();
                } else {
                    current = next;
                    next = null;
                }
                return current;
            }

            private JsonStreamElement getNextElement() {
                if (current == null) {
                    stack.push(JsonStreamElementType.START_DOCUMENT);
                    return JsonStreamElement.NonTextJsonStreamElement.START_DOCUMENT;
                } else {
                    try {
                        current.close();
                        return current.jsonStreamElementType().parseNext(pushbackReader, stack);
                    } catch (final IOException e) {
                        throw new JsonStreamException("Failed to read from Reader", e);
                    }
                }
            }

            /**
             * Not supported.
             */
            public void remove() {
                throw new UnsupportedOperationException("JsonParser cannot remove elements from JSON it has parsed");
            }
        };
    }

    /**
     * Parses the given JSON {@code String} into an {@code Iterator} of {@code JsonStreamElement}s.
     *
     * @param json the {@code String} to parse.
     * @return an {@code Iterator} of {@code JsonStreamElement}s reading from the given {@code Reader}.
     */
    public Iterator<JsonStreamElement> parseStreaming(final String json) { // TODO maybe introduce a sub-interface of Iterator<JsonStreamElement> to facilitate documentation of runtime exceptions
        return parseStreaming(new StringReader(json));
    }

    /**
     * Parses the character stream from the given {@code Reader} into calls to the given JsonListener.
     *
     * @param reader                  the {@code Reader} to parse.
     * @param jsonListener            the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException if the characters streamed from the given {@code Reader} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public void parseStreaming(final Reader reader, final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
        parseStreaming(parseStreaming(reader), jsonListener);
    }

    /**
     * Parses the given JSON {@code String} into calls to the given JsonListener.
     *
     * @param json                    the {@code String} to parse.
     * @param jsonListener            the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException if the characters streamed from the given {@code String} do not represent valid JSON.
     */
    public void parseStreaming(final String json, final JsonListener jsonListener) throws InvalidSyntaxException { // TODO move JsonListener?
        try {
            parseStreaming(new StringReader(json), jsonListener);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringReader threw an IOException");
        }
    }

    void parseStreaming(final Iterator<JsonStreamElement> stajParser, final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
        try {
            while (stajParser.hasNext()) {
                stajParser.next().visit(jsonListener);
            }
        } catch (final InvalidSyntaxRuntimeException e) {
            throw InvalidSyntaxException.from(e);
        } catch (final JsonStreamException e) {
            throw e.getCause();
        }
    }

    interface ParseExecutor {
        void parseUsing(JsonListener jsonListener) throws InvalidSyntaxException, IOException;
    }

    @SuppressWarnings("PMD.ExceptionAsFlowControl") // TODO this is apparently fixed in PMD 7.0.0
    JsonNode parse(final ParseExecutor parseExecutor) throws InvalidSyntaxException, IOException {
        final JsonStringNodeFactory jsonStringNodeFactory = new JsonStringNodeFactory();
        final JsonNumberNodeFactory jsonNumberNodeFactory = new JsonNumberNodeFactory();
        final RootNodeContainer root = new RootNodeContainer();
        final Stack<NodeContainer> stack = new Stack<NodeContainer>();
        stack.push(root);
        try {
            parseExecutor.parseUsing(new JsonListener() {
                public void startDocument() {}

                public void endDocument() {}

                public void startArray() {
                    final ArrayNodeContainer arrayNodeContainer = new ArrayNodeContainer();
                    stack.peek().addNode(arrayNodeContainer);
                    stack.push(arrayNodeContainer);
                }

                public void endArray() {
                    stack.pop();
                }

                public void startObject() {
                    final ObjectNodeContainer objectNodeContainer = new ObjectNodeContainer();
                    stack.peek().addNode(objectNodeContainer);
                    stack.push(objectNodeContainer);
                }

                public void endObject() {
                    stack.pop();
                }

                public void startField(final Reader name) {
                    try {
                        final FieldNodeContainer fieldNodeContainer = new FieldNodeContainer(jsonStringNodeFactory.jsonStringNode(asString(name, 32)));
                        stack.peek().addField(fieldNodeContainer);
                        stack.push(fieldNodeContainer);
                    } catch (final IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void endField() {
                    stack.pop();
                }

                public void stringValue(final Reader value) {
                    try {
                        stack.peek().addNode(jsonStringNodeFactory.jsonStringNode(asString(value, 32)));
                    } catch (final IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void numberValue(final Reader value) {
                    try {
                        stack.peek().addNode(jsonNumberNodeFactory.jsonNumberNode(asString(value, 16)));
                    } catch (final IOException e) {
                        throw new IORuntimeException(e);
                    }
                }

                public void trueValue() {
                    stack.peek().addNode(aTrueBuilder());
                }

                public void falseValue() {
                    stack.peek().addNode(aFalseBuilder());
                }

                public void nullValue() {
                    stack.peek().addNode(aNullBuilder());
                }
            });
        } catch (final IORuntimeException e) {
            throw e.getCause();
        }
        return root.build();
    }

    private static final class IORuntimeException extends RuntimeException {
        private final IOException typedCause;

        IORuntimeException(final IOException cause) {
            super(cause);
            this.typedCause = cause;
        }

        @Override
        public IOException getCause() {
            return typedCause;
        }
    }

    private interface NodeContainer {

        void addNode(JsonNodeBuilder<?> jsonNodeBuilder);

        void addField(FieldNodeContainer fieldNodeContainer);

    }

    private static final class RootNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {

        private JsonNodeBuilder<?> valueBuilder;

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            if (valueBuilder == null) {
                valueBuilder = jsonNodeBuilder;
            } else {
                throw new RuntimeException("Coding failure in Argo:  Attempt to add more than one root node");
            }
        }

        public void addField(final FieldNodeContainer fieldNodeContainer) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field as root node");
        }

        public JsonNode build() {
            if (valueBuilder == null) {
                throw new RuntimeException("Coding failure in Argo:  Attempt to build document with no root node");
            } else {
                return valueBuilder.build();
            }
        }
    }

    private static final class ArrayNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        private final JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            arrayBuilder.withElement(jsonNodeBuilder);
        }

        public void addField(final FieldNodeContainer fieldNodeContainer) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to an array");
        }

        public JsonNode build() {
            return arrayBuilder.build();
        }
    }

    private static final class ObjectNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        private final JsonObjectNodeBuilder jsonObjectNodeBuilder = anObjectBuilder();

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a node to an object");
        }

        public void addField(final FieldNodeContainer fieldNodeContainer) {
            jsonObjectNodeBuilder.withField(fieldNodeContainer.name, fieldNodeContainer);
        }

        public JsonNode build() {
            return jsonObjectNodeBuilder.build();
        }
    }

    private static final class FieldNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        final JsonStringNode name;
        private JsonNodeBuilder<?> valueBuilder;

        FieldNodeContainer(final JsonStringNode name) {
            this.name = name;
        }

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            valueBuilder = jsonNodeBuilder;
        }

        public void addField(final FieldNodeContainer fieldNodeContainer) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to a field");
        }

        public JsonNode build() {
            if (valueBuilder == null) {
                throw new RuntimeException("Coding failure in Argo:  Attempt to create a field without a value");
            } else {
                return valueBuilder.build();
            }
        }
    }

    private static final class JsonStringNodeFactory {
        private final Map<String, JsonStringNode> existingJsonStringNodes = new HashMap<String, JsonStringNode>();

        JsonStringNode jsonStringNode(final String value) {
            final JsonStringNode cachedStringNode = existingJsonStringNodes.get(value);
            if (cachedStringNode == null) {
                final JsonStringNode newJsonStringNode = JsonNodeFactories.string(value);
                existingJsonStringNodes.put(value, newJsonStringNode);
                return newJsonStringNode;
            } else {
                return cachedStringNode;
            }
        }
    }

    private static final class JsonNumberNodeFactory {
        private final Map<String, JsonNodeBuilder<JsonNode>> existingJsonNumberNodes = new HashMap<String, JsonNodeBuilder<JsonNode>>(); // TODO make object reuse switchable.

        JsonNodeBuilder<JsonNode> jsonNumberNode(final String value) {
            final JsonNodeBuilder<JsonNode> cachedNumberNode = existingJsonNumberNodes.get(value);
            if (cachedNumberNode == null) {
                final JsonNodeBuilder<JsonNode> newJsonNumberNode = prevalidatedNumberBuilder(new PrevalidatedNumber(value));
                existingJsonNumberNodes.put(value, newJsonNumberNode);
                return newJsonNumberNode;
            } else {
                return cachedNumberNode;
            }
        }
    }

    private static String asString(final Reader reader, final int initialCapacity) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder(initialCapacity);
        int c;
        while((c = reader.read()) != -1) {
            stringBuilder.append((char) c);
        }
        return stringBuilder.toString();
    }

    /**
     * Internal class
     */
    public static final class PrevalidatedNumber {
        public final String value;

        PrevalidatedNumber(final String value) {
            this.value = value;
        }

    }

}
