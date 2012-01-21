/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class JsonObject extends AbstractJsonObject {

    private final Map<JsonStringNode, JsonNode> fields;

    JsonObject(final Map<JsonStringNode, JsonNode> fields) {
        this.fields = Collections.unmodifiableMap(new HashMap<JsonStringNode, JsonNode>(fields));
    }

    @Override
    public Map<JsonStringNode, JsonNode> getFields() {
        return fields;
    }

}
