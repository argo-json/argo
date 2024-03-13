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

import argo.staj.StajParser;

import java.io.Reader;
import java.util.Iterator;

interface StajParserJsonParserShim {
    Iterator<JsonStreamElement> parse(Reader reader);

    Iterator<JsonStreamElement> parse(String json);

    final class Staj implements StajParserJsonParserShim {

        @Override
        public Iterator<JsonStreamElement> parse(final Reader reader) {
            return new StajParser(reader);
        }

        @Override
        public Iterator<JsonStreamElement> parse(final String json) {
            return new StajParser(json);
        }
    }

    final class Json implements StajParserJsonParserShim {
        private final JsonParser jsonParser;

        Json(final JsonParser jsonParser) {
            this.jsonParser = jsonParser;
        }

        @Override
        public Iterator<JsonStreamElement> parse(final Reader reader) {
            return jsonParser.parseStreaming(reader);
        }

        @Override
        public Iterator<JsonStreamElement> parse(final String json) {
            return jsonParser.parseStreaming(json);
        }
    }
}
