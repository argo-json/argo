/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.List;
import java.util.Map;

/**
 * Factory for <code>JsonNode</code>s.
 */
public final class JsonNodeFactory {

    public static JsonNode aNull() {
        return JsonConstants.NULL;
    }

    public static JsonNode aTrue() {
        return JsonConstants.TRUE;
    }

    public static JsonNode aFalse() {
        return JsonConstants.FALSE;
    }

    public static JsonStringNode aJsonString(final String value) {
        return new JsonStringNode(value);
    }

    public static JsonNode aJsonNumber(final String value) {
        return new JsonNumberNode(value);
    }

    public static JsonRootNode aJsonArray(final List<JsonNode> elements) {
        return new JsonArray(elements);
    }

    public static JsonRootNode aJsonObject(final Map<JsonStringNode, JsonNode> fields) {
        return new JsonObject(fields);
    }

}
