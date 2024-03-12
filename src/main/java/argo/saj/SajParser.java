/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import argo.JsonParser;

import java.io.IOException;
import java.io.Reader;

/**
 * Converts a character stream into calls to a {@code JsonListener}.
 * <p>
 * Instances of {@code SajParser} are thread-safe in that concurrent calls to {@code parse} are safe, provided
 * each call is made with a different {@code Reader} and a different {@code JsonListener}.
 *
 * @see JsonListener
 */
public final class SajParser {

    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * Parses the given JSON {@code String} into calls to the given JsonListener.
     *
     * @param json         the {@code String} to parse.
     * @param jsonListener the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException thrown to indicate the characters read from {@code in} did not constitute valid JSON.
     */
    public void parse(final String json, final JsonListener jsonListener) throws InvalidSyntaxException {
        JSON_PARSER.parseStreaming(json, jsonListener);
    }

    /**
     * Parses the given character stream into calls to the given JsonListener.
     *
     * @param in           the character stream to parse
     * @param jsonListener the JsonListener to notify of parsing events
     * @throws InvalidSyntaxException thrown to indicate the characters read from {@code in} did not constitute valid JSON.
     * @throws IOException rethrown when reading characters from {@code in} throws {@code IOException}.
     */
    public void parse(final Reader in, final JsonListener jsonListener) throws InvalidSyntaxException, IOException {
        JSON_PARSER.parseStreaming(in, jsonListener);
    }

}
