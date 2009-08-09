package argo.jdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JsonArray extends JsonRootNode {

    private final List<JsonNode> elements;

    JsonArray(final List<JsonNode> elements) {
        this.elements = new ArrayList<JsonNode>(elements);
    }

    public JsonNodeType getType() {
        return JsonNodeType.ARRAY;
    }

    public List<JsonNode> getElements() {
        return new ArrayList<JsonNode>(elements);
    }

    public boolean hasText() {
        return false;
    }

    public String getText() {
        throw new RuntimeException("Attempt to get text on a JsonNode without text.");
    }

    public boolean hasFields() {
        return false;
    }

    public Map<JsonStringNode, JsonNode> getFields() {
        throw new RuntimeException("Attempt to get fields on a JsonNode without fields.");
    }

    public boolean hasElements() {
        return true;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonArray thatJsonArray = (JsonArray) that;
        return this.elements.equals(thatJsonArray.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonArray elements:[")
                .append(elements)
                .append("]")
                .toString();
    }
}
