/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.format.PrettyJsonFormatter.fieldOrderNormalisingPrettyJsonFormatter;
import static argo.format.PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter;
import static argo.jdom.JsonNodeFactories.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public final class PrettyJsonFormatterTest {
    @Test
    public void formatsAJsonObject() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(object(newArrayList(
            field(string("Hello"), string("World")),
            field(string("Foo"), string("Bar"))
        ))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"Hello\": \"World\",")
                        .printLine("\t\"Foo\": \"Bar\"")
                        .print("}")
                        .build()
        ));
    }

    @Test
    public void formatsAnEmptyJsonObject() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(object(new HashMap<JsonStringNode, JsonNode>() {{
                    put(string("Hello"), object(new HashMap<>()));
        }})), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"Hello\": {}")
                        .print("}")
                        .build()
        )
        );
    }

    @Test
    public void formatsAJsonArray() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(asList(
                string("BobBob")
                , number("23")
        ))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"BobBob\",")
                        .printLine("\t23")
                        .print("]")
                        .build()
        )
        );
    }

    @Test
    public void formatsAnEmptyJsonArray() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(emptyList())), equalTo(
                aJsonStringResultBuilder()
                        .print("[]")
                        .build()
        )
        );
    }

    @Test
    public void formatsAString() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(string("foo")), equalTo(
                aJsonStringResultBuilder()
                        .print("\"foo\"")
                        .build()
                )
        );
    }

    @Test
    public void formatsANumber() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(number("123.456E789")), equalTo(
                aJsonStringResultBuilder()
                        .print("123.456E789")
                        .build()
                )
        );
    }

    @Test
    public void formatsANull() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(nullNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("null")
                        .build()
                )
        );
    }

    @Test
    public void formatsATrue() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(trueNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("true")
                        .build()
                )
        );
    }

    @Test
    public void formatsAFalse() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(falseNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("false")
                        .build()
                )
        );
    }

    @Test
    public void formatsAJsonStringWithEscapedCharacters() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(singletonList(
                (JsonNode) string("\" \\ \b \f \n \r \t")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\\\" \\\\ \\b \\f \\n \\r \\t\"")
                        .print("]")
                        .build()
        )
        );
    }

    @Test
    public void formatsAStringWithinAString() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(singletonList(
                (JsonNode) string("\"\\\"A String\\\" within a String\"")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"")
                        .print("]")
                        .build()
        ));
    }

    @Test
    public void testRoundTrip() throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("Sample.json").getFile());
        final String json = readFileToString(longJsonExample, UTF_8);
        final JdomParser jdomParser = new JdomParser();
        final JsonNode node = jdomParser.parse(json);
        final JsonFormatter jsonFormatter = fieldOrderPreservingPrettyJsonFormatter();
        final String expected = jsonFormatter.format(node);
        assertThat(jdomParser.parse(expected), Matchers.equalTo(node));
    }

    @Test
    public void orderPreservingFormatterPreservesFieldOrder() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(object(field("b", string("A String")), field("a", string("A String")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"b\": \"A String\",")
                        .printLine("\t\"a\": \"A String\"")
                        .print("}")
                        .build()
        ));
    }

    @Test
    public void orderNormalisingFormatterNormalisesFieldOrder() throws Exception {
        assertThat(fieldOrderNormalisingPrettyJsonFormatter().format(object(field("b", string("A String")), field("a", string("A String")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"a\": \"A String\",")
                        .printLine("\t\"b\": \"A String\"")
                        .print("}")
                        .build()
        ));
    }

    @Test
    public void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinArray() throws Exception {
        assertThat(fieldOrderNormalisingPrettyJsonFormatter().format(array(object(field("b", string("A String")), field("a", string("A String"))))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t{")
                        .printLine("\t\t\"a\": \"A String\",")
                        .printLine("\t\t\"b\": \"A String\"")
                        .printLine("\t}")
                        .print("]")
                        .build()
        ));
    }

    @Test
    public void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinObject() throws Exception {
        assertThat(fieldOrderNormalisingPrettyJsonFormatter().format(object(field("Foo", object(field("b", string("A String")), field("a", string("A String")))))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"Foo\": {")
                        .printLine("\t\t\"a\": \"A String\",")
                        .printLine("\t\t\"b\": \"A String\"")
                        .printLine("\t}")
                        .print("}")
                        .build()
        ));
    }

    @Test
    public void formatsEcmaSurrogatePairExamples() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(singletonList(
                (JsonNode) string("\ud834\udd1e")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\ud834\udd1e\"")
                        .print("]")
                        .build()
        )
        );
    }


    @Test
    public void formatsControlCharacters() throws Exception {
        assertThat(fieldOrderPreservingPrettyJsonFormatter().format(array(singletonList(
                        (JsonNode) string("\u0000")))), equalTo(
                        aJsonStringResultBuilder()
                                .printLine("[")
                                .printLine("\t\"\\u0000\"")
                                .print("]")
                                .build()
                )
        );
    }

}
