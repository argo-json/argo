package argo.jdom;

import argo.token.JsonListener;

import java.util.*;

final class JsonListenerToJdomAdapter implements JsonListener {

    private final Stack stack = new Stack();
    private JsonValueGenerator document;

    public JsonValue getDocument() {
        return document.generateJsonValue();
    }

    public void startDocument() {
        stack.push(new MutableJsonDocument());
    }

    public void endDocument() {
        // TODO check type?
        document = (JsonValueGenerator) stack.pop();
    }

    public void startArray() {
        final MutableJsonArray mutableJsonArray = new MutableJsonArray();
        ((ThingWithValues) stack.peek()).addValue(mutableJsonArray);
        stack.push(mutableJsonArray);
    }

    public void endArray() {
        // TODO check type?
        stack.pop();
    }

    public void startObject() {
        final MutableJsonObject jsonObject = new MutableJsonObject();
        ((ThingWithValues) stack.peek()).addValue(jsonObject);
        stack.push(jsonObject);
    }

    public void endObject() {
        // TODO check type?
        stack.pop();
    }

    public void numberValue(final String value) {
        ((ThingWithValues) stack.peek()).addValue(new JsonNumber(value));
    }

    public void trueValue() {
        ((ThingWithValues) stack.peek()).addValue(JsonConstants.TRUE);
    }

    public void falseValue() {
        ((ThingWithValues) stack.peek()).addValue(JsonConstants.FALSE);
    }

    public void nullValue() {
        ((ThingWithValues) stack.peek()).addValue(JsonConstants.NULL);
    }

    public void stringValue(final String value) {
        ((ThingWithValues) stack.peek()).addValue(new JsonString(value));
    }

    public void startField(final String name) {
        final MutableField field = new MutableField(name);
        ((MutableJsonObject) stack.peek()).addField(field);
        stack.push(field);
    }

    public void endField() {
        // TODO check type?
        stack.pop();
    }

    private static final class MutableJsonArray implements ThingWithValues, JsonValueGenerator {
        private final List<Object> elements = new LinkedList<Object>();

        public void addValue(final Object element) {
            elements.add(element);
        }

        List<Object> getElements() {
            return elements;
        }

        public JsonValue generateJsonValue() {
            final List<JsonValue> jsonValueElements = new ArrayList<JsonValue>(elements.size());
            for (Object element : elements) {
                jsonValueElements.add(JsonListenerToJdomAdapter.generateJsonValue(element));
            }
            return new JsonArray(jsonValueElements);
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

    private static final class MutableJsonObject implements JsonValueGenerator {
        private final List<MutableField> fields = new LinkedList<MutableField>();

        void addField(final MutableField field) {
            fields.add(field);
        }

        public List<MutableField> getFields() {
            return fields;
        }

        public JsonValue generateJsonValue() {
            final Map<JsonString, JsonValue> jsonStringJsonValueHashMap = new HashMap<JsonString, JsonValue>();
            for (MutableField field : fields) {
                jsonStringJsonValueHashMap.put(new JsonString(field.getName()), JsonListenerToJdomAdapter.generateJsonValue(field.getValue()));
            }
            return new JsonObject(jsonStringJsonValueHashMap);
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

    private static final class MutableField implements ThingWithValues, JsonValueGenerator {
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

        public JsonValue generateJsonValue() {
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

    private static final class MutableJsonDocument implements ThingWithValues, JsonValueGenerator {
        Object value;

        public void addValue(final Object value) {
            this.value = value;
        }

        Object getValue() {
            return value;
        }

        public JsonValue generateJsonValue() {
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

    private static interface JsonValueGenerator {
        JsonValue generateJsonValue();
    }

    private static JsonValue generateJsonValue(final Object value) {
        final JsonValue result;
        if (value instanceof JsonValue) {
            result = (JsonValue) value;
        } else {
            result = ((JsonValueGenerator) value).generateJsonValue();
        }
        return result;
    }
}
