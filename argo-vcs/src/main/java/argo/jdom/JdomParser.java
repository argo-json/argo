package argo.jdom;

import argo.jax.JaxParser;
import argo.jax.JsonListenerException;

import java.io.IOException;
import java.io.Reader;

public final class JdomParser {
    public JsonValue parse(final Reader reader) throws JsonListenerException, IOException {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        new JaxParser().parse(reader, jsonListenerToJdomAdapter);
        return jsonListenerToJdomAdapter.getDocument();
    }
}
