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
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrettyJsonWriterTest {

    @SuppressWarnings("deprecation")
    static final class JsonGeneratorJsonWriterShimArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.concat(
                    Stream.of(
                            new PrettyJsonWriter(),
                            new JsonGeneratorJsonWriterAdapter(new JsonGenerator()),
                            new JsonGeneratorJsonWriterAdapter(new JsonGenerator().style(PRETTY))
                    ).map(WriterJsonGeneratorJsonWriterTestCase::new), Stream.of(
                            new StringJsonGeneratorJsonWriterTestCase(new JsonGenerator()),
                            new StringJsonGeneratorJsonWriterTestCase(new JsonGenerator().style(PRETTY))
                    )).map(Arguments::arguments);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyArray(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write(array()), equalTo("[]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesEmptyWriteableJsonArray(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> {
        }), equalTo("[]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesArrayOfArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonArray) arrayWriter1 -> {
        })), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t[]")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesArrayOfAllElements(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement(array());
            arrayWriter.writeElement(object());
            arrayWriter.writeElement(string("foo"));
            arrayWriter.writeElement(number("12345.67890"));
            arrayWriter.writeElement(nullNode());
            arrayWriter.writeElement(trueNode());
            arrayWriter.writeElement(falseNode());
        }), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t[],")
                .printLine("\t{},")
                .printLine("\t\"foo\",")
                .printLine("\t12345.67890,")
                .printLine("\tnull,")
                .printLine("\ttrue,")
                .printLine("\tfalse")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonObject) objectWriter -> {

        })), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t{}")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyObject(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write(object()), equalTo("{}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyWriteableJsonObject(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
        }), equalTo("{}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonObject) objectWriter1 -> {
            });
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonFieldObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(field("Foo", object()));
            objectWriter.writeField(field("Bar", object()));
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", string("Bar"));
            objectWriter.writeField("Baz", string("Qux"));
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": \"Bar\",")
                .printLine("\t\"Baz\": \"Qux\"")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonArray) arrayWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonArray) arrayWriter1 -> {
            });
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": [],")
                .printLine("\t\"Bar\": []")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonArray) arrayWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonArray) arrayWriter1 -> {
            });
        }), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": [],")
                .printLine("\t\"Bar\": []")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonString) writer -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonString) writer -> {
            });
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonString) writer -> {
            });
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonString(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonString) writer -> writer.write("\"Foo\""));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfWriteableJsonString(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonString) writer -> writer.write("\"Foo\"")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonArray) writeableJsonArray -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonArray) writeableJsonArray -> {
            });
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonString) writer -> {
            });
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedJsonNodes(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), trueNode());
            objectWriter.writeField(writer -> writer.write("Bar"), falseNode());
        });
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonNumber(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) writer -> writer.write("1234.56e+10"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void rejectsNonIntegerNumberWithNothingBeforeTheDecimalPoint(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) {
        assertThrows(IllegalArgumentException.class, () -> jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) writer -> writer.write(".1")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void rejectsNumberWithDecimalPointButNothingAfter(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) {
        assertThrows(IllegalStateException.class, () -> jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) writer -> writer.write("1.")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfWriteableJsonNumber(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonNumber) writer -> writer.write("-123.456E+789")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                "Foo", (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                string("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                writer -> writer.write("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
    }

}