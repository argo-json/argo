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

interface SajParserJsonParserShim {
    void parse(Reader reader, JsonListener jsonListener) throws IOException, InvalidSyntaxException;

    void parse(String json, JsonListener jsonListener) throws IOException, InvalidSyntaxException;

    final class Saj implements SajParserJsonParserShim {
        private final SajParser sajParser;

        public Saj(final SajParser sajParser) {
            this.sajParser = sajParser;
        }

        @Override
        public void parse(final Reader reader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
            sajParser.parse(reader, jsonListener);
        }

        @Override
        public void parse(final String json, JsonListener jsonListener) throws IOException, InvalidSyntaxException {
            sajParser.parse(json, jsonListener);
        }
    }

    final class Json implements SajParserJsonParserShim {
        private final JsonParser jsonParser;

        public Json(final JsonParser jsonParser) {
            this.jsonParser = jsonParser;
        }

        @Override
        public void parse(final Reader reader, JsonListener jsonListener) throws IOException, InvalidSyntaxException {
            jsonParser.parseStreaming(reader, jsonListener);
        }

        @Override
        public void parse(final String json, JsonListener jsonListener) throws IOException, InvalidSyntaxException {
            jsonParser.parseStreaming(json, jsonListener);
        }
    }
}
