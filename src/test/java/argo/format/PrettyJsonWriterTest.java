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
import org.apache.commons.io.output.StringBuilderWriter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.StringWriter;
import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrettyJsonWriterTest {

    static final class JsonGeneratorJsonWriterShimArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new PrettyJsonWriter(),
                    new JsonGeneratorJsonWriterAdapter(new JsonGenerator()),
                    new JsonGeneratorJsonWriterAdapter(new JsonGenerator().style(PRETTY))
            ).map(Arguments::arguments);
        }

    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyArray(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, array());
        assertThat(stringBuilderWriter.toString(), equalTo("[]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesEmptyWriteableJsonArray(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> {
        });
        assertThat(stringBuilderWriter.toString(), equalTo("[]"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesArrayOfArrays(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonArray) arrayWriter1 -> {
        }));
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t[]")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWritesArrayOfAllElements(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement(array());
            arrayWriter.writeElement(object());
            arrayWriter.writeElement(string("foo"));
            arrayWriter.writeElement(number("12345.67890"));
            arrayWriter.writeElement(nullNode());
            arrayWriter.writeElement(trueNode());
            arrayWriter.writeElement(falseNode());
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
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
    void canWriteAnArrayOfObjects(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonObject) objectWriter -> {

        }));
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t{}")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyObject(final JsonWriter jsonWriter) throws Exception {
        final StringWriter stringWriter = new StringWriter();
        jsonWriter.write(stringWriter, object());
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteEmptyWriteableJsonObject(final JsonWriter jsonWriter) throws Exception {
        final StringWriter stringWriter = new StringWriter();
        jsonWriter.write(stringWriter, (WriteableJsonObject) objectWriter -> {
        });
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedObjects(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonObject) objectWriter1 -> {
            });
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedObjects(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonFieldObjects(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(field("Foo", object()));
            objectWriter.writeField(field("Bar", object()));
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedStrings(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", string("Bar"));
            objectWriter.writeField("Baz", string("Qux"));
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": \"Bar\",")
                .printLine("\t\"Baz\": \"Qux\"")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonArrays(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonArray) arrayWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonArray) arrayWriter1 -> {
            });
        });
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": [],")
                .printLine("\t\"Bar\": []")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonStrings(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonString) writer -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": \"\",")
                .printLine("\t\"Bar\": \"\"")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonStrings(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": \"\",")
                .printLine("\t\"Bar\": \"\"")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonString(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonString) writer -> writer.write("\"Foo\""));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("\"\\\"Foo\\\"\""));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfWriteableJsonString(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonString) writer -> writer.write("\"Foo\"")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t\"\\\"Foo\\\"\"")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonObjects(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": {},")
                .printLine("\t\"Bar\": {}")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonArrays(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonArray) writeableJsonArray -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonArray) writeableJsonArray -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": [],")
                .printLine("\t\"Bar\": []")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonStrings(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": \"\",")
                .printLine("\t\"Bar\": \"\"")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedJsonNodes(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), trueNode());
            objectWriter.writeField(writer -> writer.write("Bar"), falseNode());
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": true,")
                .printLine("\t\"Bar\": false")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteWriteableJsonNumber(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonNumber) writer -> writer.write("1234.56e+10"));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("1234.56e+10"));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void rejectsNonIntegerNumberWithNothingBeforeTheDecimalPoint(final JsonWriter jsonWriter) {
        assertThrows(IllegalArgumentException.class, () -> jsonWriter.write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write(".1")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void rejectsNumberWithDecimalPointButNothingAfter(final JsonWriter jsonWriter) {
        assertThrows(IllegalStateException.class, () -> jsonWriter.write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write("1.")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteAnArrayOfWriteableJsonNumber(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonNumber) writer -> writer.write("-123.456E+789")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t-123.456E+789")
                .print("]")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfStringKeyedWriteableJsonNumberFields(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                "Foo", (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfJsonStringKeyedWriteableJsonNumberFields(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                string("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonNumberFields(final JsonWriter jsonWriter) throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        jsonWriter.write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                writer -> writer.write("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

}