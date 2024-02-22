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

import org.apache.commons.io.output.StringBuilderWriter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static argo.format.JsonStringResultBuilder.aJsonStringResultBuilder;
import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrettyJsonWriterTest {
    @Test
    void canWriteEmptyArray() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, array());
        assertThat(stringBuilderWriter.toString(), equalTo("[]"));
    }

    @Test
    void canWritesEmptyWriteableJsonArray() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> {
        });
        assertThat(stringBuilderWriter.toString(), equalTo("[]"));
    }

    @Test
    void canWritesArrayOfArrays() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonArray) arrayWriter1 -> {
        }));
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t[]")
                .print("]")
                .build()));
    }

    @Test
    void canWritesArrayOfAllElements() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> {
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

    @Test
    void canWriteAnArrayOfObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonObject) objectWriter -> {

        }));
        assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t{}")
                .print("]")
                .build()));
    }

    @Test
    void canWriteEmptyObject() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new PrettyJsonWriter().write(stringWriter, object());
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @Test
    void canWriteEmptyWriteableJsonObject() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new PrettyJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
        });
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @Test
    void canWriteObjectOfStringKeyedObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfJsonStringKeyedObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfJsonFieldObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfStringKeyedStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfStringKeyedWriteableJsonArrays() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfJsonStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteWriteableJsonString() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonString) writer -> writer.write("\"Foo\""));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("\"\\\"Foo\\\"\""));
    }

    @Test
    void canWriteAnArrayOfWriteableJsonString() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonString) writer -> writer.write("\"Foo\"")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t\"\\\"Foo\\\"\"")
                .print("]")
                .build()));
    }

    @Test
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonArrays() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteObjectOfWriteableJsonStringKeyedJsonNodes() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
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

    @Test
    void canWriteWriteableJsonNumber() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonNumber) writer -> writer.write("1234.56e+10"));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("1234.56e+10"));
    }

    @Test
    void rejectsNonIntegerNumberWithNothingBeforeTheDecimalPoint() {
        assertThrows(IllegalArgumentException.class, () -> new PrettyJsonWriter().write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write(".1")));
    }

    @Test
    void rejectsNumberWithDecimalPointButNothingAfter() {
        assertThrows(IllegalStateException.class, () -> new PrettyJsonWriter().write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write("1.")));
    }

    @Test
    void canWriteAnArrayOfWriteableJsonNumber() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonNumber) writer -> writer.write("-123.456E+789")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("[")
                .printLine("\t-123.456E+789")
                .print("]")
                .build()));
    }

    @Test
    void canWriteObjectOfStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                "Foo", (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

    @Test
    void canWriteObjectOfJsonStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                string("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

    @Test
    void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new PrettyJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                writer -> writer.write("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo(aJsonStringResultBuilder()
                .printLine("{")
                .printLine("\t\"Foo\": 1234")
                .print("}")
                .build()));
    }

}