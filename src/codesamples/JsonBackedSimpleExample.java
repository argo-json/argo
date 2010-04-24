/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeSelector;
import argo.jdom.JsonRootNode;

import java.util.AbstractList;
import java.util.List;

import static argo.jdom.JsonNodeSelectors.*;

public final class JsonBackedSimpleExample implements SimpleExample {

    private static final JsonNodeSelector<JsonNode, String> NAME_SELECTOR = aStringNode("name");
    private static final JsonNodeSelector<JsonNode, String> SALES_SELECTOR = aNumberNode("sales");
    private static final JsonNodeSelector<JsonNode, List<JsonNode>> SINGLES_SELECTOR = anArrayNode("singles");
    private static final JsonNodeSelector<JsonNode, String> SINGLE_NAME_SELECTOR = aStringNode();

    private final JsonRootNode json;

    public JsonBackedSimpleExample(final JsonRootNode json) {
        this.json = json;
    }

    public String getName() {
        return NAME_SELECTOR.getValue(json);
    }

    public int getSales() {
        return Integer.valueOf(SALES_SELECTOR.getValue(json));
    }

    public List<String> getSingles() {
        return new AbstractList<String>() {
            public String get(final int index) {
                return SINGLE_NAME_SELECTOR.getValue(SINGLES_SELECTOR.getValue(json).get(index));
            }

            public int size() {
                return SINGLES_SELECTOR.getValue(json).size();
            }
        };
    }
}
