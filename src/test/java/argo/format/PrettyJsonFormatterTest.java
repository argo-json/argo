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
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("deprecation")
final class PrettyJsonFormatterTest {
    static final class FieldOrderPreservingPrettyJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter(),
                    new JsonGeneratorFieldOrderPreservingJsonFormatterAdapter(new JsonGenerator()),
                    new JsonGeneratorFieldOrderPreservingJsonFormatterAdapter(new JsonGenerator().style(PRETTY))
            ).map(Arguments::arguments);
        }
    }

    static final class FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    PrettyJsonFormatter.fieldOrderNormalisingPrettyJsonFormatter(),
                    new JsonGeneratorFieldOrderNormalisingJsonFormatterAdapter(new JsonGenerator()),
                    new JsonGeneratorFieldOrderNormalisingJsonFormatterAdapter(new JsonGenerator().style(PRETTY))
            ).map(Arguments::arguments);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAJsonObject(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(asList(
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

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAnEmptyJsonObject(final JsonFormatter jsonFormatter) {
        assertThat(
                jsonFormatter.format(object(field("Hello", object()))),
                equalTo(aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"Hello\": {}")
                        .print("}")
                        .build())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAJsonArray(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(asList(
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

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAnEmptyJsonArray(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(emptyList())), equalTo(
                aJsonStringResultBuilder()
                        .print("[]")
                        .build()
        )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAString(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(string("foo")), equalTo(
                aJsonStringResultBuilder()
                        .print("\"foo\"")
                        .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsANumber(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(number("123.456E789")), equalTo(
                aJsonStringResultBuilder()
                        .print("123.456E789")
                        .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsANull(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(nullNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("null")
                        .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsATrue(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(trueNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("true")
                        .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAFalse(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(falseNode()), equalTo(
                aJsonStringResultBuilder()
                        .print("false")
                        .build()
                )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAJsonStringWithEscapedCharacters(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
                (JsonNode) string("\" \\ \b \f \n \r \t")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\\\" \\\\ \\b \\f \\n \\r \\t\"")
                        .print("]")
                        .build()
        )
        );
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsAStringWithinAString(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
                (JsonNode) string("\"\\\"A String\\\" within a String\"")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\\\"\\\\\\\"A String\\\\\\\" within a String\\\"\"")
                        .print("]")
                        .build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void testRoundTrip(final JsonFormatter jsonFormatter) throws Exception {
        final File longJsonExample = new File(this.getClass().getResource("Sample.json").getFile());
        final String json = readFileToString(longJsonExample, UTF_8);
        final JdomParser jdomParser = new JdomParser();
        final JsonNode node = jdomParser.parse(json);
        final String expected = jsonFormatter.format(node);
        assertThat(jdomParser.parse(expected), Matchers.equalTo(node));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void orderPreservingFormatterPreservesFieldOrder(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("b", string("A String")), field("a", string("A String")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"b\": \"A String\",")
                        .printLine("\t\"a\": \"A String\"")
                        .print("}")
                        .build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrder(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("b", string("A String")), field("a", string("A String")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"a\": \"A String\",")
                        .printLine("\t\"b\": \"A String\"")
                        .print("}")
                        .build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinArray(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(object(field("b", string("A String")), field("a", string("A String"))))), equalTo(
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

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinObject(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("Foo", object(field("b", string("A String")), field("a", string("A String")))))), equalTo(
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

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinArrayWithinObject(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(object(field("Foo", array(object(field("b", string("A String")), field("a", string("A String"))))))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("{")
                        .printLine("\t\"Foo\": [")
                        .printLine("\t\t{")
                        .printLine("\t\t\t\"a\": \"A String\",")
                        .printLine("\t\t\t\"b\": \"A String\"")
                        .printLine("\t\t}")
                        .printLine("\t]")
                        .print("}")
                        .build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderNormalisingPrettyJsonFormatterArgumentsProvider.class)
    void orderNormalisingFormatterNormalisesFieldOrderOfObjectWithinObjectWithinArray(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(object(field("Foo", object(field("b", string("A String")), field("a", string("A String"))))))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t{")
                        .printLine("\t\t\"Foo\": {")
                        .printLine("\t\t\t\"a\": \"A String\",")
                        .printLine("\t\t\t\"b\": \"A String\"")
                        .printLine("\t\t}")
                        .printLine("\t}")
                        .print("]")
                        .build()
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsEcmaSurrogatePairExamples(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
                (JsonNode) string("\ud834\udd1e")))), equalTo(
                aJsonStringResultBuilder()
                        .printLine("[")
                        .printLine("\t\"\ud834\udd1e\"")
                        .print("]")
                        .build()
        )
        );
    }


    @ParameterizedTest
    @ArgumentsSource(FieldOrderPreservingPrettyJsonFormatterArgumentsProvider.class)
    void formatsControlCharacters(final JsonFormatter jsonFormatter) {
        assertThat(jsonFormatter.format(array(singletonList(
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
