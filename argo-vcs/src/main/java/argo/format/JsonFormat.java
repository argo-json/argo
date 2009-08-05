package argo.format;

import argo.jdom.JsonRootNode;

import java.io.IOException;
import java.io.Writer;

public interface JsonFormat {

    String format(JsonRootNode jsonRootNode) throws IOException;
    void format(JsonRootNode jsonRootNode, Writer writer) throws IOException;

}
