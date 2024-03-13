/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CharacterUtilitiesTest {

    @ParameterizedTest
    @ValueSource(chars = {
            ' ',
            '!',
            '"',
            '#',
            '$',
            '%',
            '&',
            '\'',
            '(',
            ')',
            '*',
            '+',
            ',',
            '-',
            '.',
            '/',
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            ':',
            ';',
            '<',
            '=',
            '>',
            '?',
            '@',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            '[',
            '\\',
            ']',
            '^',
            '_',
            '`',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z',
            '{',
            '|',
            '}',
            '~',
    })
    void convertsPrintableCharacterToItself(final char character) {
        assertThat(CharacterUtilities.asPrintableString(character), equalTo(Character.toString(character)));
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
            "0x0008,\\u0008",
            "0x0009,\\u0009",
            "0x000a,\\u000A",
            "0x000b,\\u000B",
            "0x000c,\\u000C",
            "0x000d,\\u000D",
            "0x000e,\\u000E",
            "0x000f,\\u000F",
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
            "0x001a,\\u001A",
            "0x001b,\\u001B",
            "0x001c,\\u001C",
            "0x001d,\\u001D",
            "0x001e,\\u001E",
            "0x001f,\\u001F",

            "0x007f,\\u007F",

            "0xd800,\\uD800",
            "0xdfff,\\uDFFF",
            "0xffff,\\uFFFF",
    })
    void convertsNonPrintableCharacterToItsUnicodeEscapedRepresentation(final int character, final String expectedRepresentation) {
        assertThat(CharacterUtilities.asPrintableString((char)character), equalTo(expectedRepresentation));
    }

    @Test
    void convertsCharArrayLimitedByLength() {
        assertThat(CharacterUtilities.asPrintableString(new char[]{'F', 'o', 'o'}, 2), equalTo("[F, o]"));
    }

    @Test
    void convertsEntireCharArrayWhenLengthIsGreaterThanArrayLength() {
        assertThat(CharacterUtilities.asPrintableString(new char[]{'F', 'o', 'o'}, 10), equalTo("[F, o, o]"));
    }

    @Test
    void convertsCharArrayOfNonPrintableCharacters() {
        assertThat(CharacterUtilities.asPrintableString(new char[]{'\ud800', '\udfff', '\uffff'}, 3), equalTo("[\\uD800, \\uDFFF, \\uFFFF]"));
    }

    @Test
    void convertsCharArrayOfMixedPrintableAndNonPrintableCharacters() {
        assertThat(CharacterUtilities.asPrintableString(new char[]{'F', '\u0000', '\u0000'}, 3), equalTo("[F, \\u0000, \\u0000]"));
    }
}