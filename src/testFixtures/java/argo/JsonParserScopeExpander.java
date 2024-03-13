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
import argo.saj.JsonListener;

import java.io.IOException;
import java.util.Iterator;

public final class JsonParserScopeExpander {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private JsonParserScopeExpander() {
    }

    public static JsonNode parse(final ScopeExpandedParseExecutor scopeExpandedParseExecutor) throws InvalidSyntaxException, IOException {
        return JSON_PARSER.parse(scopeExpandedParseExecutor::parseUsing);
    }

    public static void parse(final JsonListener jsonListener, final Iterator<JsonStreamElement> stajParser) throws InvalidSyntaxException, IOException {
        JSON_PARSER.parseStreaming(stajParser, jsonListener);
    }

    @FunctionalInterface
    public interface ScopeExpandedParseExecutor {
        void parseUsing(JsonListener jsonListener) throws InvalidSyntaxException, IOException;
    }

}
