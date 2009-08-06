package argo.jdom;

import static argo.jdom.JsonNodeType.NUMBER;
import static argo.jdom.JsonNodeType.STRING;

import java.util.List;
import java.util.Map;

public final class JsonNodeFactory {

    public static JsonNode aNull() {
        return JsonConstants.NULL;
    }

    public static JsonNode aTrue() {
        return JsonConstants.TRUE;
    }

    public static JsonNode aFalse() {
        return JsonConstants.FALSE;
    }

    public static JsonNode aJsonString(final String value) {
        return new JsonTextNode(value, STRING);
    }

    public static JsonNode aJsonNumber(final String value) {
        return new JsonTextNode(value, NUMBER);
    }

    public static JsonRootNode aJsonArray(final List<JsonNode> elements) {
        return new JsonArray(elements);
    }

    public static JsonRootNode aJsonObject(final Map<String, JsonNode> fields) {
        return new JsonObject(fields);
    }

}
