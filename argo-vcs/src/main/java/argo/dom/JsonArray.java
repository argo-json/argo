package argo.dom;

import java.util.ArrayList;
import java.util.List;

public final class JsonArray implements JsonValue {

    private final List<JsonValue> elements;

    public JsonArray(final List<JsonValue> elements) {
        this.elements = new ArrayList<JsonValue>(elements);
    }

    public List<JsonValue> getElements() {
        return new ArrayList<JsonValue>(elements);
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
