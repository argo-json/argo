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
import static argo.jdom.JsonNodeSelecter.aNumberNode;
import static argo.jdom.JsonNodeSelecter.anArrayNode;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class JsonNodeSelecterTest {

    @Test
    public void matchesAStringNode() throws Exception {
        final JsonNodeSelecter<String> jsonNodeSelecter = JsonNodeSelecter.aStringNode();
        assertTrue(jsonNodeSelecter.matches(aJsonString("hello")));
    }

    @Test
    public void getsValueOfAStringNode() throws Exception {
        final JsonNodeSelecter<String> jsonNodeSelecter = JsonNodeSelecter.aStringNode();
        assertThat(jsonNodeSelecter.getValue(aJsonString("hello")), equalTo("hello"));
    }

    @Test
    public void matchesANumberNode() throws Exception {
        final JsonNodeSelecter<String> jsonNodeSelecter = aNumberNode();
        assertTrue(jsonNodeSelecter.matches(aJsonNumber("12.1")));
    }

    @Test
    public void getsValueOfANumberNode() throws Exception {
        final JsonNodeSelecter<String> jsonNodeSelecter = aNumberNode();
        assertThat(jsonNodeSelecter.getValue(aJsonNumber("12.1")), equalTo("12.1"));
    }

    @Test
    public void matchesAnArrayNode() throws Exception {
        final JsonNodeSelecter<List<JsonNode>> jsonNodeSelecter = anArrayNode();
        assertTrue(jsonNodeSelecter.matches(aJsonArray(new LinkedList<JsonNode>())));
    }

    @Test
    public void getsValueOfAnArrayNode() throws Exception {
        final JsonNodeSelecter<List<JsonNode>> jsonNodeSelecter = anArrayNode();
        assertThat(jsonNodeSelecter.getValue(aJsonArray(new LinkedList<JsonNode>())), equalTo((List<JsonNode>)new LinkedList<JsonNode>()));
    }

    @Test
    public void matchesAnElementOfAnArrayNode() throws Exception {
        final JsonNodeSelecter<JsonNode> jsonNodeSelecter = anArrayNode(0);
        assertTrue(jsonNodeSelecter.matches(aJsonArray(new LinkedList<JsonNode>(Arrays.asList(aJsonString("hello"))))));
    }

    @Test
    public void getsValueOfAnElementOfAnArrayNode() throws Exception {
        final JsonNodeSelecter<JsonNode> jsonNodeSelecter = JsonNodeSelecter.anArrayNode(0);
        assertThat(jsonNodeSelecter.getValue(aJsonArray(new LinkedList<JsonNode>(Arrays.asList(aJsonString("hello"))))), equalTo((JsonNode)aJsonString("hello")));
    }

    @Test
    public void rejectsAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelecter<JsonNode> jsonNodeSelecter = anArrayNode(0);
        assertFalse(jsonNodeSelecter.matches(aJsonArray(new LinkedList<JsonNode>(new LinkedList<JsonNode>()))));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getsValueOfAnElementOfAnArrayNodeGreaterThanArraySize() throws Exception {
        final JsonNodeSelecter<JsonNode> jsonNodeSelecter = JsonNodeSelecter.anArrayNode(0);
        jsonNodeSelecter.getValue(aJsonArray(new LinkedList<JsonNode>()));
    }

}
