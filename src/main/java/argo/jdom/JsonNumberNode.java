package argo.jdom;

import static argo.jdom.JsonNodeType.NUMBER;

import java.util.List;
import java.util.Map;

final class JsonNumberNode extends JsonNode {

    private final String value;

    JsonNumberNode(final String value) {
        if (value == null) {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        this.value = value;
    }

    public JsonNodeType getType() {
        return NUMBER;
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

    public Map<JsonStringNode, JsonNode> getFields() {
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

        final JsonNumberNode thatJsonNumberNode = (JsonNumberNode) that;
        return this.value.equals(thatJsonNumberNode.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonNumberNode value:[")
                .append(value)
                .append("]")
                .toString();
    }
}
