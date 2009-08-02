package argo.dom;

import java.util.HashMap;
import java.util.Map;

public final class JsonObject implements JsonValue {

    private final Map<JsonString, JsonValue> fields;

    public JsonObject(final Map<JsonString, JsonValue> fields) {
        this.fields = new HashMap<JsonString, JsonValue>(fields);
    }

    public Map<JsonString, JsonValue> getFields() {
        return new HashMap<JsonString, JsonValue>(fields);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonObject thatJsonObject = (JsonObject) that;
        return this.fields.equals(thatJsonObject.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonObject fields:[")
                .append(fields)
                .append("]")
                .toString();
    }
}
