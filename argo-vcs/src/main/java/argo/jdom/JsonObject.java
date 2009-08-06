package argo.jdom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonObject implements JsonRootNode {

    private final Map<JsonString, JsonNode> fields;

    public JsonObject(final Map<JsonString, JsonNode> fields) {
        this.fields = new HashMap<JsonString, JsonNode>(fields);
    }

    public Map<JsonString, JsonNode> getFields() {
        return new HashMap<JsonString, JsonNode>(fields);
    }

    public JsonNodeType getType() {
        return JsonNodeType.OBJECT;
    }

    public boolean hasText() {
        return false;
    }

    public String getText() {
        throw new RuntimeException("Attempt to get text on a JsonNode without text.");
    }

    public boolean hasFields() {
        return true;
    }

    public boolean hasElements() {
        return false;
    }

    public List<JsonNode> getElements() {
        throw new RuntimeException("Attempt to get elements on a JsonNode without elements.");
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
