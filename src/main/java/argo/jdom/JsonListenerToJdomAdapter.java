/*
 * Copyright 2015 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.saj.JsonListener;

import java.util.Stack;

import static argo.jdom.JsonNodeBuilders.*;
import static argo.jdom.JsonNodeFactories.field;

final class JsonListenerToJdomAdapter implements JsonListener {

    private final Stack<NodeContainer> stack = new Stack<NodeContainer>();
    private JsonNodeBuilder<? extends JsonNode> root;

    JsonNode getDocument() {
        return root.build();
    }

    public void startDocument() {
    }

    public void endDocument() {
    }

    public void startArray() {
        final ArrayNodeContainer arrayNodeContainer = new ArrayNodeContainer();
        addValue(arrayNodeContainer);
        stack.push(arrayNodeContainer);
    }

    public void endArray() {
        stack.pop();
    }

    public void startObject() {
        final ObjectNodeContainer objectNodeContainer = new ObjectNodeContainer();
        addValue(objectNodeContainer);
        stack.push(objectNodeContainer);
    }

    public void endObject() {
        stack.pop();
    }

    public void startField(final String name) {
        final FieldNodeContainer fieldNodeContainer = new FieldNodeContainer(name);
        stack.peek().addField(fieldNodeContainer);
        stack.push(fieldNodeContainer);
    }

    public void endField() {
        stack.pop();
    }

    public void numberValue(final String value) {
        addValue(aNumberBuilder(value));
    }

    public void trueValue() {
        addValue(aTrueBuilder());
    }

    public void stringValue(final String value) {
        addValue(aStringBuilder(value));
    }

    public void falseValue() {
        addValue(aFalseBuilder());
    }

    public void nullValue() {
        addValue(aNullBuilder());
    }

    private void addValue(final JsonNodeBuilder<? extends JsonNode> nodeBuilder) {
        if (root == null) {
            root = nodeBuilder;
        } else {
            stack.peek().addNode(nodeBuilder);
        }
    }

    private interface NodeContainer {

        void addNode(JsonNodeBuilder jsonNodeBuilder);

        void addField(JsonFieldBuilder jsonFieldBuilder);

    }

    private static final class ArrayNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        private final JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();

        public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
            arrayBuilder.withElement(jsonNodeBuilder);
        }

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to an array.");
        }

        public JsonNode build() {
            return arrayBuilder.build();
        }
    }

    private static final class ObjectNodeContainer implements NodeContainer, JsonNodeBuilder<JsonNode> {
        private final JsonObjectNodeBuilder objectNodeBuilder = anObjectBuilder();

        public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a node to an object.");
        }

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            objectNodeBuilder.withFieldBuilder(jsonFieldBuilder);
        }

        public JsonNode build() {
            return objectNodeBuilder.build();
        }
    }

    private static final class FieldNodeContainer implements NodeContainer, JsonFieldBuilder {
        private final String name;
        private JsonNodeBuilder valueBuilder;

        FieldNodeContainer(String name) {
            this.name = name;
        }

        public void addNode(final JsonNodeBuilder jsonNodeBuilder) {
            valueBuilder = jsonNodeBuilder;
        }

        public void addField(final JsonFieldBuilder jsonFieldBuilder) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to add a field to a field.");
        }

        public String name() {
            return name;
        }

        public JsonField build() {
            if (valueBuilder == null) {
                throw new RuntimeException("Coding failure in Argo:  Attempt to create a field without a value.");
            } else {
                return field(name, valueBuilder.build());
            }
        }
    }
}
