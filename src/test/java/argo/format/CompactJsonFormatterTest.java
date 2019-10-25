/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static argo.format.CompactJsonFormatter.fieldOrderNormalisingCompactJsonFormatter;
import static argo.format.CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter;
import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

final class CompactJsonFormatterTest {
    @Test
    void formatsAJsonObject() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(object(newArrayList(
                field(string("Foo"), string("Bar")),
                field(string("Hello"), string("World"))
        ))), equalTo("{\"Foo\":\"Bar\",\"Hello\":\"World\"}"));
    }

    @Test
    void formatsAJsonNumber() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("S"), number("7"));
        }})), equalTo("{\"S\":7}"));
    }

    @Test
    void formatsAJsonArray() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(array(asList(
                number("12")
                , string("tie")
        ))), equalTo("[12,\"tie\"]"));
    }

    @Test
    void formatsTheJsonConstants() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(array(asList(
                nullNode()
                , trueNode()
                , falseNode()
        ))), equalTo("[null,true,false]"));
    }


    @Test
    void formatsAString() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(string("foo")), equalTo(
                aJsonStringResultBuilder()
                        .print("\"foo\"")
                        .build()
                )
        );
    }

    @Test
    void formatsANumber() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(number("123.456E789")), equalTo(
                aJsonStringResultBuilder()
                        .print("123.456E789")
                        .build()
                )
        );
    }

    @Test
    void formatsANull() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(nullNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("null")
                        .build()
                )
        );
    }

    @Test
    void formatsATrue() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(trueNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("true")
                        .build()
                )
        );
    }

    @Test
    void formatsAFalse() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(falseNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("false")
                        .build()
                )
        );
    }

    @Test
    void formatsAJsonStringWithEscapedCharacters() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(array(asList(
                (JsonNode) string("\" \\ \b \f \n \r \t")))), equalTo("[\"\\\" \\\\ \\b \\f \\n \\r \\t\"]"));
    }

    @Test
    void formatsAStringWithinAString() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(array(asList(
                (JsonNode) string("\"\\\"A String\\\" within a String\"")))), equalTo("[\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"]"));
    }

    @Test
    void orderPreservingFormatterPreservesFieldOrder() {
        assertThat(fieldOrderPreservingCompactJsonFormatter().format(object(field("b", string("A String")), field("a", string("A String")))), equalTo("{\"b\":\"A String\",\"a\":\"A String\"}"));
    }

    @Test
    void orderNormalisingFormatterNormalisesFieldOrder() {
        assertThat(fieldOrderNormalisingCompactJsonFormatter().format(object(field("b", string("A String")), field("a", string("A String")))), equalTo("{\"a\":\"A String\",\"b\":\"A String\"}"));
    }
}
