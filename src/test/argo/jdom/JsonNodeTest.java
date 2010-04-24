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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public final class JsonNodeTest {

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
        assertThat(aJsonTrue().isBooleanValue(), equalTo(true));
        assertThat(aJsonNull().isBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.isBooleanValue("some", "missing", "path"), equalTo(false));
        assertThat(aJsonTrue().aBooleanValue(), equalTo(Boolean.TRUE));
        assertThat(aJsonTrue().getBooleanValue(), equalTo(Boolean.TRUE));
    }

    @Test
    public void matchesANullBooleanNode() throws Exception {
        assertThat(aJsonTrue().isNullableBooleanValue(), equalTo(true));
        assertThat(aJsonNull().isNullableBooleanValue(), equalTo(true));
        assertThat(aJsonNumber("12").isNullableBooleanValue(), equalTo(false));
        assertThat(SAMPLE_JSON.aNullableBooleanValue("retirement age"), is(nullValue()));
        assertThat(SAMPLE_JSON.getNullableBooleanValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesAStringNode() throws Exception {
        final JsonStringNode node = aJsonString("hello");
        assertThat(node.isStringValue(), equalTo(true));
        assertThat(aJsonTrue().isStringValue(), equalTo(false));
        assertThat(aJsonNull().isStringValue(), equalTo(false));
        assertThat(node.aStringValue(), equalTo("hello"));
        assertThat(node.getStringValue(), equalTo("hello"));
    }

    @Test
    public void matchesANullStringNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullableStringValue("name"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableStringValue("championships"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableStringValue("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.aNullableStringValue("name"), equalTo("Rossi"));
        assertThat(SAMPLE_JSON.aNullableStringValue("retirement age"), is(nullValue()));
        assertThat(SAMPLE_JSON.getNullableStringValue("name"), equalTo("Rossi"));
        assertThat(SAMPLE_JSON.getNullableStringValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesANumberNode() throws Exception {
        final JsonNode node = aJsonNumber("12.1");
        assertThat(node.isNumberValue(), equalTo(true));
        assertThat(aJsonNull().isNumberValue(), equalTo(false));
        assertThat(aJsonString("Hiya!").isNumberValue(), equalTo(false));
        assertThat(node.aNumberValue(), equalTo("12.1"));
        assertThat(node.getNumberValue(), equalTo("12.1"));
    }

    @Test
    public void matchesANullNumberNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullableNumberNode("championships", 2), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableNumberNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableNumberNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.aNullableNumberValue("championships", 2), equalTo("2004"));
        assertThat(SAMPLE_JSON.aNullableNumberValue("retirement age"), is(nullValue()));
        assertThat(SAMPLE_JSON.getNullableNumberValue("championships", 2), equalTo("2004"));
        assertThat(SAMPLE_JSON.getNullableNumberValue("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesANullNode() throws Exception {
        assertThat(SAMPLE_JSON.isNullNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullNode("some", "missing", "path"), equalTo(false));
        assertThat(SAMPLE_JSON.aNullNode("retirement age"), equalTo(aJsonNull()));
        assertThat(SAMPLE_JSON.getNullNode("retirement age"), equalTo(aJsonNull()));
    }

    @Test
    public void matchesAnObjectNode() throws Exception {
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Barry"), aJsonString("Lemons"));
        }};
        final JsonNode node = aJsonObject(someJsonMappings);
        assertThat(node.isObjectNode(), equalTo(true));
        assertThat(aJsonNull().isObjectNode(), equalTo(false));
        assertThat(aJsonString("Some string").isObjectNode(), equalTo(false));
        assertThat(node.anObjectNode(), equalTo(someJsonMappings));
        assertThat(node.getObjectNode(), equalTo(someJsonMappings));
    }

    @Test
    public void matchesANullableObjectNode() throws Exception {
        final Map<JsonStringNode, JsonNode> someJsonMappings = new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Barry"), aJsonString("Lemons"));
        }};
        final JsonNode node = aJsonObject(someJsonMappings);
        assertThat(node.isNullableObjectNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableObjectNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableObjectNode("some", "missing", "path"), equalTo(false));
        assertThat(node.aNullableObjectNode(), equalTo(someJsonMappings));
        assertThat(SAMPLE_JSON.aNullableObjectNode("retirement age"), is(nullValue()));
        assertThat(node.getNullableObjectNode(), equalTo(someJsonMappings));
        assertThat(SAMPLE_JSON.getNullableObjectNode("retirement age"), is(nullValue()));
    }

    @Test
    public void matchesAnArrayNode() throws Exception {
        final List<JsonNode> someJsonNodes = asList(aJsonNumber("30"));
        final JsonNode node = aJsonArray(someJsonNodes);
        assertThat(node.isArrayNode(), equalTo(true));
        assertThat(aJsonNull().isArrayNode(), equalTo(false));
        assertThat(aJsonString("Hi").isArrayNode(), equalTo(false));
        assertThat(node.anArrayNode(), equalTo(someJsonNodes));
        assertThat(node.getArrayNode(), equalTo(someJsonNodes));
    }

    @Test
    public void matchesANullableArrayNode() throws Exception {
        final List<JsonNode> someJsonNodes = asList(aJsonNumber("30"));
        final JsonNode node = aJsonArray(someJsonNodes);
        assertThat(node.isNullableArrayNode(), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("retirement age"), equalTo(true));
        assertThat(SAMPLE_JSON.isNullableArrayNode("name"), equalTo(false));
        assertThat(SAMPLE_JSON.isNullableArrayNode("some", "missing", "path"), equalTo(false));
        assertThat(node.aNullableArrayNode(), equalTo(someJsonNodes));
        assertThat(SAMPLE_JSON.aNullableArrayNode("retirement age"), is(nullValue()));
        assertThat(node.getNullableArrayNode(), equalTo(someJsonNodes));
        assertThat(SAMPLE_JSON.getNullableArrayNode("retirement age"), is(nullValue()));
    }

}
