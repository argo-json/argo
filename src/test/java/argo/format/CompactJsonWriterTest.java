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

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompactJsonWriterTest {

    @SuppressWarnings("deprecation")
    static final class JsonGeneratorJsonWriterShimArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.concat(
                    Stream.of(
                            new CompactJsonWriter(),
                            new JsonGeneratorJsonWriterAdapter(new JsonGenerator().style(COMPACT))
                    ).map(WriterJsonGeneratorJsonWriterTestCase::new), Stream.of(
                            new StringJsonGeneratorJsonWriterTestCase(new JsonGenerator().style(COMPACT))
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
    void canWriteEmptyWriteableJsonArray(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> {
        }), equalTo("[]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteArrayOfArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonArray) arrayWriter1 -> {

        })), equalTo("[[]]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonObject) objectWriter -> {

        })), equalTo("[{}]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfConstants(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement(trueNode());
            arrayWriter.writeElement(falseNode());
            arrayWriter.writeElement(nullNode());
        }), equalTo("[true,false,null]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfString(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement(string("foo"))), equalTo("[\"foo\"]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfNumber(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement(number("123.456E789"))), equalTo("[123.456E789]"));
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
        }), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        }), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonFieldObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(field("Foo", object()));
            objectWriter.writeField(field("Bar", object()));
        }), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", string("Bar"));
            objectWriter.writeField("Baz", string("Qux"));
        }), equalTo("{\"Foo\":\"Bar\",\"Baz\":\"Qux\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonArray) arrayWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonArray) arrayWriter1 -> {
            });
        }), equalTo("{\"Foo\":[],\"Bar\":[]}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonString) writer -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonString) writer -> {
            });
        }), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonString) writer -> {
            });
        }), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonObjects(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        }), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonArrays(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonArray) writeableJsonArray -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonArray) writeableJsonArray -> {
            });
        }), equalTo("{\"Foo\":[],\"Bar\":[]}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonStrings(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonString) writer -> {
            });
        }), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedJsonNodes(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), trueNode());
            objectWriter.writeField(writer -> writer.write("Bar"), falseNode());
        }), equalTo("{\"Foo\":true,\"Bar\":false}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonString(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonString) writer -> writer.write("\"Foo\"")), equalTo("\"\\\"Foo\\\"\""));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfWriteableJsonString(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonString) writer -> writer.write("\"Foo\""))), equalTo("[\"\\\"Foo\\\"\"]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonNumber(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) writer -> writer.write("1234.56e+10")), equalTo("1234.56e+10"));
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
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonNumber) writer -> writer.write("-123.456E+789"))), equalTo("[-123.456E+789]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                "Foo", (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        )), equalTo("{\"Foo\":1234}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                string("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        )), equalTo("{\"Foo\":1234}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonNumberFields(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        assertThat(jsonGeneratorJsonWriterTestCase.write((WriteableJsonObject) objectWriter -> objectWriter.writeField(
                writer -> writer.write("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        )), equalTo("{\"Foo\":1234}"));
    }

}