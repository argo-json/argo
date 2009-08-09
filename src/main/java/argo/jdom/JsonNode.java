package argo.jdom;

import java.util.List;
import java.util.Map;

/**
 * An node (leaf or otherwise) in a JSON document.
 */
public abstract class JsonNode {

    // Only extendable by classes in this package
    JsonNode() {
    }

    public abstract JsonNodeType getType();

    public abstract boolean hasText();
    public abstract String getText();

    public abstract boolean hasFields();
    public abstract Map<JsonNode, JsonNode> getFields();
    
    public abstract boolean hasElements();
    public abstract List<JsonNode> getElements();

}
