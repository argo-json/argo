package argo.jdom;

import java.util.List;
import java.util.Map;

enum JsonConstants implements JsonNode {

    NULL(JsonNodeType.NULL), TRUE(JsonNodeType.TRUE), FALSE(JsonNodeType.FALSE);

    private final JsonNodeType jsonNodeType;

    JsonConstants(JsonNodeType jsonNodeType) {
        this.jsonNodeType = jsonNodeType;
    }

    public JsonNodeType getType() {
        return jsonNodeType;
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

    public Map<JsonNode, JsonNode> getFields() {
        throw new RuntimeException("Attempt to get fields on a JsonNode without fields.");
    }

    public boolean hasElements() {
        return false;
    }

    public List<JsonNode> getElements() {
        throw new RuntimeException("Attempt to get elements on a JsonNode without elements.");
    }

}