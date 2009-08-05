package argo.format;

import argo.jdom.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public final class CompactJsonFormat implements JsonFormat {

    public String format(final JsonRootNode jsonNode) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        format(jsonNode, stringWriter);
        return stringWriter.toString();
    }

    public void format(final JsonRootNode jsonNode, final Writer writer) throws IOException {
        formatJsonNode(jsonNode, writer);
    }

    private void formatJsonNode(final JsonNode jsonNode, final Writer writer) throws IOException {
        if (jsonNode.hasElements()) {
            writer.append('[');
            boolean first = true;
            for (final JsonNode node : jsonNode.getElements()) {
                if (!first) {
                    writer.append(',');
                }
                first = false;
                formatJsonNode(node, writer);
            }
            writer.append(']');
        }
        if (jsonNode.hasFields()) {
            writer.append('{');
            boolean first = true;
            for (final Map.Entry<JsonString, JsonNode> field : jsonNode.getFields().entrySet()) {
                if (!first) {
                    writer.append(',');
                }
                first = false;
                formatJsonNode(field.getKey(), writer);
                writer.append(':');
                formatJsonNode(field.getValue(), writer);
            }
            writer.append('}');
        }
        if (jsonNode.hasText()) {
            if (jsonNode instanceof JsonNumber) {
                writer.append(jsonNode.getText());
            } else if (jsonNode instanceof JsonString) {
                writer.append('"');
                writer.append(jsonNode.getText());
                writer.append('"');
            }
        }
        if (jsonNode == JsonConstants.FALSE) {
            writer.append("false");
        }
        if (jsonNode == JsonConstants.TRUE) {
            writer.append("true");
        }
        if (jsonNode == JsonConstants.NULL) {
            writer.append("null");
        }
    }
}
