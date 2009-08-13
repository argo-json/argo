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

import static argo.jdom.JsonNodeFactory.*;
import static argo.jdom.JsonNodeSelector.*;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.StringReader;
import java.util.*;

public final class JsonNodeSelectorTest {

    @Test
    public void matchesABooleanNode() throws Exception {
        final JsonNodeSelector<JsonNode, Boolean> jsonNodeSelector = JsonNodeSelector.aBooleanNode();
        assertTrue(jsonNodeSelector.matches(aJsonTrue()));
        assertTrue(jsonNodeSelector.matches(aJsonFalse()));
        assertThat(jsonNodeSelector.getValue(aJsonTrue()), equalTo(Boolean.TRUE));
        assertThat(jsonNodeSelector.getValue(aJsonFalse()), equalTo(Boolean.FALSE));
    }

    @Test
    public void matchesAStringNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelector.aStringNode();
        final JsonStringNode node = aJsonString("hello");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("hello"));
    }

    @Test
    public void matchesANumberNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = aNumberNode();
        final JsonNode node = aJsonNumber("12.1");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("12.1"));
    }

    @Test
    public void matchesANullNode() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = aNullNode();
        final JsonNode node = aJsonNull();
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(node));
    }

    @Test
    public void matchesAnObjectNode() throws Exception {
        final JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> jsonNodeSelector = anObjectNode();
        final JsonRootNode node = aJsonObject(new HashMap<JsonStringNode, JsonNode>());
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((Map<JsonStringNode, JsonNode>)new HashMap<JsonStringNode, JsonNode>()));
    }

    @Test
    public void matchesAFieldOfAnObjectNode() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = aField("Wobbly");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode)aJsonString("Bob")));
    }

    @Test
    public void matchesAnObjectWithField() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = anObjectNodeWithField("Wobbly");
        final JsonNode node = aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }});
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode)aJsonString("Bob")));
    }

    @Test
    public void rejectsAFieldOfAnObjectNodeThatDoesNotExist() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertFalse(jsonNodeSelector.matches(node));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotGetAFieldOfAnObjectNodeThatDoesNotExist() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode)aJsonString("Bob")));
    }

    @Test
    public void matchesAnArrayNode() throws Exception {
        final JsonNodeSelector<JsonNode, List<JsonNode>> jsonNodeSelector = anArrayNode();
        final JsonRootNode node = aJsonArray(new LinkedList<JsonNode>());
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((List<JsonNode>)new LinkedList<JsonNode>()));
    }

    @Test
    public void matchesAnElementOfAnArrayNode() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = anElement(0);
        final List<JsonNode> node = Arrays.asList((JsonNode) aJsonString("hello"));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode)aJsonString("hello")));
    }

    @Test
    public void matchesAnArrayWithElement() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = anArrayWithElement(0);
        final JsonNode node = aJsonArray(Arrays.asList((JsonNode) aJsonString("hello")));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode)aJsonString("hello")));
    }

    @Test
    public void rejectsAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = anElement(0);
        assertFalse(jsonNodeSelector.matches(new LinkedList<JsonNode>()));
    }

    @Test (expected = IllegalArgumentException.class)
    public void doesNotGetValueOfAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = anElement(0);
        jsonNodeSelector.getValue(new LinkedList<JsonNode>());
    }

    @Test
    public void chainingUsingWithChildMatches() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = anObjectNode()
                .with(aField("Hello"));
        final JsonRootNode node = aJsonObject(
                new HashMap<JsonStringNode, JsonNode>() {{
                    put(aJsonString("Hello"), aJsonNumber("12.5"));
                }}
        );
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(aJsonNumber("12.5")));
    }

    @Test
    public void javadocExampleWorks() throws Exception {
        final String json = "{\"Fee\":{\"fi\":\"fo\"}}";
        final JsonNode jsonNode = new JdomParser().parse(new StringReader(json));
        final String result = anObjectNodeWithField("Fee")
                .with(anObjectNodeWithField("fi"))
                .with(aStringNode())
                .getValue(jsonNode);
        assertThat(result, equalTo("fo"));
    }
}
