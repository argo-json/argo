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

import org.apache.commons.io.output.NullWriter;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonEscapedStringTest {

    @Test
    void passesThroughUncontentiousCharactersVerbatim() throws Exception {
        final String uncontentiousCharacters = "abcdefghijklmnopqrstuvwxyz";
        assertThat(escapeString(uncontentiousCharacters), equalTo(uncontentiousCharacters));
    }

    @Test
    void formatsReverseSolidusAsEscapedReverseSolidus() throws Exception {
        assertThat(escapeString("\\"), equalTo("\\\\"));
    }

    @Test
    void formatsDoubleQuoteAsEscapedDoubleQuote() throws Exception {
        assertThat(escapeString("\""), equalTo("\\\""));
    }

    @ParameterizedTest
    @CsvSource({
            "0x0000,\\u0000",
            "0x0001,\\u0001",
            "0x0002,\\u0002",
            "0x0003,\\u0003",
            "0x0004,\\u0004",
            "0x0005,\\u0005",
            "0x0006,\\u0006",
            "0x0007,\\u0007",
            "0x0008,\\b",
            "0x0009,\\t",
            "0x000a,\\n",
            "0x000b,\\u000b",
            "0x000c,\\f",
            "0x000d,\\r",
            "0x000e,\\u000e",
            "0x000f,\\u000f",
            "0x0010,\\u0010",
            "0x0011,\\u0011",
            "0x0012,\\u0012",
            "0x0013,\\u0013",
            "0x0014,\\u0014",
            "0x0015,\\u0015",
            "0x0016,\\u0016",
            "0x0017,\\u0017",
            "0x0018,\\u0018",
            "0x0019,\\u0019",
            "0x001a,\\u001a",
            "0x001b,\\u001b",
            "0x001c,\\u001c",
            "0x001d,\\u001d",
            "0x001e,\\u001e",
            "0x001f,\\u001f",
    })
    void formatsControlCharactersAsEscapedUnicodeCharacters(final int character, final String expectedRepresentation) throws Exception {
        assertThat(escapeString(String.valueOf((char) character)), equalTo(expectedRepresentation));
    }

    @Test
    void formatsACharArray() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, new char[] {'a', 'b', 'c'}, 0, 3);
        assertThat(stringBuilderWriter.toString(), equalTo("abc"));
    }

    @Test
    void formatsACharArrayWithOffset() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, new char[] {'a', 'b', 'c'}, 1, 2);
        assertThat(stringBuilderWriter.toString(), equalTo("bc"));
    }

    @Test
    void formatsACharArrayWithLength() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, new char[] {'a', 'b', 'c'}, 0, 2);
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    void formatsACharArrayWithOffsetAndLength() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, new char[] {'a', 'b', 'c'}, 1, 1);
        assertThat(stringBuilderWriter.toString(), equalTo("b"));
    }


    @Test
    void rejectsNegativeOffset() {
        assertThrows(IndexOutOfBoundsException.class, () -> JsonEscapedString.escapeStringTo(NullWriter.INSTANCE, new char[] {'a', 'b', 'c'}, -1, 3));
    }

    @Test
    void rejectsOffsetGreaterThanArrayLength() {
        assertThrows(IndexOutOfBoundsException.class, () -> JsonEscapedString.escapeStringTo(NullWriter.INSTANCE, new char[] {'a', 'b', 'c'}, 4, 3));
    }

    @Test
    void rejectsNegativeLength() {
        assertThrows(IndexOutOfBoundsException.class, () -> JsonEscapedString.escapeStringTo(NullWriter.INSTANCE, new char[] {'a', 'b', 'c'}, 0, -1));
    }

    @Test
    void rejectsLengthGreaterThanArrayLength() {
        assertThrows(IndexOutOfBoundsException.class, () -> JsonEscapedString.escapeStringTo(NullWriter.INSTANCE, new char[] {'a', 'b', 'c'}, 0, 4));
    }

    @Test
    void rejectsOffsetPlusLengthGreaterThanArrayLength() {
        assertThrows(IndexOutOfBoundsException.class, () -> JsonEscapedString.escapeStringTo(NullWriter.INSTANCE, new char[] {'a', 'b', 'c'}, 2, 2));
    }

    private static String escapeString(final String unescapedString) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, unescapedString);
        return stringBuilderWriter.toString();
    }
}
