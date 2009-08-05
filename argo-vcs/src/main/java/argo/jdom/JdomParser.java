package argo.jdom;

import argo.jax.InvalidSyntaxException;
import argo.jax.JaxParser;

import java.io.IOException;
import java.io.Reader;

public final class JdomParser {
    public JsonNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        new JaxParser().parse(reader, jsonListenerToJdomAdapter);
        return jsonListenerToJdomAdapter.getDocument();
    }
}
