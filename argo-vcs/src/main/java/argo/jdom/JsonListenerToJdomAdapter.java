package argo.jdom;

import argo.jax.JsonListener;
import static argo.jdom.JsonNodeFactory.*;

import java.util.*;

final class JsonListenerToJdomAdapter implements JsonListener {

    private final Stack stack = new Stack();
    private JsonNodeGenerator document;

    public JsonRootNode getDocument() {
        return (JsonRootNode)document.generateJsonValue();
    }

    public void startDocument() {
        stack.push(new MutableJsonDocument());
    }

    public void endDocument() {
        document = (JsonNodeGenerator) stack.pop();
    }

    public void startArray() {
        final MutableJsonArray mutableJsonArray = new MutableJsonArray();
        ((ThingWithValues) stack.peek()).addValue(mutableJsonArray);
        stack.push(mutableJsonArray);
    }

    public void endArray() {
        stack.pop();
    }

    public void startObject() {
        final MutableJsonObject jsonObject = new MutableJsonObject();
        ((ThingWithValues) stack.peek()).addValue(jsonObject);
        stack.push(jsonObject);
    }

    public void endObject() {
        stack.pop();
    }

    public void numberValue(final String value) {
        ((ThingWithValues) stack.peek()).addValue(JsonNodeFactory.aJsonNumber(value));
    }

    public void trueValue() {
        ((ThingWithValues) stack.peek()).addValue(aTrue());
    }

    public void falseValue() {
        ((ThingWithValues) stack.peek()).addValue(aFalse());
    }

    public void nullValue() {
        ((ThingWithValues) stack.peek()).addValue(aNull());
    }

    public void stringValue(final String value) {
        ((ThingWithValues) stack.peek()).addValue(JsonNodeFactory.aJsonStringNode(value));
    }

    public void startField(final String name) {
        final MutableField field = new MutableField(name);
        ((MutableJsonObject) stack.peek()).addField(field);
        stack.push(field);
    }

    public void endField() {
        stack.pop();
    }

    private static final class MutableJsonArray implements ThingWithValues, JsonNodeGenerator {
        private final List<Object> elements = new LinkedList<Object>();

        public void addValue(final Object element) {
            elements.add(element);
        }

        List<Object> getElements() {
            return elements;
        }

        public JsonNode generateJsonValue() {
            final List<JsonNode> jsonNodeElements = new ArrayList<JsonNode>(elements.size());
            for (Object element : elements) {
                jsonNodeElements.add(JsonListenerToJdomAdapter.generateJsonValue(element));
            }
            return JsonNodeFactory.aJsonArray(jsonNodeElements);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("MutableJsonArray elements:[")
                    .append(elements)
                    .append("]")
                    .toString();
        }
    }

    private static final class MutableJsonObject implements JsonNodeGenerator {
        private final List<MutableField> fields = new LinkedList<MutableField>();

        void addField(final MutableField field) {
            fields.add(field);
        }

        public List<MutableField> getFields() {
            return fields;
        }

        public JsonNode generateJsonValue() {
            final Map<String, JsonNode> jsonStringJsonValueHashMap = new HashMap<String, JsonNode>();
            for (MutableField field : fields) {
                jsonStringJsonValueHashMap.put(field.getName(), JsonListenerToJdomAdapter.generateJsonValue(field.getValue()));
            }
            return JsonNodeFactory.aJsonObject(jsonStringJsonValueHashMap);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("MutableJsonObject fields:[")
                    .append(fields)
                    .append("]")
                    .toString();
        }
    }

    private static final class MutableField implements ThingWithValues, JsonNodeGenerator {
        final String name;
        Object value;

        MutableField(final String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        Object getValue() {
            return value;
        }

        public void addValue(final Object value) {
            this.value = value;
        }

        public JsonNode generateJsonValue() {
            return JsonListenerToJdomAdapter.generateJsonValue(value);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("MutableJsonField name:[")
                    .append(name)
                    .append("], value:[")
                    .append(value)
                    .append("]")
                    .toString();
        }
    }

    private static final class MutableJsonDocument implements ThingWithValues, JsonNodeGenerator {
        Object value;

        public void addValue(final Object value) {
            this.value = value;
        }

        Object getValue() {
            return value;
        }

        public JsonNode generateJsonValue() {
            return JsonListenerToJdomAdapter.generateJsonValue(value);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("MutableJsonDocument value:[")
                    .append(value)
                    .append("]")
                    .toString();
        }
    }


    private static interface ThingWithValues {
        void addValue(Object value);
    }

    private static interface JsonNodeGenerator {
        JsonNode generateJsonValue();
    }

    private static JsonNode generateJsonValue(final Object value) {
        final JsonNode result;
        if (value instanceof JsonNode) {
            result = (JsonNode) value;
        } else {
            result = ((JsonNodeGenerator) value).generateJsonValue();
        }
        return result;
    }
}
