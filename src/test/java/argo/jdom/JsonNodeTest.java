/*
 * Copyright 2014 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.format.CompactJsonFormatter;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.aJsonLeafNode;
import static argo.jdom.JsonStringNodeTestBuilder.aValidJsonString;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class JsonNodeTest {

    private static final JsonRootNode SAMPLE_JSON = object(
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
    public void matchesABooleanNode() throws Exception {
        assertThat(trueNode().isBooleanValue(), equalTo(true));
        assertThat(nullNode().isBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.isBooleanValue("some", "missing", "path"), equalTo(false));
        assertThat(trueNode().getBooleanValue(), equalTo(Boolean.TRUE));
    }

    @Test
    public void matchesANullBooleanNode() throws Exception {
        assertThat(trueNode().isNullableBooleanValue(), equalTo(true));
        assertThat(nullNode().isNullableBooleanValue(), equalTo(true));
        assertThat(number("12").isNullableBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableBooleanValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesAStringNode() throws Exception {
        final JsonStringNode node = string("hello");
        assertThat(node.isStringValue(), equalTo(true));
        assertThat(trueNode().isStringValue(), equalTo(false));
        assertThat(nullNode().isStringValue(), equalTo(false));
        assertThat(node.getStringValue(), equalTo("hello"));
    }

    @Test
    public void matchesANullStringNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullableStringValue("name"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("championships"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableStringValue("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableStringValue("name"), equalTo("Rossi"));
        assertThat(SAMPLE_JSON.getNullableStringValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesANumberNode() throws Exception {
        final JsonNode node = number("12.1");
        assertThat(node.isNumberValue(), equalTo(true));
        assertThat(nullNode().isNumberValue(), equalTo(false));
        assertThat(string("Hiya!").isNumberValue(), equalTo(false));
        assertThat(node.getNumberValue(), equalTo("12.1"));
    }

    @Test
    public void matchesANullNumberNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullableNumberNode("championships", 2), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableNumberNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullableNumberValue("championships", 2), equalTo("2004"));
        assertThat(SAMPLE_JSON.getNullableNumberValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesANullNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.getNullNode("retirement age"), equalTo(nullNode()));
    }

    @Test
    public void matchesAnObjectNode() throws Exception {
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Barry"), string("Lemons"));
        }};
        final JsonNode node = object(someJsonMappings);
        assertThat(node.isObjectNode(), equalTo(true));
        assertThat(nullNode().isObjectNode(), equalTo(false));
        assertThat(string("Some string").isObjectNode(), equalTo(false));
        assertThat(node.getObjectNode(), equalTo(someJsonMappings));
    }

    @Test
    public void matchesANullableObjectNode() throws Exception {
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Barry"), string("Lemons"));
        }};
        final JsonNode node = object(someJsonMappings);
        assertThat(node.isNullableObjectNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableObjectNode("some", "missing", "path"), equalTo(false));
        assertThat(node.getNullableObjectNode(), equalTo(someJsonMappings));
        assertThat(SAMPLE_JSON.getNullableObjectNode("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesAnArrayNode() throws Exception {
        final List<JsonNode> someJsonNodes = asList(number("30"));
        final JsonNode node = array(someJsonNodes);
        assertThat(node.isArrayNode(), equalTo(true));
        assertThat(nullNode().isArrayNode(), equalTo(false));
        assertThat(string("Hi").isArrayNode(), equalTo(false));
        assertThat(node.getArrayNode(), equalTo(someJsonNodes));
    }

    @Test
    public void matchesANullableArrayNode() throws Exception {
        final List<JsonNode> someJsonNodes = asList(number("30"));
        final JsonNode node = array(someJsonNodes);
        assertThat(node.isNullableArrayNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableArrayNode("some", "missing", "path"), equalTo(false));
        assertThat(node.getNullableArrayNode(), equalTo(someJsonNodes));
        assertThat(SAMPLE_JSON.getNullableArrayNode("retirement age"), is(nullValue()));
    }

    @Test
    public void getArrayNodeFromObjectHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue("championships", 2, 12);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find an array at [\"championships\".2.12] while resolving [\"championships\".2.12] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getFromWrongTypeOfPathElementsHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue("championships", "bob", 2);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find an object at [\"championships\".\"bob\"] while resolving [\"championships\".\"bob\".2] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getFromMissingFieldNameElementsHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue("wrong field name", 2);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find a field called [\"wrong field name\"] at [\"wrong field name\"] while resolving [\"wrong field name\".2] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getFromMissingIndexElementsHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue("championships", 22);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(
                    e.getMessage(),
                    equalTo("Failed to find an element at index [22] at [\"championships\".22] while resolving [\"championships\".22] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getArrayNodeFromObjectForSingleElementPathHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue(12);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find an array while resolving [12] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getFromWrongTypeOfPathElementsForSingleElementPathHandledNicely() throws Exception {
        final JsonRootNode aNode = SAMPLE_JSON.getRootNode("championships");
        try {
            aNode.getStringValue("bob");
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find an object while resolving [\"bob\"] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(aNode) + "]."));
        }
    }

    @Test
    public void getFromMissingFieldNameElementsForSingleElementPathHandledNicely() throws Exception {
        try {
            SAMPLE_JSON.getStringValue("wrong field name");
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(e.getMessage(), equalTo("Failed to find a field called [\"wrong field name\"] while resolving [\"wrong field name\"] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(SAMPLE_JSON) + "]."));
        }
    }

    @Test
    public void getFromMissingIndexElementsForSingleElementPathHandledNicely() throws Exception {
        final JsonRootNode aNode = SAMPLE_JSON.getRootNode("championships");
        try {
            aNode.getStringValue(22);
            fail("Should have thrown a JsonNodeDoesNotMatchJsonNodeSelectorException");
        } catch (final JsonNodeDoesNotMatchJsonNodeSelectorException e) {
            assertThat(
                    e.getMessage(),
                    equalTo("Failed to find an element at index [22] while resolving [22] in [" + CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter().format(aNode) + "]."));
        }
    }

    @Test
    public void hasTextReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(nullNode().hasText(), equalTo(false));
        assertThat(array().hasText(), equalTo(false));
        assertThat(falseNode().hasText(), equalTo(false));
        assertThat(number("22.2").hasText(), equalTo(true));
        assertThat(object().hasText(), equalTo(false));
        assertThat(string("Goggle").hasText(), equalTo(true));
        assertThat(trueNode().hasText(), equalTo(false));
    }

    @Test
    public void hasFieldsReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(nullNode().hasFields(), equalTo(false));
        assertThat(array().hasFields(), equalTo(false));
        assertThat(falseNode().hasFields(), equalTo(false));
        assertThat(number("22.2").hasFields(), equalTo(false));
        assertThat(object().hasFields(), equalTo(true));
        assertThat(string("Goggle").hasFields(), equalTo(false));
        assertThat(trueNode().hasFields(), equalTo(false));
    }

    @Test
    public void hasElementsReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(nullNode().hasElements(), equalTo(false));
        assertThat(array().hasElements(), equalTo(true));
        assertThat(falseNode().hasElements(), equalTo(false));
        assertThat(number("22.2").hasElements(), equalTo(false));
        assertThat(object().hasElements(), equalTo(false));
        assertThat(string("Goggle").hasElements(), equalTo(false));
        assertThat(trueNode().hasElements(), equalTo(false));
    }

    @Test
    public void isNodeReturnsCorrectValueForAllNodeTypes() throws Exception {
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
    public void getNodeReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(nullNode().getNode(), equalTo(nullNode()));
        assertThat(array().getNode(), equalTo((JsonNode) array()));
        assertThat(falseNode().getNode(), equalTo(falseNode()));
        assertThat(number("22.2").getNode(), equalTo(number("22.2")));
        assertThat(object().getNode(), equalTo((JsonNode) object()));
        assertThat(string("Goggle").getNode(), equalTo((JsonNode) string("Goggle")));
        assertThat(trueNode().getNode(), equalTo(trueNode()));

        assertThat(SAMPLE_JSON.getNode(), equalTo((JsonNode) SAMPLE_JSON));
        assertThat(SAMPLE_JSON.getNode("name"), equalTo((JsonNode) string("Rossi")));
        assertThat(SAMPLE_JSON.getNode("championships"), equalTo((JsonNode) array(number("2002"), number("2003"), number("2004"), number("2005"), number("2008"), number("2009"))));
        assertThat(SAMPLE_JSON.getNode("retirement age"), equalTo(nullNode()));
        assertThat(SAMPLE_JSON.getNode("championships", 2), equalTo(number("2004")));
    }

    @Test
    public void isRootNodeReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(nullNode().isRootNode(), equalTo(false));
        assertThat(array().isRootNode(), equalTo(true));
        assertThat(falseNode().isRootNode(), equalTo(false));
        assertThat(number("22.2").isRootNode(), equalTo(false));
        assertThat(object().isRootNode(), equalTo(true));
        assertThat(string("Goggle").isRootNode(), equalTo(false));
        assertThat(trueNode().isRootNode(), equalTo(false));

        assertThat(SAMPLE_JSON.isRootNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isRootNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isRootNode("championships"), equalTo(true));
        assertThat(SAMPLE_JSON.isRootNode("retirement age"), equalTo(false));
        assertThat(SAMPLE_JSON.isRootNode("championships", 2), equalTo(false));
        assertThat(SAMPLE_JSON.isRootNode("championships", 22), equalTo(false));
        assertThat(SAMPLE_JSON.isRootNode("championships", 2, 4), equalTo(false));
    }

    @Test
    public void getRootNodeReturnsCorrectValueForAllNodeTypes() throws Exception {
        assertThat(array().getRootNode(), equalTo((JsonNode) array()));
        assertThat(object().getRootNode(), equalTo((JsonNode) object()));

        assertThat(SAMPLE_JSON.getRootNode(), equalTo(SAMPLE_JSON));
        assertThat(SAMPLE_JSON.getRootNode("championships"), equalTo(array(number("2002"), number("2003"), number("2004"), number("2005"), number("2008"), number("2009"))));
    }

    @Test
    public void getFieldListReturnsAllFieldsEvenWhenKeysAreDuplicated() throws Exception {
        final String aKeyString = aValidJsonString();
        final JsonField aField = field(aKeyString, aJsonLeafNode());
        final JsonField anotherField = field(aKeyString, aJsonLeafNode());
        assertThat(object(aField, anotherField).getFieldList(), contains(aField, anotherField));
    }
}
