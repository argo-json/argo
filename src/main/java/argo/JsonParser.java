/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.saj.InvalidSyntaxException;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.JsonStreamElement;
import argo.staj.StajParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

/**
 * Provides operations to parse JSON.
 * <p>
 * Instances of this class are immutable, reusable, and thread-safe.
 */
public final class JsonParser {

    /**
     * Parses the character stream from the given {@code Reader} into a {@code JsonNode} object.
     *
     * @param reader the {@code Reader} to parse.
     * @return a {@code JsonNode} representing the JSON read from the given {@code Reader}.
     * @throws InvalidSyntaxException if the characters streamed from the given {@code Reader} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public JsonNode parse(final Reader reader) throws InvalidSyntaxException, IOException {
        return new JdomParser().parse(reader);
    }

    /**
     * Parses the given JSON {@code String} into a {@code JsonNode} object.
     *
     * @param json the {@code String} to parse.
     * @return a {@code JsonNode} representing the JSON read from the given {@code String}.
     * @throws InvalidSyntaxException if the characters streamed from the given {@code String} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public JsonNode parse(final String json) throws InvalidSyntaxException, IOException { // TODO can this actually throw IOException?
        return parse(new StringReader(json));
    }

    /**
     * Parses the character stream from the given {@code Reader} into an {@code Iterator} of {@code JsonStreamElement}s.
     *
     * @param reader the {@code Reader} to parse.
     * @return a StajParser reading from the given {@code Reader}.
     */
    public Iterator<JsonStreamElement> parseStreaming(final Reader reader) {
        return new StajParser(reader);
    }

    /**
     * Parses the given JSON {@code String} into an {@code Iterator} of {@code JsonStreamElement}s.
     *
     * @param json the {@code String} to parse.
     */
    public Iterator<JsonStreamElement> parseStreaming(final String json) {
        return parseStreaming(new StringReader(json));
    }

    /**
     * Parses the character stream from the given {@code Reader} into calls to the given JsonListener.
     *
     * @param reader                  the {@code Reader} to parse.
     * @param jsonListener            the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException if the characters streamed from the given {@code Reader} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public void parseStreaming(final Reader reader, final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
        new SajParser().parse(reader, jsonListener);
    }

    /**
     * Parses the given JSON {@code String} into calls to the given JsonListener.
     *
     * @param json                    the {@code String} to parse.
     * @param jsonListener            the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException if the characters streamed from the given {@code String} do not represent valid JSON.
     * @throws IOException rethrown when reading characters from the given {@code Reader} throws {@code IOException}.
     */
    public void parseStreaming(final String json, final JsonListener jsonListener) throws InvalidSyntaxException, IOException { // TODO can this actually throw IOException?
        parseStreaming(new StringReader(json), jsonListener);
    }

}
