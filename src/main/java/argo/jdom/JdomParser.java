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
import argo.staj.InvalidSyntaxRuntimeException;
import argo.staj.JsonStreamElement;
import argo.staj.JsonStreamException;
import argo.staj.StajParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static argo.jdom.JsonNodeBuilders.*;
import static argo.jdom.JsonNodeFactories.field;

/**
 * Parses a JSON character stream into a {@code JsonNode} object.  Instances of this class can safely be shared
 * between threads.
 */
public final class JdomParser {

    /**
     * Parse the specified JSON {@code String} into a {@code JsonNode} object.
     *
     * @param json the {@code String} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code String}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code String} does not represent valid JSON.
     * @throws IOException rethrown when reading characters from {@code in} throws {@code IOException}.
     */
    public JsonNode parse(final String json) throws InvalidSyntaxException, IOException {
        return parse(new StringReader(json));
    }

    /**
     * Parse the character stream from the specified {@code Reader} into a {@code JsonNode} object.
     *
     * @param reader the {@code Reader} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code Reader}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code Reader} does not represent valid JSON.
     * @throws IOException rethrown when reading characters from {@code in} throws {@code IOException}.
     */
    public JsonNode parse(final Reader reader) throws InvalidSyntaxException, IOException {
        return parse(new StajParser(reader));
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    JsonNode parse(final StajParser stajParser) throws IOException, InvalidSyntaxException {
        final JsonStringNodeFactory jsonStringNodeFactory = new JsonStringNodeFactory();
        final JsonNumberNodeFactory jsonNumberNodeFactory = new JsonNumberNodeFactory();
        final RootNodeContainer root = new RootNodeContainer();
        final Stack<NodeContainer> stack = new Stack<NodeContainer>();
        stack.push(root);

        try {
            while (stajParser.hasNext()) {
                final JsonStreamElement jsonStreamElement = stajParser.next();
                switch (jsonStreamElement.jsonStreamElementType()) {
                    case START_DOCUMENT:
                    case END_DOCUMENT:
                        break;
                    case START_ARRAY:
                        final ArrayNodeContainer arrayNodeContainer = new ArrayNodeContainer();
                        stack.peek().addNode(arrayNodeContainer);
                        stack.push(arrayNodeContainer);
                        break;
                    case START_OBJECT:
                        final ObjectNodeContainer objectNodeContainer = new ObjectNodeContainer();
                        stack.peek().addNode(objectNodeContainer);
                        stack.push(objectNodeContainer);
                        break;
                    case START_FIELD:
                        final FieldNodeContainer fieldNodeContainer = new FieldNodeContainer(jsonStringNodeFactory.jsonStringNode(asString(jsonStreamElement.reader(), 32)));
                        stack.peek().addField(fieldNodeContainer);
                        stack.push(fieldNodeContainer);
                        break;
                    case END_ARRAY:
                    case END_OBJECT:
                    case END_FIELD:
                        stack.pop();
                        break;
                    case NULL:
                        stack.peek().addNode(aNullBuilder());
                        break;
                    case TRUE:
                        stack.peek().addNode(aTrueBuilder());
                        break;
                    case FALSE:
                        stack.peek().addNode(aFalseBuilder());
                        break;
                    case STRING:
                        stack.peek().addNode(jsonStringNodeFactory.jsonStringNode(asString(jsonStreamElement.reader(), 32)));
                        break;
                    case NUMBER:
                        stack.peek().addNode(jsonNumberNodeFactory.jsonNumberNode(asString(jsonStreamElement.reader(), 16)));
                        break;
                    default:
                        throw new IllegalStateException("Coding failure in Argo:  Got a JsonStreamElement of unexpected type: " + jsonStreamElement);
                }
            }
        } catch (final InvalidSyntaxRuntimeException e) {
            throw InvalidSyntaxException.from(e);
        } catch (final JsonStreamException e) {
            throw e.getCause();
        }

        return root.build();
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
                final JsonNodeBuilder<JsonNode> newJsonNumberNode = aNumberBuilder(value); // TODO this validates the argument, but we already know it's a valid number
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
