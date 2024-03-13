/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.JsonParser;
import argo.saj.InvalidSyntaxException;

import java.io.IOException;
import java.io.Reader;

/**
 * Parses a JSON character stream into a {@code JsonNode} object.  Instances of this class can safely be shared
 * between threads.
 *
 * @deprecated Replaced by {@link argo.JsonParser}.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated public final class JdomParser {

    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * Parses the specified JSON {@code String} into a {@code JsonNode} object.
     *
     * @param json the {@code String} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code String}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code String} does not represent valid JSON.
     *
     * @deprecated Replaced by {@link argo.JsonParser#parse(String)}.
     */
    public JsonNode parse(final String json) throws InvalidSyntaxException {
        try {
            return JSON_PARSER.parse(json);
        } catch (final argo.InvalidSyntaxException e) {
            throw InvalidSyntaxException.from(e);
        }
    }

    /**
     * Parses the character stream from the specified {@code Reader} into a {@code JsonNode} object.
     *
     * @param reader the {@code Reader} to parse.
     * @return a {@code JsonNode} representing the JSON read from the specified {@code Reader}.
     * @throws InvalidSyntaxException if the characters streamed from the specified {@code Reader} does not represent valid JSON.
     * @throws IOException rethrown when reading characters from {@code in} throws {@code IOException}.
     *
     * @deprecated Replaced by {@link argo.JsonParser#parse(Reader)}.
     */
    public JsonNode parse(final Reader reader) throws InvalidSyntaxException, IOException {
        try {
            return JSON_PARSER.parse(reader);
        } catch (final argo.InvalidSyntaxException e) {
            throw InvalidSyntaxException.from(e);
        }
    }
}
