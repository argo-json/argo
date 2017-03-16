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

import org.junit.Test;

import java.util.HashMap;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class JsonNodeSelectorTest {

    @Test
    public void chainingUsingWithChildMatches() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.anObjectNode()
                .with(JsonNodeSelectors.aField("Hello"));
        final JsonNode node = object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Hello"), number("12.5"));
        }});
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(number("12.5")));
    }

    @Test
    public void toStringProducesSomethingIntelligible() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.anObjectNodeWithField("Hello")
                .with(JsonNodeSelectors.anArrayNodeWithElement(2))
                .with(JsonNodeSelectors.aNumberNode());
        assertThat(jsonNodeSelector.toString(), equalTo("an object, with a field called [\"Hello\"], with an array, with an element at index [2], with a value that is a number"));
    }

}
