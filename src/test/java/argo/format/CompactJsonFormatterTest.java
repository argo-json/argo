/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.JsonGenerator;
import argo.jdom.JsonNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("deprecation")
final class CompactJsonFormatterTest {
    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAJsonObject(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(asList(
                field(string("Foo"), string("Bar")),
                field(string("Hello"), string("World"))
        ))), equalTo("{\"Foo\":\"Bar\",\"Hello\":\"World\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAJsonNumber(final JsonFormatter jsonFormatter) {
        assertThat(
                jsonFormatter.format(object(field("S", number("7")))),
                equalTo("{\"S\":7}")
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAJsonArray(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(asList(
                number("12")
                , string("tie")
        ))), equalTo("[12,\"tie\"]"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsTheJsonConstants(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(asList(
                nullNode()
                , trueNode()
                , falseNode()
        ))), equalTo("[null,true,false]"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAString(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(string("foo")), equalTo(
                        aJsonStringResultBuilder()
                                .print("\"foo\"")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsANumber(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(number("123.456E789")), equalTo(
                        aJsonStringResultBuilder()
                                .print("123.456E789")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsANull(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(nullNode()), equalTo(
                        aJsonStringResultBuilder()
                                .print("null")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsATrue(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(trueNode()), equalTo(
                        aJsonStringResultBuilder()
                                .print("true")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAFalse(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(falseNode()), equalTo(
                        aJsonStringResultBuilder()
                                .print("false")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAJsonStringWithEscapedCharacters(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
                (JsonNode) string("\" \\ \b \f \n \r \t")))), equalTo("[\"\\\" \\\\ \\b \\f \\n \\r \\t\"]"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void formatsAStringWithinAString(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
                (JsonNode) string("\"\\\"A String\\\" within a String\"")))), equalTo("[\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"]"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingCompactJsonFormatterArgumentsProvider.class)
    void orderPreservingFormatterPreservesFieldOrder(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("b", string("A String")), field("a", string("A String")))), equalTo("{\"b\":\"A String\",\"a\":\"A String\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingCompactJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrder(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("b", string("A String")), field("a", string("A String")))), equalTo("{\"a\":\"A String\",\"b\":\"A String\"}"));
    }

    static final class FieldOrderPreservingCompactJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter(),
                    new JsonGeneratorFieldOrderPreservingJsonFormatterAdapter(new JsonGenerator().style(COMPACT))
            ).map(Arguments::arguments);
        }
    }

    static final class FieldOrderNormalisingCompactJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    CompactJsonFormatter.fieldOrderNormalisingCompactJsonFormatter(),
                    new JsonGeneratorFieldOrderNormalisingJsonFormatterAdapter(new JsonGenerator().style(COMPACT))
            ).map(Arguments::arguments);
        }
    }
}
