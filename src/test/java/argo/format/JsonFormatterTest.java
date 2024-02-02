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

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.anArrayNode;
import static argo.jdom.JsonNodeTestBuilder.anObjectNode;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JsonFormatterTest {

    static final class FieldOrderPreservingJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    CompactJsonFormatter.fieldOrderPreservingCompactJsonFormatter(),
                    PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter()
            ).map(Arguments::arguments);
        }
    }

    static final class FieldOrderNormalisingJsonFormatterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    CompactJsonFormatter.fieldOrderNormalisingCompactJsonFormatter(),
                    PrettyJsonFormatter.fieldOrderNormalisingPrettyJsonFormatter()
            ).map(Arguments::arguments);
        }
    }

    @Nested
    class FieldOrderPreservingJsonFormatter {
        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughStringUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode stringNode = aStringNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(stringNode)), equalTo(stringNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughNumberUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode numberNode = aNumberNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(numberNode)), equalTo(numberNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughTrueUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode trueNode = trueNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(trueNode)), equalTo(trueNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughFalseUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode falseNode = falseNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(falseNode)), equalTo(falseNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughNullUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode nullNode = nullNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(nullNode)), equalTo(nullNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughArrayUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode arrayNode = anArrayNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(arrayNode)), equalTo(arrayNode));
        }

        @ParameterizedTest
        @ArgumentsSource(FieldOrderPreservingJsonFormatterArgumentsProvider.class)
        void passesThroughObjectUnmolested(final JsonFormatter jsonFormatter) throws Exception {
            final JsonNode objectNode = anObjectNode();
            assertThat(new JdomParser().parse(jsonFormatter.format(objectNode)), equalTo(objectNode));
        }
    }

    @Nested
    class FieldOrderNormalisingJsonFormatter {

        @Nested
        class Root {
            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughStringUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode stringNode = aStringNode();
                assertThat(new JdomParser().parse(jsonFormatter.format(stringNode)), equalTo(stringNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNumberUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode numberNode = aNumberNode();
                assertThat(new JdomParser().parse(jsonFormatter.format(numberNode)), equalTo(numberNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughTrueUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode trueNode = trueNode();
                assertThat(new JdomParser().parse(jsonFormatter.format(trueNode)), equalTo(trueNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughFalseUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode falseNode = falseNode();
                assertThat(new JdomParser().parse(jsonFormatter.format(falseNode)), equalTo(falseNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNullUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode nullNode = nullNode();
                assertThat(new JdomParser().parse(jsonFormatter.format(nullNode)), equalTo(nullNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughArrayUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode arrayNode = array(
                        aStringNode(),
                        aNumberNode(),
                        trueNode(),
                        falseNode(),
                        nullNode(),
                        array()
                );
                assertThat(new JdomParser().parse(jsonFormatter.format(arrayNode)), equalTo(arrayNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void sortsObjectFieldsLexicographically(final JsonFormatter jsonFormatter) throws Exception {
                assertThat(new JdomParser().parse(jsonFormatter.format(object(
                        field("c", string("c")),
                        field("b", string("b")),
                        field("a", string("a"))
                ))), equalTo(object(
                        field("a", string("a")),
                        field("b", string("b")),
                        field("c", string("c"))
                )));
            }
        }

        @Nested
        class InsideArray {
            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughStringUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode stringNode = array(aStringNode());
                assertThat(new JdomParser().parse(jsonFormatter.format(stringNode)), equalTo(stringNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNumberUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode numberNode = array(aNumberNode());
                assertThat(new JdomParser().parse(jsonFormatter.format(numberNode)), equalTo(numberNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughTrueUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode trueNode = array(trueNode());
                assertThat(new JdomParser().parse(jsonFormatter.format(trueNode)), equalTo(trueNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughFalseUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode falseNode = array(falseNode());
                assertThat(new JdomParser().parse(jsonFormatter.format(falseNode)), equalTo(falseNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNullUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode nullNode = array(nullNode());
                assertThat(new JdomParser().parse(jsonFormatter.format(nullNode)), equalTo(nullNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughArrayUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode arrayNode = array(array(
                        aStringNode(),
                        aNumberNode(),
                        trueNode(),
                        falseNode(),
                        nullNode(),
                        array()
                ));
                assertThat(new JdomParser().parse(jsonFormatter.format(arrayNode)), equalTo(arrayNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void sortsObjectFieldsLexicographically(final JsonFormatter jsonFormatter) throws Exception {
                assertThat(new JdomParser().parse(jsonFormatter.format(array(object(
                        field("c", string("c")),
                        field("b", string("b")),
                        field("a", string("a"))
                )))), equalTo(array(object(
                        field("a", string("a")),
                        field("b", string("b")),
                        field("c", string("c"))
                ))));
            }
        }

        @Nested
        class InsideObject {
            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughStringUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode stringNode = object(field("foo", aStringNode()));
                assertThat(new JdomParser().parse(jsonFormatter.format(stringNode)), equalTo(stringNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNumberUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode numberNode = object(field("foo", aNumberNode()));
                assertThat(new JdomParser().parse(jsonFormatter.format(numberNode)), equalTo(numberNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughTrueUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode trueNode = object(field("foo", trueNode()));
                assertThat(new JdomParser().parse(jsonFormatter.format(trueNode)), equalTo(trueNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughFalseUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode falseNode = object(field("foo", falseNode()));
                assertThat(new JdomParser().parse(jsonFormatter.format(falseNode)), equalTo(falseNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughNullUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode nullNode = object(field("foo", nullNode()));
                assertThat(new JdomParser().parse(jsonFormatter.format(nullNode)), equalTo(nullNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void passesThroughArrayUnmolested(final JsonFormatter jsonFormatter) throws Exception {
                final JsonNode arrayNode = object(field("foo", array(
                        aStringNode(),
                        aNumberNode(),
                        trueNode(),
                        falseNode(),
                        nullNode(),
                        array()
                )));
                assertThat(new JdomParser().parse(jsonFormatter.format(arrayNode)), equalTo(arrayNode));
            }

            @ParameterizedTest
            @ArgumentsSource(FieldOrderNormalisingJsonFormatterArgumentsProvider.class)
            void sortsObjectFieldsLexicographically(final JsonFormatter jsonFormatter) throws Exception {
                assertThat(new JdomParser().parse(jsonFormatter.format(object(field("foo", object(
                        field("c", string("c")),
                        field("b", string("b")),
                        field("a", string("a"))
                ))))), equalTo(object(field("foo", object(
                        field("a", string("a")),
                        field("b", string("b")),
                        field("c", string("c"))
                )))));
            }
        }

    }

}
