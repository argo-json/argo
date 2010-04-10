/*
 * Copyright 2010 Mark Slater
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
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public final class CompactJsonFormatTest {
    @Test
    public void formatsAJsonObject() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Hello"), aJsonString("World"));
            put(aJsonString("Foo"), aJsonString("Bar"));
        }})), Matchers.anyOf(
                equalTo("{\"Hello\":\"World\",\"Foo\":\"Bar\"}")
                , equalTo("{\"Foo\":\"Bar\",\"Hello\":\"World\"}")
        ));
    }

    @Test
    public void formatsAJsonNumber() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("S"), aJsonNumber("7"));
        }})), equalTo("{\"S\":7}"));
    }

    @Test
    public void formatsAJsonArray() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonArray(Arrays.asList(
                aJsonNumber("12")
                , aJsonString("tie")
        ))), equalTo("[12,\"tie\"]"));
    }

    @Test
    public void formatsTheJsonConstants() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonArray(Arrays.asList(
                aJsonNull()
                , aJsonTrue()
                , aJsonFalse()
        ))), equalTo("[null,true,false]"));
    }

    @Test
    public void formatsAJsonStringWithEscapedCharacters() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonArray(Arrays.asList(
                (JsonNode)aJsonString("\" \\ \b \f \n \r \t"))
        )), equalTo("[\"\\\" \\\\ \\b \\f \\n \\r \\t\"]"));
    }

    @Test
    public void formatsAStringWithinAString() throws Exception {
        assertThat(new CompactJsonFormatter().format(aJsonArray(Arrays.asList(
                (JsonNode)aJsonString("\"\\\"A String\\\" within a String\""))
        )), equalTo("[\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"]"));
    }
}
