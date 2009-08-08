package argo.jdom;

import argo.saj.InvalidSyntaxException;
import argo.saj.SajParser;

import java.io.IOException;
import java.io.Reader;

public final class JdomParser {
    public JsonRootNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        new SajParser().parse(reader, jsonListenerToJdomAdapter);
        return jsonListenerToJdomAdapter.getDocument();
    }
}
