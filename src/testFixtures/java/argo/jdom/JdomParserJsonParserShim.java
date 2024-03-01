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

interface JdomParserJsonParserShim {
    JsonNode parse(Reader reader) throws IOException, InvalidSyntaxException;

    JsonNode parse(String json) throws IOException, InvalidSyntaxException;

    final class Jdom implements JdomParserJsonParserShim {
        private final JdomParser jdomParser;

        public Jdom(final JdomParser jdomParser) {
            this.jdomParser = jdomParser;
        }

        @Override
        public JsonNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
            return jdomParser.parse(reader);
        }

        @Override
        public JsonNode parse(final String json) throws IOException, InvalidSyntaxException {
            return jdomParser.parse(json);
        }
    }

    final class Json implements JdomParserJsonParserShim {
        private final JsonParser jsonParser;

        public Json(final JsonParser jsonParser) {
            this.jsonParser = jsonParser;
        }

        @Override
        public JsonNode parse(final Reader reader) throws IOException, InvalidSyntaxException {
            return jsonParser.parse(reader);
        }

        @Override
        public JsonNode parse(final String json) throws IOException, InvalidSyntaxException {
            return jsonParser.parse(json);
        }
    }
}
