package argo.jdom;

import java.util.List;
import java.util.Map;

/**
 * Factory for <code>JsonNode</code>s.
 */
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

    public static JsonStringNode aJsonString(final String value) {
        return new JsonStringNode(value);
    }

    public static JsonNode aJsonNumber(final String value) {
        return new JsonNumberNode(value);
    }

    public static JsonRootNode aJsonArray(final List<JsonNode> elements) {
        return new JsonArray(elements);
    }

    public static JsonRootNode aJsonObject(final Map<JsonStringNode, JsonNode> fields) {
        return new JsonObject(fields);
    }

}
