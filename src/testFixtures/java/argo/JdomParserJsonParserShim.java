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

import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Reader;

interface JdomParserJsonParserShim {
    JsonNode parse(Reader reader) throws IOException, InvalidSyntaxException;

    JsonNode parse(String json) throws InvalidSyntaxException;

    @SuppressWarnings("deprecation")
    final class Jdom implements JdomParserJsonParserShim {
        private final argo.jdom.JdomParser jdomParser;

        Jdom(final argo.jdom.JdomParser jdomParser) {
            this.jdomParser = jdomParser;
        }

        @Override
        public JsonNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
            try {
                return jdomParser.parse(reader);
            } catch (final argo.saj.InvalidSyntaxException e) {
                throw new InvalidSyntaxException(e.getMessage(), e.getCause(), e.getLine(), e.getColumn());
            }
        }

        @Override
        public JsonNode parse(final String json) throws InvalidSyntaxException {
            try {
                return jdomParser.parse(json);
            } catch (final argo.saj.InvalidSyntaxException e) {
                throw new InvalidSyntaxException(e.getMessage(), e.getCause(), e.getLine(), e.getColumn());
            }
        }
    }

    final class Json implements JdomParserJsonParserShim {
        private final JsonParser jsonParser;

        Json(final JsonParser jsonParser) {
            this.jsonParser = jsonParser;
        }

        @Override
        public JsonNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
            return jsonParser.parse(reader);
        }

        @Override
        public JsonNode parse(final String json) throws InvalidSyntaxException {
            return jsonParser.parse(json);
        }
    }
}
