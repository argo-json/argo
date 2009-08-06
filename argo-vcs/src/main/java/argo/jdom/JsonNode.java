package argo.jdom;

import java.util.List;
import java.util.Map;

/**
 * An node (leaf or otherwise) in a json document.
 */
public interface JsonNode {
    JsonNodeType getType();

    boolean hasText();
    String getText();

    boolean hasFields();
    Map<JsonNode, JsonNode> getFields();
    
    boolean hasElements();
    List<JsonNode> getElements();

}
