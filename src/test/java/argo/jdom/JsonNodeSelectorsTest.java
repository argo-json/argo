/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public final class JsonNodeSelectorsTest {

    private static final JsonRootNode SAMPLE_JSON = aJsonObject(
            aJsonField("name", aJsonString("Rossi"))
            , aJsonField("championships", aJsonArray(
                    aJsonNumber("2002")
                    , aJsonNumber("2003")
                    , aJsonNumber("2004")
                    , aJsonNumber("2005")
                    , aJsonNumber("2008")
                    , aJsonNumber("2009")
            ))
            ,aJsonField("retirement age", aJsonNull())
    );

    @Test
    public void matchesABooleanNode() throws Exception {
        final JsonNodeSelector<JsonNode, Boolean> jsonNodeSelector = JsonNodeSelectors.aBooleanNode();
        assertTrue(jsonNodeSelector.matches(aJsonTrue()));
        assertTrue(jsonNodeSelector.matches(aJsonFalse()));
        assertThat(jsonNodeSelector.getValue(aJsonTrue()), equalTo(Boolean.TRUE));
        assertThat(jsonNodeSelector.getValue(aJsonFalse()), equalTo(Boolean.FALSE));
    }

    @Test
    public void matchesANullBooleanNode() throws Exception {
        final JsonNodeSelector<JsonNode, Boolean> jsonNodeSelector = JsonNodeSelectors.aNullableBooleanNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    public void matchesAStringNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aStringNode();
        final JsonStringNode node = aJsonString("hello");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("hello"));
    }

    @Test
    public void matchesANullStringNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNullableStringNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    public void matchesANumberNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNumberNode();
        final JsonNode node = aJsonNumber("12.1");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("12.1"));
    }

    @Test
    public void matchesANullNumberNode() throws Exception {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNullableNumberNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    public void matchesANullNode() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.aNullNode();
        final JsonNode node = aJsonNull();
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(node));
    }

    @Test
    public void matchesAnObjectNode() throws Exception {
        final JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> jsonNodeSelector = JsonNodeSelectors.anObjectNode();
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Barry"), aJsonString("Lemons"));
        }};
        final JsonNode node = aJsonObject(someJsonMappings);
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonMappings));
    }

    @Test
    public void matchesANullableObjectNode() throws Exception {
        final JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> jsonNodeSelector = JsonNodeSelectors.aNullableObjectNode();
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Barry"), aJsonString("Lemons"));
        }};
        final JsonNode node = aJsonObject(someJsonMappings);
        assertTrue(jsonNodeSelector.matches(node));
        assertTrue(jsonNodeSelector.matches(aJsonNull()));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonMappings));
        assertThat(jsonNodeSelector.getValue(aJsonNull()), is(nullValue()));
    }

    @Test
    public void matchesAFieldOfAnObjectNode() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Wobbly");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) aJsonString("Bob")));
    }

    @Test
    public void matchesAnObjectWithField() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.anObjectNodeWithField("Wobbly");
        final JsonNode node = aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }});
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) aJsonString("Bob")));
    }

    @Test
    public void rejectsAFieldOfAnObjectNodeThatDoesNotExist() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertFalse(jsonNodeSelector.matches(node));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotGetAFieldOfAnObjectNodeThatDoesNotExist() throws Exception {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Wobbly"), aJsonString("Bob"));
        }};
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) aJsonString("Bob")));
    }

    @Test
    public void matchesAnArrayNode() throws Exception {
        final JsonNodeSelector<JsonNode, List<JsonNode>> jsonNodeSelector = JsonNodeSelectors.anArrayNode();
        final List<JsonNode> someJsonNodes = asList(aJsonNumber("12"));
        final JsonRootNode node = aJsonArray(someJsonNodes);
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonNodes));
    }

    @Test
    public void matchesANullableArrayNode() throws Exception {
        final JsonNodeSelector<JsonNode, List<JsonNode>> jsonNodeSelector = JsonNodeSelectors.aNullableArrayNode();
        final List<JsonNode> someJsonNodes = asList(aJsonNumber("12"));
        final JsonRootNode node = aJsonArray(someJsonNodes);
        assertTrue(jsonNodeSelector.matches(node));
        assertTrue(jsonNodeSelector.matches(aJsonNull()));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonNodes));
        assertThat(jsonNodeSelector.getValue(aJsonNull()), is(nullValue()));
    }

    @Test
    public void matchesAnElementOfAnArrayNode() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        final List<JsonNode> node = asList((JsonNode) aJsonString("hello"));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) aJsonString("hello")));
    }

    @Test
    public void matchesAnArrayWithElement() throws Exception {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.anArrayNodeWithElement(0);
        final JsonNode node = aJsonArray(asList((JsonNode) aJsonString("hello")));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) aJsonString("hello")));
    }

    @Test
    public void rejectsAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        assertFalse(jsonNodeSelector.matches(new LinkedList<JsonNode>()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotGetValueOfAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        jsonNodeSelector.getValue(new LinkedList<JsonNode>());
    }

    @Test
    public void shorthandChainedStringSelectorGetsAValue() throws Exception {
        final JsonNodeSelector<JsonNode, String> selector = JsonNodeSelectors.aStringNode("name");
        assertTrue(selector.matches(SAMPLE_JSON));
        assertThat(selector.getValue(SAMPLE_JSON), equalTo("Rossi"));
    }

    @Test
    public void shorthandChainedNumberSelectorGetsAValue() throws Exception {
        final JsonNodeSelector<JsonNode, String> selector = JsonNodeSelectors.aNumberNode("championships", 3);
        assertTrue(selector.matches(SAMPLE_JSON));
        assertThat(selector.getValue(SAMPLE_JSON), equalTo("2005"));
    }

    @Test
    public void javadocExampleWorks() throws Exception {
        final String json = "{\"Fee\":{\"fi\":\"fo\"}}";
        final JsonNode jsonNode = new JdomParser().parse(new StringReader(json));
        final String result = JsonNodeSelectors.anObjectNodeWithField("Fee")
                .with(JsonNodeSelectors.anObjectNodeWithField("fi"))
                .with(JsonNodeSelectors.aStringNode())
                .getValue(jsonNode);
        assertThat(result, equalTo("fo"));
    }

}
