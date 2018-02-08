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

import org.apache.commons.io.output.StringBuilderWriter;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.StringWriter;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CompactJsonWriterTest {
    @Test
    public void canWriteEmptyArray() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, array());
        assertThat(stringWriter.toString(), equalTo("[]"));
    }

    @Test
    public void canWriteEmptyWriteableJsonArray() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> {
        });
        assertThat(stringWriter.toString(), equalTo("[]"));
    }

    @Test
    public void canWriteArrayOfArrays() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonArray) arrayWriter1 -> {

        }));
        assertThat(stringWriter.toString(), equalTo("[[]]"));
    }

    @Test
    public void canWriteAnArrayOfObjects() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonObject) objectWriter -> {

        }));
        assertThat(stringWriter.toString(), equalTo("[{}]"));
    }

    @Test
    public void canWriteAnArrayOfConstants() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement(trueNode());
            arrayWriter.writeElement(falseNode());
            arrayWriter.writeElement(nullNode());
        });
        assertThat(stringWriter.toString(), equalTo("[true,false,null]"));
    }

    @Test
    public void canWriteAnArrayOfString() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement(string("foo")));
        assertThat(stringWriter.toString(), equalTo("[\"foo\"]"));
    }

    @Test
    public void canWriteAnArrayOfNumber() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement(number("123.456E789")));
        assertThat(stringWriter.toString(), equalTo("[123.456E789]"));
    }

    @Test
    public void canWriteEmptyObject() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, object());
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @Test
    public void canWriteEmptyWriteableJsonObject() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
        });
        assertThat(stringWriter.toString(), equalTo("{}"));
    }

    @Test
    public void canWriteObjectOfStringKeyedObjects() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonObject) objectWriter1 -> {
            });
        });
        assertThat(stringWriter.toString(), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @Test
    public void canWriteObjectOfJsonStringKeyedObjects() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
        assertThat(stringWriter.toString(), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @Test
    public void canWriteObjectOfJsonFieldObjects() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        new CompactJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
        assertThat(stringWriter.toString(), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @Test
    public void canWriteObjectOfStringKeyedStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", string("Bar"));
            objectWriter.writeField("Baz", string("Qux"));
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":\"Bar\",\"Baz\":\"Qux\"}"));
    }

    @Test
    public void canWriteObjectOfStringKeyedWriteableJsonArrays() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonArray) arrayWriter1 -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonArray) arrayWriter1 -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":[],\"Bar\":[]}"));
    }

    @Test
    public void canWriteObjectOfStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("Foo", (WriteableJsonString) writer -> {
            });
            objectWriter.writeField("Bar", (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @Test
    public void canWriteObjectOfJsonStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(string("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(string("Bar"), (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @Test
    public void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonObjects() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonObject) objectWriter1 -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonObject) objectWriter1 -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":{},\"Bar\":{}}"));
    }

    @Test
    public void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonArrays() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonArray) writeableJsonArray -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonArray) writeableJsonArray -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":[],\"Bar\":[]}"));
    }

    @Test
    public void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonStrings() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), (WriteableJsonString) writer -> {
            });
            objectWriter.writeField(writer -> writer.write("Bar"), (WriteableJsonString) writer -> {
            });
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":\"\",\"Bar\":\"\"}"));
    }

    @Test
    public void canWriteObjectOfWriteableJsonStringKeyedJsonNodes() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField(writer -> writer.write("Foo"), trueNode());
            objectWriter.writeField(writer -> writer.write("Bar"), falseNode());
        });
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":true,\"Bar\":false}"));
    }

    @Test
    public void canWriteWriteableJsonString() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonString) writer -> writer.write("\"Foo\""));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("\"\\\"Foo\\\"\""));
    }

    @Test
    public void canWriteAnArrayOfWriteableJsonString() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonString) writer -> writer.write("\"Foo\"")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("[\"\\\"Foo\\\"\"]"));
    }

    @Test
    public void canWriteWriteableJsonNumber() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonNumber) writer -> writer.write("1234.56e+10"));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("1234.56e+10"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonIntegerNumberWithNothingBeforeTheDecimalPoint() throws Exception {
        new CompactJsonWriter().write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write(".1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNumberWithDecimalPointButNothingAfter() throws Exception {
        new CompactJsonWriter().write(new StringBuilderWriter(), (WriteableJsonNumber) writer -> writer.write("1."));
    }

    @Test
    public void canWriteAnArrayOfWriteableJsonNumber() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement((WriteableJsonNumber) writer -> writer.write("-123.456E+789")));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("[-123.456E+789]"));
    }

    @Test
    public void canWriteObjectOfStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                "Foo", (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":1234}"));
    }

    @Test
    public void canWriteObjectOfJsonStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                string("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":1234}"));
    }


    @Test
    public void canWriteObjectOfWriteableJsonStringKeyedWriteableJsonNumberFields() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        new CompactJsonWriter().write(stringBuilderWriter, (WriteableJsonObject) objectWriter -> objectWriter.writeField(
                writer -> writer.write("Foo"), (WriteableJsonNumber) numberWriter -> numberWriter.write("1234")
        ));
        MatcherAssert.assertThat(stringBuilderWriter.toString(), equalTo("{\"Foo\":1234}"));
    }

}