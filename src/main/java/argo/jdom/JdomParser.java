package argo.jdom;

import argo.saj.InvalidSyntaxException;
import argo.saj.SajParser;

import java.io.IOException;
import java.io.Reader;

/**
 * Parses a JSON character stream into a <code>JsonRootNode</code> object.
 */
public final class JdomParser {

    /**
     * Parse the character stream from the specified <code>Reader</code> into a <code>JsonRootNode</code> object.
     *
     * @param reader the <code>Reader</code> to parse.
     * @return a <code>JsonRootNode</code> representing the JSON read from the specified <code>Reader</code>.
     * @throws IOException bubbled up from exceptions thrown reading from the specified <code>Reader</code>.
     * @throws InvalidSyntaxException if the characters streamed from the specified <code>Reader</code> does not represent valid JSON.
     */
    public JsonRootNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        new SajParser().parse(reader, jsonListenerToJdomAdapter);
        return jsonListenerToJdomAdapter.getDocument();
    }
}
