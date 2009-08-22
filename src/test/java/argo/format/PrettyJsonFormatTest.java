/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import static argo.jdom.JsonNodeFactories.*;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public final class PrettyJsonFormatTest {
    @Test
    public void formatsAJsonObject() throws Exception {
        assertThat(new PrettyJsonFormatter().format(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Hello"), aJsonString("World"));
            put(aJsonString("Foo"), aJsonString("Bar"));
        }})), Matchers.anyOf(
                equalTo(
                        aJsonStringResultBuilder()
                                .println("{")
                                .println("\t\"Hello\": \"World\"")
                                .println("\t, \"Foo\": \"Bar\"")
                                .print("}")
                                .build()
                )
                , equalTo(
                        aJsonStringResultBuilder()
                                .println("{")
                                .println("\t\"Foo\": \"Bar\"")
                                .println("\t, \"Hello\": \"World\"")
                                .print("}")
                                .build()
                )
        ));
    }

    @Test
    public void formatsAnEmptyJsonObject() throws Exception {
        assertThat(new PrettyJsonFormatter().format(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Hello"), aJsonObject(new HashMap<JsonStringNode, JsonNode>()));
        }})), equalTo(
                aJsonStringResultBuilder()
                        .println("{")
                        .println("\t\"Hello\": {}")
                        .print("}")
                        .build()
        )
        );
    }

    @Test
    public void formatsAJsonArray() throws Exception {
        assertThat(new PrettyJsonFormatter().format(aJsonArray(Arrays.asList(
                aJsonString("BobBob")
                , aJsonNumber("23")
        ))), equalTo(
                aJsonStringResultBuilder()
                        .println("[")
                        .println("\t\"BobBob\"")
                        .println("\t, 23")
                        .print("]")
                        .build()
        )
        );
    }

    @Test
    public void formatsAnEmptyJsonArray() throws Exception {
        assertThat(new PrettyJsonFormatter().format(aJsonArray(Collections.<JsonNode>emptyList()
        )), equalTo(
                aJsonStringResultBuilder()
                        .print("[]")
                        .build()
        )
        );
    }

    @Test
    public void formatsAJsonStringWithEscapedCharacters() throws Exception {
        assertThat(new PrettyJsonFormatter().format(aJsonArray(Arrays.asList(
                (JsonNode)aJsonString("\" \\ / \b \f \n \r \t"))
        )), equalTo(
                aJsonStringResultBuilder()
                        .println("[")
                        .println("\t\"\\\" \\\\ \\/ \\b \\f \\n \\r \\t\"")
                        .print("]")
                        .build()
        )
        );
    }

    @Test
    public void testRoundtrip() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("Sample.json").getFile());
        final String json = readFileToString(longJsonExample);
        final JdomParser jdomParser = new JdomParser();
        final JsonRootNode node = jdomParser.parse(json);
        final JsonFormatter jsonFormatter = new PrettyJsonFormatter();
        final String expected = jsonFormatter.format(node);
        assertThat(jdomParser.parse(expected), Matchers.equalTo(node));
    }

}
