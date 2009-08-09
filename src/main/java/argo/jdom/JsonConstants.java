package argo.jdom;

import java.util.List;
import java.util.Map;

final class JsonConstants extends JsonNode {

    static final JsonConstants NULL = new JsonConstants(JsonNodeType.NULL);
    static final JsonConstants TRUE = new JsonConstants(JsonNodeType.TRUE);
    static final JsonConstants FALSE = new JsonConstants(JsonNodeType.FALSE);

    private final JsonNodeType jsonNodeType;

    private JsonConstants(JsonNodeType jsonNodeType) {
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
