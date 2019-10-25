/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.*;

final class JsonNodeSelectorsTest {

    private static final JsonNode SAMPLE_JSON = object(
            field("name", string("Rossi")),
            field("championships", array(
                    number("2002"),
                    number("2003"),
                    number("2004"),
                    number("2005"),
                    number("2008"),
                    number("2009"))
            ),
            field("retirement age", nullNode())
    );

    @Test
    void matchesABooleanNode() {
        final JsonNodeSelector<JsonNode, Boolean> jsonNodeSelector = JsonNodeSelectors.aBooleanNode();
        assertTrue(jsonNodeSelector.matches(trueNode()));
        assertTrue(jsonNodeSelector.matches(falseNode()));
        assertThat(jsonNodeSelector.getValue(trueNode()), equalTo(Boolean.TRUE));
        assertThat(jsonNodeSelector.getValue(falseNode()), equalTo(Boolean.FALSE));
    }

    @Test
    void matchesANullBooleanNode() {
        final JsonNodeSelector<JsonNode, Boolean> jsonNodeSelector = JsonNodeSelectors.aNullableBooleanNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    void matchesAStringNode() {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aStringNode();
        final JsonStringNode node = string("hello");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("hello"));
    }

    @Test
    void matchesANullStringNode() {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNullableStringNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    void matchesANumberNode() {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNumberNode();
        final JsonNode node = number("12.1");
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo("12.1"));
    }

    @Test
    void matchesANullNumberNode() {
        final JsonNodeSelector<JsonNode, String> jsonNodeSelector = JsonNodeSelectors.aNullableNumberNode("retirement age");
        assertTrue(jsonNodeSelector.matches(SAMPLE_JSON));
        assertThat(jsonNodeSelector.getValue(SAMPLE_JSON), is(nullValue()));
    }

    @Test
    void matchesANullNode() {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.aNullNode();
        final JsonNode node = nullNode();
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(node));
    }

    @Test
    void matchesAnObjectNode() {
        final JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> jsonNodeSelector = JsonNodeSelectors.anObjectNode();
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Barry"), string("Lemons"));
        }};
        final JsonNode node = object(someJsonMappings);
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonMappings));
    }

    @Test
    void matchesANullableObjectNode() {
        final JsonNodeSelector<JsonNode, Map<JsonStringNode, JsonNode>> jsonNodeSelector = JsonNodeSelectors.aNullableObjectNode();
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Barry"), string("Lemons"));
        }};
        final JsonNode node = object(someJsonMappings);
        assertTrue(jsonNodeSelector.matches(node));
        assertTrue(jsonNodeSelector.matches(nullNode()));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonMappings));
        assertThat(jsonNodeSelector.getValue(nullNode()), is(nullValue()));
    }

    @Test
    void matchesAFieldOfAnObjectNode() {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Wobbly");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Wobbly"), string("Bob"));
        }};
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) string("Bob")));
    }

    @Test
    void matchesAnObjectWithField() {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.anObjectNodeWithField("Wobbly");
        final JsonNode node = object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Wobbly"), string("Bob"));
        }});
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) string("Bob")));
    }

    @Test
    void rejectsAFieldOfAnObjectNodeThatDoesNotExist() {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Wobbly"), string("Bob"));
        }};
        assertFalse(jsonNodeSelector.matches(node));
    }

    @Test
    void doesNotGetAFieldOfAnObjectNodeThatDoesNotExist() {
        final JsonNodeSelector<Map<JsonStringNode, JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.aField("Golden");
        final Map<JsonStringNode, JsonNode> node = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Wobbly"), string("Bob"));
        }};
        assertThrows(IllegalArgumentException.class, () -> jsonNodeSelector.getValue(node));
    }

    @Test
    void matchesAnArrayNode() {
        final JsonNodeSelector<JsonNode, List<JsonNode>> jsonNodeSelector = JsonNodeSelectors.anArrayNode();
        final List<JsonNode> someJsonNodes = singletonList(number("12"));
        final JsonNode node = array(someJsonNodes);
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonNodes));
    }

    @Test
    void matchesANullableArrayNode() {
        final JsonNodeSelector<JsonNode, List<JsonNode>> jsonNodeSelector = JsonNodeSelectors.aNullableArrayNode();
        final List<JsonNode> someJsonNodes = singletonList(number("12"));
        final JsonNode node = array(someJsonNodes);
        assertTrue(jsonNodeSelector.matches(node));
        assertTrue(jsonNodeSelector.matches(nullNode()));
        assertThat(jsonNodeSelector.getValue(node), equalTo(someJsonNodes));
        assertThat(jsonNodeSelector.getValue(nullNode()), is(nullValue()));
    }

    @Test
    void matchesAnElementOfAnArrayNode() {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        final List<JsonNode> node = singletonList(string("hello"));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) string("hello")));
    }

    @Test
    void matchesAnArrayWithElement() {
        final JsonNodeSelector<JsonNode, JsonNode> jsonNodeSelector = JsonNodeSelectors.anArrayNodeWithElement(0);
        final JsonNode node = array(singletonList((JsonNode) string("hello")));
        assertTrue(jsonNodeSelector.matches(node));
        assertThat(jsonNodeSelector.getValue(node), equalTo((JsonNode) string("hello")));
    }

    @Test
    void rejectsAnElementOfAnArrayNodeGreaterThanArraySize() {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        assertFalse(jsonNodeSelector.matches(new LinkedList<>()));
    }

    @Test
    void doesNotGetValueOfAnElementOfAnArrayNodeGreaterThanArraySize() {
        final JsonNodeSelector<List<JsonNode>, JsonNode> jsonNodeSelector = JsonNodeSelectors.anElement(0);
        assertThrows(IllegalArgumentException.class, () -> jsonNodeSelector.getValue(new LinkedList<>()));
    }

    @Test
    void shorthandChainedStringSelectorGetsAValue() {
        final JsonNodeSelector<JsonNode, String> selector = JsonNodeSelectors.aStringNode("name");
        assertTrue(selector.matches(SAMPLE_JSON));
        assertThat(selector.getValue(SAMPLE_JSON), equalTo("Rossi"));
    }

    @Test
    void shorthandChainedNumberSelectorGetsAValue() {
        final JsonNodeSelector<JsonNode, String> selector = JsonNodeSelectors.aNumberNode("championships", 3);
        assertTrue(selector.matches(SAMPLE_JSON));
        assertThat(selector.getValue(SAMPLE_JSON), equalTo("2005"));
    }

    @Test
    void javadocExampleWorks() throws Exception {
        final String json = "{\"Fee\":{\"fi\":\"fo\"}}";
        final JsonNode jsonNode = new JdomParser().parse(new StringReader(json));
        final String result = JsonNodeSelectors.anObjectNodeWithField("Fee")
                .with(JsonNodeSelectors.anObjectNodeWithField("fi"))
                .with(JsonNodeSelectors.aStringNode())
                .getValue(jsonNode);
        assertThat(result, equalTo("fo"));
    }

}
