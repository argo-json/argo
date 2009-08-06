package argo.jax;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeType;
import argo.jdom.JsonString;

import java.util.List;
import java.util.Map;

/**
 * Test JsonValue that determines equality based on equality of an int field provided in the constructor.  Immutable.
 */
final class MockJsonNode implements JsonNode {

    private final int equalityValue;

    MockJsonNode(final int equalityValue) {
        this.equalityValue = equalityValue;
    }

    public JsonNodeType getType() {
        return null;
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

    public Map<JsonString, JsonNode> getFields() {
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
        final MockJsonNode thatMockJsonValue = (MockJsonNode) that;
        return equalityValue == thatMockJsonValue.equalityValue;
    }

    @Override
    public int hashCode() {
        return equalityValue;
    }
}
