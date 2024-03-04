/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.saj.InvalidSyntaxException;
import argo.saj.JsonListener;
import argo.saj.SajParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static argo.jdom.JsonNodeBuilders.*;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNumberNode.prevalidatedJsonNumberNode;

/**
 * Parses a JSON character stream into a {@code JsonNode} object.  Instances of this class can safely be shared
 * between threads.
 */
public final class JdomParser {

    private static final SajParser SAJ_PARSER = new SajParser();

    /**
     * Parses the specified JSON {@code String} into a {@code JsonNode} object.
     *
     * @param json the {@code String} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code String}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code String} does not represent valid JSON.
     */
    public JsonNode parse(final String json) throws InvalidSyntaxException {
        try {
            return parse(new StringReader(json));
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringReader threw an IOException");
        }
    }

    /**
     * Parses the character stream from the specified {@code Reader} into a {@code JsonNode} object.
     *
     * @param reader the {@code Reader} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code Reader}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code Reader} does not represent valid JSON.
     * @throws IOException rethrown when reading characters from {@code in} throws {@code IOException}.
     */
    public JsonNode parse(final Reader reader) throws InvalidSyntaxException, IOException {
        return parse(new ParseExecutor() {
            public void parseUsing(final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
                SAJ_PARSER.parse(reader, jsonListener);
            }
        });
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

        void addField(JsonFieldBuilder jsonFieldBuilder);

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

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
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

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to an array");
        }

        public JsonNode build() {
            return arrayBuilder.build();
        }
    }

    private static final class ObjectNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        private final JsonFieldIteratorBuilder<JsonFieldBuilder> jsonFieldIteratorBuilder = new DuplicateKeyPermittingJsonFieldIteratorBuilder<JsonFieldBuilder>();

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a node to an object");
        }

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            jsonFieldIteratorBuilder.add(jsonFieldBuilder);
        }

        public JsonNode build() {
            return JsonObject.jsonObject(jsonFieldIteratorBuilder.build(), jsonFieldIteratorBuilder.size());
        }
    }

    private static final class FieldNodeContainer implements NodeContainer, JsonFieldBuilder {
        private final JsonStringNode name;
        private JsonNodeBuilder<?> valueBuilder;

        FieldNodeContainer(final JsonStringNode name) {
            this.name = name;
        }

        public void addNode(final JsonNodeBuilder<?> jsonNodeBuilder) {
            valueBuilder = jsonNodeBuilder;
        }

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to a field");
        }

        public JsonField build() {
            if (valueBuilder == null) {
                throw new RuntimeException("Coding failure in Argo:  Attempt to create a field without a value");
            } else {
                return field(name, valueBuilder.build());
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
                final JsonNodeBuilder<JsonNode> newJsonNumberNode = prevalidatedJsonNumberNode(value);
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

}
