package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

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
        boolean first = true;
        switch(jsonNode.getType()) {
            case ARRAY:
                writer.append('[');
                for (final JsonNode node : jsonNode.getElements()) {
                    if (!first) {
                        writer.append(',');
                    }
                    first = false;
                    formatJsonNode(node, writer);
                }
                writer.append(']');
                break;
            case OBJECT:
                writer.append('{');
                for (final Map.Entry<JsonNode, JsonNode> field : jsonNode.getFields().entrySet()) {
                    if (!first) {
                        writer.append(',');
                    }
                    first = false;
                    formatJsonNode(field.getKey(), writer);
                    writer.append(':');
                    formatJsonNode(field.getValue(), writer);
                }
                writer.append('}');
                break;
            case STRING:
                writer.append('"')
                        .append(jsonNode.getText())
                        .append('"');
                break;
            case NUMBER:
                writer.append(jsonNode.getText());
                break;
            case FALSE:
                writer.append("false");
                break;
            case TRUE:
                writer.append("true");
                break;
            case NULL:
                writer.append("null");
                break;
            default:
                throw new RuntimeException("Coding failure in Argo:  Attempt to format a JsonNode of unknown type [" + jsonNode.getType() + "];");
        }
    }
}
