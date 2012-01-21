/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public final class CompactJsonFormatterTest {
    @Test
    public void formatsAJsonObject() throws Exception {
        assertThat(new CompactJsonFormatter().format(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Hello"), string("World"));
            put(string("Foo"), string("Bar"));
        }})), equalTo("{\"Foo\":\"Bar\",\"Hello\":\"World\"}"));
    }

    @Test
    public void formatsAJsonNumber() throws Exception {
        assertThat(new CompactJsonFormatter().format(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("S"), number("7"));
        }})), equalTo("{\"S\":7}"));
    }

    @Test
    public void formatsAJsonArray() throws Exception {
        assertThat(new CompactJsonFormatter().format(array(Arrays.asList(
                number("12")
                , string("tie")
        ))), equalTo("[12,\"tie\"]"));
    }

    @Test
    public void formatsTheJsonConstants() throws Exception {
        assertThat(new CompactJsonFormatter().format(array(Arrays.asList(
                nullNode()
                , trueNode()
                , falseNode()
        ))), equalTo("[null,true,false]"));
    }

    @Test
    public void formatsAJsonStringWithEscapedCharacters() throws Exception {
        assertThat(new CompactJsonFormatter().format(array(Arrays.asList(
                (JsonNode) string("\" \\ \b \f \n \r \t")))), equalTo("[\"\\\" \\\\ \\b \\f \\n \\r \\t\"]"));
    }

    @Test
    public void formatsAStringWithinAString() throws Exception {
        assertThat(new CompactJsonFormatter().format(array(Arrays.asList(
                (JsonNode) string("\"\\\"A String\\\" within a String\"")))), equalTo("[\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"]"));
    }
}
