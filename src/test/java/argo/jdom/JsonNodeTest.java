/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.MapBuilder;
import argo.format.CompactJsonFormatter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static argo.TestingFactories.aString;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.aJsonNode;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNodeTest {

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
        assertThat(trueNode().isBooleanValue(), equalTo(true));
        assertThat(nullNode().isBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.isBooleanValue("some", "missing", "path"), equalTo(false));
        assertThat(trueNode().getBooleanValue(), equalTo(Boolean.TRUE));
    }

    @Test
    void matchesANullBooleanNode() {
        assertThat(trueNode().isNullableBooleanValue(), equalTo(true));
        assertThat(nullNode().isNullableBooleanValue(), equalTo(true));
        assertThat(number("12").isNullableBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableBooleanValue("retirement age"), is(nullValue()));
    }

    @Test
    void matchesAStringNode() {
        final JsonStringNode node = string("hello");
        assertThat(node.isStringValue(), equalTo(true));
        assertThat(trueNode().isStringValue(), equalTo(false));
        assertThat(nullNode().isStringValue(), equalTo(false));
        assertThat(node.getStringValue(), equalTo("hello"));
    }

    @Test
    void matchesANullStringNode() {
        assertThat(SAMPLE_JSON.isNullableStringValue("name"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("championships"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableStringValue("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableStringValue("name"), equalTo("Rossi"));
        assertThat(SAMPLE_JSON.getNullableStringValue("retirement age"), is(nullValue()));
    }

    @Test
    void matchesANumberNode() {
        final JsonNode node = number("12.1");
        assertThat(node.isNumberValue(), equalTo(true));
        assertThat(nullNode().isNumberValue(), equalTo(false));
        assertThat(string("Hiya!").isNumberValue(), equalTo(false));
        assertThat(node.getNumberValue(), equalTo("12.1"));
    }

    @Test
    void matchesANullNumberNode() {
        assertThat(SAMPLE_JSON.isNullableNumberNode("championships", 2), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableNumberNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableNumberValue("championships", 2), equalTo("2004"));
        assertThat(SAMPLE_JSON.getNullableNumberValue("retirement age"), is(nullValue()));
    }

    @Test
    void matchesANullNode() {
        assertThat(SAMPLE_JSON.isNullNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullNode("retirement age"), equalTo(nullNode()));
    }

    @Test
    void matchesAnObjectNode() {
        final Map<JsonStringNode, JsonNode> someJsonMappings = MapBuilder.<JsonStringNode, JsonNode>mapBuilder()
                .put(string("Barry"), string("Lemons"))
                .build();
        final JsonNode node = object(someJsonMappings);
        assertThat(node.isObjectNode(), equalTo(true));
        assertThat(nullNode().isObjectNode(), equalTo(false));
        assertThat(string("Some string").isObjectNode(), equalTo(false));
        assertThat(node.getObjectNode(), equalTo(someJsonMappings));
    }

    @Test
    void matchesANullableObjectNode() {
        final Map<JsonStringNode, JsonNode> someJsonMappings = MapBuilder.<JsonStringNode, JsonNode>mapBuilder()
                .put(string("Barry"), string("Lemons"))
                .build();
        final JsonNode node = object(someJsonMappings);
        assertThat(node.isNullableObjectNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableObjectNode("some", "missing", "path"), equalTo(false));
        assertThat(node.getNullableObjectNode(), equalTo(someJsonMappings));
        assertThat(SAMPLE_JSON.getNullableObjectNode("retirement age"), is(nullValue()));
    }

    @Test
    void matchesAnArrayNode() {
        final List<JsonNode> someJsonNodes = singletonList(number("30"));
        final JsonNode node = array(someJsonNodes);
        assertThat(node.isArrayNode(), equalTo(true));
        assertThat(nullNode().isArrayNode(), equalTo(false));
        assertThat(string("Hi").isArrayNode(), equalTo(false));
        assertThat(node.getArrayNode(), equalTo(someJsonNodes));
    }

    @Test
    void matchesANullableArrayNode() {
        final List<JsonNode> someJsonNodes = singletonList(number("30"));
        final JsonNode node = array(someJsonNodes);
        assertThat(node.isNullableArrayNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableArrayNode("some", "missing", "path"), equalTo(false));
        assertThat(node.getNullableArrayNode(), equalTo(someJsonNodes));
        assertThat(SAMPLE_JSON.getNullableArrayNode("retirement age"), is(nullValue()));
    }

    @Test
    void getArrayNodeFromObjectHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue("championships", 2, 12));
        assertThat(exception.getMessage(), equalTo("Failed to find an array at [\"championships\".2.12] while resolving [\"championships\".2.12] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getFromWrongTypeOfPathElementsHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue("championships", "bob", 2));
        assertThat(exception.getMessage(), equalTo("Failed to find an object at [\"championships\".\"bob\"] while resolving [\"championships\".\"bob\".2] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getFromMissingFieldNameElementsHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue("wrong field name", 2));
        assertThat(exception.getMessage(), equalTo("Failed to find a field called [\"wrong field name\"] at [\"wrong field name\"] while resolving [\"wrong field name\".2] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getFromMissingIndexElementsHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue("championships", 22));
        assertThat(
                exception.getMessage(),
                equalTo("Failed to find an element at index [22] at [\"championships\".22] while resolving [\"championships\".22] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getArrayNodeFromObjectForSingleElementPathHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue(12));
        assertThat(exception.getMessage(), equalTo("Failed to find an array while resolving [12] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getFromWrongTypeOfPathElementsForSingleElementPathHandledNicely() {
        final JsonNode aNode = SAMPLE_JSON.getNode("championships");
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> aNode.getStringValue("bob"));
        assertThat(exception.getMessage(), equalTo("Failed to find an object while resolving [\"bob\"] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(aNode) + "]"));
    }

    @Test
    void getFromMissingFieldNameElementsForSingleElementPathHandledNicely() {
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> SAMPLE_JSON.getStringValue("wrong field name"));
        assertThat(exception.getMessage(), equalTo("Failed to find a field called [\"wrong field name\"] while resolving [\"wrong field name\"] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]"));
    }

    @Test
    void getFromMissingIndexElementsForSingleElementPathHandledNicely() {
        final JsonNode aNode = SAMPLE_JSON.getNode("championships");
        final JsonNodeDoesNotMatchJsonNodeSelectorException exception = assertThrows(JsonNodeDoesNotMatchJsonNodeSelectorException.class, () -> aNode.getStringValue(22));
        assertThat(
                exception.getMessage(),
                equalTo("Failed to find an element at index [22] while resolving [22] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(aNode) + "]"));
    }

    @Test
    void hasTextReturnsCorrectValueForAllNodeTypes() {
        assertThat(nullNode().hasText(), equalTo(false));
        assertThat(array().hasText(), equalTo(false));
        assertThat(falseNode().hasText(), equalTo(false));
        assertThat(number("22.2").hasText(), equalTo(true));
        assertThat(object().hasText(), equalTo(false));
        assertThat(string("Goggle").hasText(), equalTo(true));
        assertThat(trueNode().hasText(), equalTo(false));
    }

    @Test
    void hasFieldsReturnsCorrectValueForAllNodeTypes() {
        assertThat(nullNode().hasFields(), equalTo(false));
        assertThat(array().hasFields(), equalTo(false));
        assertThat(falseNode().hasFields(), equalTo(false));
        assertThat(number("22.2").hasFields(), equalTo(false));
        assertThat(object().hasFields(), equalTo(true));
        assertThat(string("Goggle").hasFields(), equalTo(false));
        assertThat(trueNode().hasFields(), equalTo(false));
    }

    @Test
    void hasElementsReturnsCorrectValueForAllNodeTypes() {
        assertThat(nullNode().hasElements(), equalTo(false));
        assertThat(array().hasElements(), equalTo(true));
        assertThat(falseNode().hasElements(), equalTo(false));
        assertThat(number("22.2").hasElements(), equalTo(false));
        assertThat(object().hasElements(), equalTo(false));
        assertThat(string("Goggle").hasElements(), equalTo(false));
        assertThat(trueNode().hasElements(), equalTo(false));
    }

    @Test
    void isNodeReturnsCorrectValueForAllNodeTypes() {
        assertThat(nullNode().isNode(), equalTo(true));
        assertThat(array().isNode(), equalTo(true));
        assertThat(falseNode().isNode(), equalTo(true));
        assertThat(number("22.2").isNode(), equalTo(true));
        assertThat(object().isNode(), equalTo(true));
        assertThat(string("Goggle").isNode(), equalTo(true));
        assertThat(trueNode().isNode(), equalTo(true));

        assertThat(SAMPLE_JSON.isNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNode("name"), equalTo(true));
        assertThat(SAMPLE_JSON.isNode("championships"), equalTo(true));
        assertThat(SAMPLE_JSON.isNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNode("championships", 2), equalTo(true));
        assertThat(SAMPLE_JSON.isNode("championships", 22), equalTo(false));
        assertThat(SAMPLE_JSON.isNode("championships", 2, 4), equalTo(false));
    }

    @Test
    void getNodeReturnsCorrectValueForAllNodeTypes() {
        assertThat(nullNode().getNode(), equalTo(nullNode()));
        assertThat(array().getNode(), equalTo(array()));
        assertThat(falseNode().getNode(), equalTo(falseNode()));
        assertThat(number("22.2").getNode(), equalTo(number("22.2")));
        assertThat(object().getNode(), equalTo(object()));
        assertThat(string("Goggle").getNode(), equalTo(string("Goggle")));
        assertThat(trueNode().getNode(), equalTo(trueNode()));

        assertThat(SAMPLE_JSON.getNode(), equalTo(SAMPLE_JSON));
        assertThat(SAMPLE_JSON.getNode("name"), equalTo(string("Rossi")));
        assertThat(SAMPLE_JSON.getNode("championships"), equalTo(array(number("2002"), number("2003"), number("2004"), number("2005"), number("2008"), number("2009"))));
        assertThat(SAMPLE_JSON.getNode("retirement age"), equalTo(nullNode()));
        assertThat(SAMPLE_JSON.getNode("championships", 2), equalTo(number("2004")));
    }

    @Test
    void getFieldListReturnsAllFieldsEvenWhenKeysAreDuplicated() {
        final String aKeyString = aString();
        final JsonField aField = field(aKeyString, aJsonNode());
        final JsonField anotherField = field(aKeyString, aJsonNode());
        assertThat(object(aField, anotherField).getFieldList(), contains(aField, anotherField));
    }
}
