package argo.format;

import argo.jdom.JsonRootNode;

public class PrettyJsonBuilder {
    public static String json(final JsonRootNode jsonRootNode) {
        return new PrettyJsonFormatter().format(jsonRootNode);
    }
}
