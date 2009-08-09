package argo.jdom;

/**
 * Marker for <code>JsonNode</code>s that can be the root of a document.
 */
public abstract class JsonRootNode extends JsonNode {
    // Only extendable by classes in this package
    JsonRootNode() {
    }
}
