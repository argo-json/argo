package argo.jdom;

import java.util.List;
import java.util.Map;

final class JsonTextNode extends JsonNode {

    private final String value;
    private final JsonNodeType jsonNodeType;

    JsonTextNode(final String value, final JsonNodeType jsonNodeType) {
        if (value == null) {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        this.value = value;
        this.jsonNodeType = jsonNodeType;
    }

    public JsonNodeType getType() {
        return jsonNodeType;
    }

    public boolean hasText() {
        return true;
    }

    public String getText() {
        return value;
    }

    public boolean hasFields() {
        return false;
    }

    public Map<JsonNode, JsonNode> getFields() {
        throw new RuntimeException("Attempt to get fields on a JsonNode without fields.");
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

        final JsonTextNode thatJsonTextNode = (JsonTextNode) that;
        return this.jsonNodeType == thatJsonTextNode.jsonNodeType && this.value.equals(thatJsonTextNode.value);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + jsonNodeType.hashCode();
        result = 37 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonTextNode jsonNodeType:[")
                .append(jsonNodeType)
                .append("], value:[")
                .append(value)
                .append("]")
                .toString();
    }
}
