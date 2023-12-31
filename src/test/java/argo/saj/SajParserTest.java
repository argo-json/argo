/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import org.apache.commons.io.input.BrokenReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;

import static argo.saj.BlackHoleJsonListener.BLACK_HOLE_JSON_LISTENER;
import static argo.saj.InvalidSyntaxExceptionMatcher.anInvalidSyntaxExceptionAtPosition;
import static argo.saj.RecordingJsonListener.*;
import static argo.saj.RecordingJsonListener.NumberValue.numberValue;
import static argo.saj.RecordingJsonListener.StartField.startField;
import static argo.saj.RecordingJsonListener.StringValue.stringValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SajParserTest {

    @Test
    void tokenizesValidString() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("\"hello world\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesValidStringWithEscapedChars() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("\"\\\"hello world\\\"\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("\"hello world\""),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesValidStringWithEscapedUnicodeChars() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("\"hello world \\uF001\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world \uF001"),
                END_DOCUMENT
        ));
    }

    @Test
    void rejectsStringWithInvalidEscapedUnicodeChars() {
        final String inputString = "[\"hello world \\uF0\"]";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(16, 1));
    }

    @Test
    void rejectsInvalidString() {
        final String inputString = "[hello world\"]";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(2, 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-0",

            "-0.0",
            "-0.1",
            "-0.2",
            "-0.3",
            "-0.4",
            "-0.5",
            "-0.6",
            "-0.7",
            "-0.8",
            "-0.9",

            "-0.00",
            "-0.11",
            "-0.22",
            "-0.33",
            "-0.44",
            "-0.55",
            "-0.66",
            "-0.77",
            "-0.88",
            "-0.99",

            "-0.0e+0",
            "-0.1e+1",
            "-0.2e+2",
            "-0.3e+3",
            "-0.4e+4",
            "-0.5e+5",
            "-0.6e+6",
            "-0.7e+7",
            "-0.8e+8",
            "-0.9e+9",
            "-0.0E+0",
            "-0.1E+1",
            "-0.2E+2",
            "-0.3E+3",
            "-0.4E+4",
            "-0.5E+5",
            "-0.6E+6",
            "-0.7E+7",
            "-0.8E+8",
            "-0.9E+9",
            "-0.0e+00",
            "-0.1e+11",
            "-0.2e+22",
            "-0.3e+33",
            "-0.4e+44",
            "-0.5e+55",
            "-0.6e+66",
            "-0.7e+77",
            "-0.8e+88",
            "-0.9e+99",
            "-0.0E+0",
            "-0.1E+1",
            "-0.2E+2",
            "-0.3E+3",
            "-0.4E+4",
            "-0.5E+5",
            "-0.6E+6",
            "-0.7E+7",
            "-0.8E+8",
            "-0.9E+9",

            "-0.0e-0",
            "-0.1e-1",
            "-0.2e-2",
            "-0.3e-3",
            "-0.4e-4",
            "-0.5e-5",
            "-0.6e-6",
            "-0.7e-7",
            "-0.8e-8",
            "-0.9e-9",
            "-0.0e-00",
            "-0.1e-11",
            "-0.2e-22",
            "-0.3e-33",
            "-0.4e-44",
            "-0.5e-55",
            "-0.6e-66",
            "-0.7e-77",
            "-0.8e-88",
            "-0.9e-99",
            "-0.0E-0",
            "-0.1E-1",
            "-0.2E-2",
            "-0.3E-3",
            "-0.4E-4",
            "-0.5E-5",
            "-0.6E-6",
            "-0.7E-7",
            "-0.8E-8",
            "-0.9E-9",
            "-0.0E-00",
            "-0.1E-11",
            "-0.2E-22",
            "-0.3E-33",
            "-0.4E-44",
            "-0.5E-55",
            "-0.6E-66",
            "-0.7E-77",
            "-0.8E-88",
            "-0.9E-99",

            "-0.0e0",
            "-0.1e1",
            "-0.2e2",
            "-0.3e3",
            "-0.4e4",
            "-0.5e5",
            "-0.6e6",
            "-0.7e7",
            "-0.8e8",
            "-0.9e9",
            "-0.0e00",
            "-0.1e11",
            "-0.2e22",
            "-0.3e33",
            "-0.4e44",
            "-0.5e55",
            "-0.6e66",
            "-0.7e77",
            "-0.8e88",
            "-0.9e99",
            "-0.0E0",
            "-0.1E1",
            "-0.2E2",
            "-0.3E3",
            "-0.4E4",
            "-0.5E5",
            "-0.6E6",
            "-0.7E7",
            "-0.8E8",
            "-0.9E9",
            "-0.0E00",
            "-0.1E11",
            "-0.2E22",
            "-0.3E33",
            "-0.4E44",
            "-0.5E55",
            "-0.6E66",
            "-0.7E77",
            "-0.8E88",
            "-0.9E99",

            "-1.0",
            "-1.1",
            "-1.2",
            "-1.3",
            "-1.4",
            "-1.5",
            "-1.6",
            "-1.7",
            "-1.8",
            "-1.9",

            "-1.00",
            "-1.11",
            "-1.22",
            "-1.33",
            "-1.44",
            "-1.55",
            "-1.66",
            "-1.77",
            "-1.88",
            "-1.99",

            "-1.0e+0",
            "-1.1e+1",
            "-1.2e+2",
            "-1.3e+3",
            "-1.4e+4",
            "-1.5e+5",
            "-1.6e+6",
            "-1.7e+7",
            "-1.8e+8",
            "-1.9e+9",
            "-1.0E+0",
            "-1.1E+1",
            "-1.2E+2",
            "-1.3E+3",
            "-1.4E+4",
            "-1.5E+5",
            "-1.6E+6",
            "-1.7E+7",
            "-1.8E+8",
            "-1.9E+9",
            "-1.0e+00",
            "-1.1e+11",
            "-1.2e+22",
            "-1.3e+33",
            "-1.4e+44",
            "-1.5e+55",
            "-1.6e+66",
            "-1.7e+77",
            "-1.8e+88",
            "-1.9e+99",
            "-1.0E+0",
            "-1.1E+1",
            "-1.2E+2",
            "-1.3E+3",
            "-1.4E+4",
            "-1.5E+5",
            "-1.6E+6",
            "-1.7E+7",
            "-1.8E+8",
            "-1.9E+9",

            "-1.0e-0",
            "-1.1e-1",
            "-1.2e-2",
            "-1.3e-3",
            "-1.4e-4",
            "-1.5e-5",
            "-1.6e-6",
            "-1.7e-7",
            "-1.8e-8",
            "-1.9e-9",
            "-1.0e-00",
            "-1.1e-11",
            "-1.2e-22",
            "-1.3e-33",
            "-1.4e-44",
            "-1.5e-55",
            "-1.6e-66",
            "-1.7e-77",
            "-1.8e-88",
            "-1.9e-99",
            "-1.0E-0",
            "-1.1E-1",
            "-1.2E-2",
            "-1.3E-3",
            "-1.4E-4",
            "-1.5E-5",
            "-1.6E-6",
            "-1.7E-7",
            "-1.8E-8",
            "-1.9E-9",
            "-1.0E-00",
            "-1.1E-11",
            "-1.2E-22",
            "-1.3E-33",
            "-1.4E-44",
            "-1.5E-55",
            "-1.6E-66",
            "-1.7E-77",
            "-1.8E-88",
            "-1.9E-99",

            "-1.0e0",
            "-1.1e1",
            "-1.2e2",
            "-1.3e3",
            "-1.4e4",
            "-1.5e5",
            "-1.6e6",
            "-1.7e7",
            "-1.8e8",
            "-1.9e9",
            "-1.0e00",
            "-1.1e11",
            "-1.2e22",
            "-1.3e33",
            "-1.4e44",
            "-1.5e55",
            "-1.6e66",
            "-1.7e77",
            "-1.8e88",
            "-1.9e99",
            "-1.0E0",
            "-1.1E1",
            "-1.2E2",
            "-1.3E3",
            "-1.4E4",
            "-1.5E5",
            "-1.6E6",
            "-1.7E7",
            "-1.8E8",
            "-1.9E9",
            "-1.0E00",
            "-1.1E11",
            "-1.2E22",
            "-1.3E33",
            "-1.4E44",
            "-1.5E55",
            "-1.6E66",
            "-1.7E77",
            "-1.8E88",
            "-1.9E99",

            "-0",
            "-1",
            "-2",
            "-3",
            "-4",
            "-5",
            "-6",
            "-7",
            "-8",
            "-9",

            "-11",
            "-22",
            "-33",
            "-44",
            "-55",
            "-66",
            "-77",
            "-88",
            "-99",

            "-0e+0",
            "-1e+1",
            "-2e+2",
            "-3e+3",
            "-4e+4",
            "-5e+5",
            "-6e+6",
            "-7e+7",
            "-8e+8",
            "-9e+9",
            "-0E+0",
            "-1E+1",
            "-2E+2",
            "-3E+3",
            "-4E+4",
            "-5E+5",
            "-6E+6",
            "-7E+7",
            "-8E+8",
            "-9E+9",
            "-0e+00",
            "-1e+11",
            "-2e+22",
            "-3e+33",
            "-4e+44",
            "-5e+55",
            "-6e+66",
            "-7e+77",
            "-8e+88",
            "-9e+99",
            "-0E+0",
            "-1E+1",
            "-2E+2",
            "-3E+3",
            "-4E+4",
            "-5E+5",
            "-6E+6",
            "-7E+7",
            "-8E+8",
            "-9E+9",

            "-0e-0",
            "-1e-1",
            "-2e-2",
            "-3e-3",
            "-4e-4",
            "-5e-5",
            "-6e-6",
            "-7e-7",
            "-8e-8",
            "-9e-9",
            "-0e-00",
            "-1e-11",
            "-2e-22",
            "-3e-33",
            "-4e-44",
            "-5e-55",
            "-6e-66",
            "-7e-77",
            "-8e-88",
            "-9e-99",
            "-0E-0",
            "-1E-1",
            "-2E-2",
            "-3E-3",
            "-4E-4",
            "-5E-5",
            "-6E-6",
            "-7E-7",
            "-8E-8",
            "-9E-9",
            "-0E-00",
            "-1E-11",
            "-2E-22",
            "-3E-33",
            "-4E-44",
            "-5E-55",
            "-6E-66",
            "-7E-77",
            "-8E-88",
            "-9E-99",

            "-0e0",
            "-1e1",
            "-2e2",
            "-3e3",
            "-4e4",
            "-5e5",
            "-6e6",
            "-7e7",
            "-8e8",
            "-9e9",
            "-0e00",
            "-1e11",
            "-2e22",
            "-3e33",
            "-4e44",
            "-5e55",
            "-6e66",
            "-7e77",
            "-8e88",
            "-9e99",
            "-0E0",
            "-1E1",
            "-2E2",
            "-3E3",
            "-4E4",
            "-5E5",
            "-6E6",
            "-7E7",
            "-8E8",
            "-9E9",
            "-0E00",
            "-1E11",
            "-2E22",
            "-3E33",
            "-4E44",
            "-5E55",
            "-6E66",
            "-7E77",
            "-8E88",
            "-9E99",

            "0",

            "0.0",
            "0.1",
            "0.2",
            "0.3",
            "0.4",
            "0.5",
            "0.6",
            "0.7",
            "0.8",
            "0.9",

            "0.00",
            "0.11",
            "0.22",
            "0.33",
            "0.44",
            "0.55",
            "0.66",
            "0.77",
            "0.88",
            "0.99",

            "0.0e+0",
            "0.1e+1",
            "0.2e+2",
            "0.3e+3",
            "0.4e+4",
            "0.5e+5",
            "0.6e+6",
            "0.7e+7",
            "0.8e+8",
            "0.9e+9",
            "0.0E+0",
            "0.1E+1",
            "0.2E+2",
            "0.3E+3",
            "0.4E+4",
            "0.5E+5",
            "0.6E+6",
            "0.7E+7",
            "0.8E+8",
            "0.9E+9",
            "0.0e+00",
            "0.1e+11",
            "0.2e+22",
            "0.3e+33",
            "0.4e+44",
            "0.5e+55",
            "0.6e+66",
            "0.7e+77",
            "0.8e+88",
            "0.9e+99",
            "0.0E+0",
            "0.1E+1",
            "0.2E+2",
            "0.3E+3",
            "0.4E+4",
            "0.5E+5",
            "0.6E+6",
            "0.7E+7",
            "0.8E+8",
            "0.9E+9",

            "0.0e-0",
            "0.1e-1",
            "0.2e-2",
            "0.3e-3",
            "0.4e-4",
            "0.5e-5",
            "0.6e-6",
            "0.7e-7",
            "0.8e-8",
            "0.9e-9",
            "0.0e-00",
            "0.1e-11",
            "0.2e-22",
            "0.3e-33",
            "0.4e-44",
            "0.5e-55",
            "0.6e-66",
            "0.7e-77",
            "0.8e-88",
            "0.9e-99",
            "0.0E-0",
            "0.1E-1",
            "0.2E-2",
            "0.3E-3",
            "0.4E-4",
            "0.5E-5",
            "0.6E-6",
            "0.7E-7",
            "0.8E-8",
            "0.9E-9",
            "0.0E-00",
            "0.1E-11",
            "0.2E-22",
            "0.3E-33",
            "0.4E-44",
            "0.5E-55",
            "0.6E-66",
            "0.7E-77",
            "0.8E-88",
            "0.9E-99",

            "0.0e0",
            "0.1e1",
            "0.2e2",
            "0.3e3",
            "0.4e4",
            "0.5e5",
            "0.6e6",
            "0.7e7",
            "0.8e8",
            "0.9e9",
            "0.0e00",
            "0.1e11",
            "0.2e22",
            "0.3e33",
            "0.4e44",
            "0.5e55",
            "0.6e66",
            "0.7e77",
            "0.8e88",
            "0.9e99",
            "0.0E0",
            "0.1E1",
            "0.2E2",
            "0.3E3",
            "0.4E4",
            "0.5E5",
            "0.6E6",
            "0.7E7",
            "0.8E8",
            "0.9E9",
            "0.0E00",
            "0.1E11",
            "0.2E22",
            "0.3E33",
            "0.4E44",
            "0.5E55",
            "0.6E66",
            "0.7E77",
            "0.8E88",
            "0.9E99",

            "1.0",
            "1.1",
            "1.2",
            "1.3",
            "1.4",
            "1.5",
            "1.6",
            "1.7",
            "1.8",
            "1.9",

            "1.00",
            "1.11",
            "1.22",
            "1.33",
            "1.44",
            "1.55",
            "1.66",
            "1.77",
            "1.88",
            "1.99",

            "1.0e+0",
            "1.1e+1",
            "1.2e+2",
            "1.3e+3",
            "1.4e+4",
            "1.5e+5",
            "1.6e+6",
            "1.7e+7",
            "1.8e+8",
            "1.9e+9",
            "1.0E+0",
            "1.1E+1",
            "1.2E+2",
            "1.3E+3",
            "1.4E+4",
            "1.5E+5",
            "1.6E+6",
            "1.7E+7",
            "1.8E+8",
            "1.9E+9",
            "1.0e+00",
            "1.1e+11",
            "1.2e+22",
            "1.3e+33",
            "1.4e+44",
            "1.5e+55",
            "1.6e+66",
            "1.7e+77",
            "1.8e+88",
            "1.9e+99",
            "1.0E+0",
            "1.1E+1",
            "1.2E+2",
            "1.3E+3",
            "1.4E+4",
            "1.5E+5",
            "1.6E+6",
            "1.7E+7",
            "1.8E+8",
            "1.9E+9",

            "1.0e-0",
            "1.1e-1",
            "1.2e-2",
            "1.3e-3",
            "1.4e-4",
            "1.5e-5",
            "1.6e-6",
            "1.7e-7",
            "1.8e-8",
            "1.9e-9",
            "1.0e-00",
            "1.1e-11",
            "1.2e-22",
            "1.3e-33",
            "1.4e-44",
            "1.5e-55",
            "1.6e-66",
            "1.7e-77",
            "1.8e-88",
            "1.9e-99",
            "1.0E-0",
            "1.1E-1",
            "1.2E-2",
            "1.3E-3",
            "1.4E-4",
            "1.5E-5",
            "1.6E-6",
            "1.7E-7",
            "1.8E-8",
            "1.9E-9",
            "1.0E-00",
            "1.1E-11",
            "1.2E-22",
            "1.3E-33",
            "1.4E-44",
            "1.5E-55",
            "1.6E-66",
            "1.7E-77",
            "1.8E-88",
            "1.9E-99",

            "1.0e0",
            "1.1e1",
            "1.2e2",
            "1.3e3",
            "1.4e4",
            "1.5e5",
            "1.6e6",
            "1.7e7",
            "1.8e8",
            "1.9e9",
            "1.0e00",
            "1.1e11",
            "1.2e22",
            "1.3e33",
            "1.4e44",
            "1.5e55",
            "1.6e66",
            "1.7e77",
            "1.8e88",
            "1.9e99",
            "1.0E0",
            "1.1E1",
            "1.2E2",
            "1.3E3",
            "1.4E4",
            "1.5E5",
            "1.6E6",
            "1.7E7",
            "1.8E8",
            "1.9E9",
            "1.0E00",
            "1.1E11",
            "1.2E22",
            "1.3E33",
            "1.4E44",
            "1.5E55",
            "1.6E66",
            "1.7E77",
            "1.8E88",
            "1.9E99",

            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",

            "11",
            "22",
            "33",
            "44",
            "55",
            "66",
            "77",
            "88",
            "99",

            "0e+0",
            "1e+1",
            "2e+2",
            "3e+3",
            "4e+4",
            "5e+5",
            "6e+6",
            "7e+7",
            "8e+8",
            "9e+9",
            "0E+0",
            "1E+1",
            "2E+2",
            "3E+3",
            "4E+4",
            "5E+5",
            "6E+6",
            "7E+7",
            "8E+8",
            "9E+9",
            "0e+00",
            "1e+11",
            "2e+22",
            "3e+33",
            "4e+44",
            "5e+55",
            "6e+66",
            "7e+77",
            "8e+88",
            "9e+99",
            "0E+0",
            "1E+1",
            "2E+2",
            "3E+3",
            "4E+4",
            "5E+5",
            "6E+6",
            "7E+7",
            "8E+8",
            "9E+9",

            "0e-0",
            "1e-1",
            "2e-2",
            "3e-3",
            "4e-4",
            "5e-5",
            "6e-6",
            "7e-7",
            "8e-8",
            "9e-9",
            "0e-00",
            "1e-11",
            "2e-22",
            "3e-33",
            "4e-44",
            "5e-55",
            "6e-66",
            "7e-77",
            "8e-88",
            "9e-99",
            "0E-0",
            "1E-1",
            "2E-2",
            "3E-3",
            "4E-4",
            "5E-5",
            "6E-6",
            "7E-7",
            "8E-8",
            "9E-9",
            "0E-00",
            "1E-11",
            "2E-22",
            "3E-33",
            "4E-44",
            "5E-55",
            "6E-66",
            "7E-77",
            "8E-88",
            "9E-99",

            "0e0",
            "1e1",
            "2e2",
            "3e3",
            "4e4",
            "5e5",
            "6e6",
            "7e7",
            "8e8",
            "9e9",
            "0e00",
            "1e11",
            "2e22",
            "3e33",
            "4e44",
            "5e55",
            "6e66",
            "7e77",
            "8e88",
            "9e99",
            "0E0",
            "1E1",
            "2E2",
            "3E3",
            "4E4",
            "5E5",
            "6E6",
            "7E7",
            "8E8",
            "9E9",
            "0E00",
            "1E11",
            "2E22",
            "3E33",
            "4E44",
            "5E55",
            "6E66",
            "7E77",
            "8E88",
            "9E99",
    })
    void tokenizesValidNumber(final String numberString) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse(numberString, recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                numberValue(numberString),
                END_DOCUMENT
        ));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "-0.0E+.",
            "-0.0E+.",

            "-0.0e-.",
            "-0.0e-.",
            "-0.0E-.",
            "-0.0E-.",

            "-0.0e.",
            "-0.0e.",
            "-0.0E.",
            "-0.0E.",

            "-0.0e+",
            "-0.1e+",
            "-0.2e+",
            "-0.3e+",
            "-0.4e+",
            "-0.5e+",
            "-0.6e+",
            "-0.7e+",
            "-0.8e+",
            "-0.9e+",
            "-0.0E+",
            "-0.1E+",
            "-0.2E+",
            "-0.3E+",
            "-0.4E+",
            "-0.5E+",
            "-0.6E+",
            "-0.7E+",
            "-0.8E+",
            "-0.9E+",

            "-0.0e-",
            "-0.1e-",
            "-0.2e-",
            "-0.3e-",
            "-0.4e-",
            "-0.5e-",
            "-0.6e-",
            "-0.7e-",
            "-0.8e-",
            "-0.9e-",
            "-0.0E-",
            "-0.1E-",
            "-0.2E-",
            "-0.3E-",
            "-0.4E-",
            "-0.5E-",
            "-0.6E-",
            "-0.7E-",
            "-0.8E-",
            "-0.9E-",

            "-0.0e",
            "-0.1e",
            "-0.2e",
            "-0.3e",
            "-0.4e",
            "-0.5e",
            "-0.6e",
            "-0.7e",
            "-0.8e",
            "-0.9e",
            "-0.0E",
            "-0.1E",
            "-0.2E",
            "-0.3E",
            "-0.4E",
            "-0.5E",
            "-0.6E",
            "-0.7E",
            "-0.8E",
            "-0.9E",

            "-0.",
            "-0..",
            "-0.e",



            "-1.0E+.",
            "-1.0E+.",

            "-1.0e-.",
            "-1.0e-.",
            "-1.0E-.",
            "-1.0E-.",

            "-1.0e.",
            "-1.0e.",
            "-1.0E.",
            "-1.0E.",

            "-1.0e+",
            "-1.1e+",
            "-1.2e+",
            "-1.3e+",
            "-1.4e+",
            "-1.5e+",
            "-1.6e+",
            "-1.7e+",
            "-1.8e+",
            "-1.9e+",
            "-1.0E+",
            "-1.1E+",
            "-1.2E+",
            "-1.3E+",
            "-1.4E+",
            "-1.5E+",
            "-1.6E+",
            "-1.7E+",
            "-1.8E+",
            "-1.9E+",

            "-1.0e-",
            "-1.1e-",
            "-1.2e-",
            "-1.3e-",
            "-1.4e-",
            "-1.5e-",
            "-1.6e-",
            "-1.7e-",
            "-1.8e-",
            "-1.9e-",
            "-1.0E-",
            "-1.1E-",
            "-1.2E-",
            "-1.3E-",
            "-1.4E-",
            "-1.5E-",
            "-1.6E-",
            "-1.7E-",
            "-1.8E-",
            "-1.9E-",

            "-1.0e",
            "-1.1e",
            "-1.2e",
            "-1.3e",
            "-1.4e",
            "-1.5e",
            "-1.6e",
            "-1.7e",
            "-1.8e",
            "-1.9e",
            "-1.0E",
            "-1.1E",
            "-1.2E",
            "-1.3E",
            "-1.4E",
            "-1.5E",
            "-1.6E",
            "-1.7E",
            "-1.8E",
            "-1.9E",

            "-1.",
            "-1..",
            "-1.e",


            "-00",

            "-0E+.",
            "-0E+.",

            "-0e-.",
            "-0e-.",
            "-0E-.",
            "-0E-.",

            "-0e.",
            "-0e.",
            "-0E.",
            "-0E.",

            "-0e+",
            "-1e+",
            "-2e+",
            "-3e+",
            "-4e+",
            "-5e+",
            "-6e+",
            "-7e+",
            "-8e+",
            "-9e+",
            "-0E+",
            "-1E+",
            "-2E+",
            "-3E+",
            "-4E+",
            "-5E+",
            "-6E+",
            "-7E+",
            "-8E+",
            "-9E+",

            "-0e-",
            "-1e-",
            "-2e-",
            "-3e-",
            "-4e-",
            "-5e-",
            "-6e-",
            "-7e-",
            "-8e-",
            "-9e-",
            "-0E-",
            "-1E-",
            "-2E-",
            "-3E-",
            "-4E-",
            "-5E-",
            "-6E-",
            "-7E-",
            "-8E-",
            "-9E-",

            "-0e",
            "-1e",
            "-2e",
            "-3e",
            "-4e",
            "-5e",
            "-6e",
            "-7e",
            "-8e",
            "-9e",
            "-0E",
            "-1E",
            "-2E",
            "-3E",
            "-4E",
            "-5E",
            "-6E",
            "-7E",
            "-8E",
            "-9E",

            "-",
            "-.",
            "-e",

            "0.0E+.",
            "0.0E+.",

            "0.0e-.",
            "0.0e-.",
            "0.0E-.",
            "0.0E-.",

            "0.0e.",
            "0.0e.",
            "0.0E.",
            "0.0E.",

            "0.0e+",
            "0.1e+",
            "0.2e+",
            "0.3e+",
            "0.4e+",
            "0.5e+",
            "0.6e+",
            "0.7e+",
            "0.8e+",
            "0.9e+",
            "0.0E+",
            "0.1E+",
            "0.2E+",
            "0.3E+",
            "0.4E+",
            "0.5E+",
            "0.6E+",
            "0.7E+",
            "0.8E+",
            "0.9E+",

            "0.0e-",
            "0.1e-",
            "0.2e-",
            "0.3e-",
            "0.4e-",
            "0.5e-",
            "0.6e-",
            "0.7e-",
            "0.8e-",
            "0.9e-",
            "0.0E-",
            "0.1E-",
            "0.2E-",
            "0.3E-",
            "0.4E-",
            "0.5E-",
            "0.6E-",
            "0.7E-",
            "0.8E-",
            "0.9E-",

            "0.0e",
            "0.1e",
            "0.2e",
            "0.3e",
            "0.4e",
            "0.5e",
            "0.6e",
            "0.7e",
            "0.8e",
            "0.9e",
            "0.0E",
            "0.1E",
            "0.2E",
            "0.3E",
            "0.4E",
            "0.5E",
            "0.6E",
            "0.7E",
            "0.8E",
            "0.9E",

            "0.",
            "0..",
            "0.e",



            "1.0E+.",
            "1.0E+.",

            "1.0e-.",
            "1.0e-.",
            "1.0E-.",
            "1.0E-.",

            "1.0e.",
            "1.0e.",
            "1.0E.",
            "1.0E.",

            "1.0e+",
            "1.1e+",
            "1.2e+",
            "1.3e+",
            "1.4e+",
            "1.5e+",
            "1.6e+",
            "1.7e+",
            "1.8e+",
            "1.9e+",
            "1.0E+",
            "1.1E+",
            "1.2E+",
            "1.3E+",
            "1.4E+",
            "1.5E+",
            "1.6E+",
            "1.7E+",
            "1.8E+",
            "1.9E+",

            "1.0e-",
            "1.1e-",
            "1.2e-",
            "1.3e-",
            "1.4e-",
            "1.5e-",
            "1.6e-",
            "1.7e-",
            "1.8e-",
            "1.9e-",
            "1.0E-",
            "1.1E-",
            "1.2E-",
            "1.3E-",
            "1.4E-",
            "1.5E-",
            "1.6E-",
            "1.7E-",
            "1.8E-",
            "1.9E-",

            "1.0e",
            "1.1e",
            "1.2e",
            "1.3e",
            "1.4e",
            "1.5e",
            "1.6e",
            "1.7e",
            "1.8e",
            "1.9e",
            "1.0E",
            "1.1E",
            "1.2E",
            "1.3E",
            "1.4E",
            "1.5E",
            "1.6E",
            "1.7E",
            "1.8E",
            "1.9E",

            "1.",
            "1..",
            "1.e",


            "00",

            "0E+.",
            "0E+.",

            "0e-.",
            "0e-.",
            "0E-.",
            "0E-.",

            "0e.",
            "0e.",
            "0E.",
            "0E.",

            "0e+",
            "1e+",
            "2e+",
            "3e+",
            "4e+",
            "5e+",
            "6e+",
            "7e+",
            "8e+",
            "9e+",
            "0E+",
            "1E+",
            "2E+",
            "3E+",
            "4E+",
            "5E+",
            "6E+",
            "7E+",
            "8E+",
            "9E+",

            "0e-",
            "1e-",
            "2e-",
            "3e-",
            "4e-",
            "5e-",
            "6e-",
            "7e-",
            "8e-",
            "9e-",
            "0E-",
            "1E-",
            "2E-",
            "3E-",
            "4E-",
            "5E-",
            "6E-",
            "7E-",
            "8E-",
            "9E-",

            "0e",
            "1e",
            "2e",
            "3e",
            "4e",
            "5e",
            "6e",
            "7e",
            "8e",
            "9e",
            "0E",
            "1E",
            "2E",
            "3E",
            "4E",
            "5E",
            "6E",
            "7E",
            "8E",
            "9E",

            "",
            ".",
            "e",
    })
    void handlesInvalidNumber(final String numberString) {
        final SajParser sajParser = new SajParser();
        assertThrows(InvalidSyntaxException.class, () -> sajParser.parse(numberString, BLACK_HOLE_JSON_LISTENER));
        // TODO assert message is correct
    }

    @Test
    void tokenizesJsonStringObjectFromReader() throws Exception {
        RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse(new StringReader("\"hello world\""), recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonStringObjectFromString() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("\"hello world\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonObjectWithWhitespace() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"hello\": \"world\"}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("hello"),
                stringValue("world"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesMultiElementArrayWithWhitespace() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[ 1, 2 ]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                numberValue("1"),
                numberValue("2"),
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesMultiFieldObjectWithStringValues() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"hello\":\"world\",\"foo\":\"bar\"}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("hello"),
                stringValue("world"),
                END_FIELD,
                startField("foo"),
                stringValue("bar"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesObjectWithNumberValue() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"room\":101}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("room"),
                numberValue("101"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonNull() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("null", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonTrue() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("true", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                TRUE_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonFalse() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("false", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                FALSE_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesMultiFieldObjectWithNumberValues() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"room\":101,\"answer\":42}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("room"),
                numberValue("101"),
                END_FIELD,
                startField("answer"),
                numberValue("42"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesMultiFieldObjectWithMixedValueTypes() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"room\":101,\"foo\":\"bar\"}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("room"),
                numberValue("101"),
                END_FIELD,
                startField("foo"),
                stringValue("bar"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesEmptyObject() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesNestedObject() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"Test\":{\"Inner test\":12}}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("Test"),
                START_OBJECT,
                startField("Inner test"),
                numberValue("12"),
                END_FIELD,
                END_OBJECT,
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesEmptyArray() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesSingleElementArray() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[12]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                numberValue("12"),
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesMultiElementArray() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[12,\"test\"]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                numberValue("12"),
                stringValue("test"),
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesNestedArray() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[[12]]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                START_ARRAY,
                numberValue("12"),
                END_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesObjectWithArrayValue() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"Test\":[12]}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("Test"),
                START_ARRAY,
                numberValue("12"),
                END_ARRAY,
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesArrayWithObjectElement() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[{\"Test\":12}]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                START_OBJECT,
                startField("Test"),
                numberValue("12"),
                END_FIELD,
                END_OBJECT,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void rejectsEmptyDocument() {
        final String inputString = "";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(1, 1));
    }


    @Test
    void rejectsLeadingNonWhitespaceCharacters() {
        final String inputString = "whoops[]";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(1, 1));
    }

    @Test
    void rejectsTrailingNonWhitespaceCharacters() {
        final String inputString = "[]whoops";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(3, 1));
    }

    @Test
    void rejectsMismatchedDoubleQuotes() {
        final String inputString = "{\"}";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(2, 1));
    }

    @Test
    void rejectsLeadingNonWhitespaceCharactersWithNewLines() {
        final String inputString = "\n whoops[\n]";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(2, 2));
    }

    @Test
    void rejectsTrailingNonWhitespaceCharactersWithNewLines() {
        final String inputString = "[\n]\n whoops";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception, anInvalidSyntaxExceptionAtPosition(2, 3));
    }

    @Test
    void tokenizesArrayWithLeadingWhitespaceCharacters() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse(" []", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesArrayTrailingWhitespaceCharacters() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[] ", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringTrueValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[tru";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 5:  Expected 't' to be followed by [[r, u, e]], but got [[r, u]]."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFalseValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[fal";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 5:  Expected 'f' to be followed by [[a, l, s, e]], but got [[a, l]]."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNullValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[nul";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 5:  Expected 'n' to be followed by [[u, l, l]], but got [[u, l]]."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringHexCharacterWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[\"\\uab";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 7:  Expected 4 hexadecimal digits, but got [[a, b]]."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringExponentWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1E";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 4:  Expected '+' or '-' or a digit 0 - 9 but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFractionalPartOfNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1.";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 4:  Expected a digit 0 - 9 but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNegativeNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[-";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 3:  Expected a digit 0 - 9 but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringEscapedCharacterWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[\"\\";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 4:  Unexpectedly reached end of input during escaped character."));
    }

    @Test
    void rejectsPrematureEndOfStreamStartingArrayOrObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 1:  Expected a value but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamEndingArrayWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 2:  Expected a value but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamEndingPopulatedArrayWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamEndingObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 2:  Expected object identifier to begin with [\"] but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamFollowingFieldNameWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\"";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 5:  Expected object identifier to be followed by : but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamFollowingFieldNameAndSeparatorWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\":";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 6:  Expected a value but reached end of input."));
    }

    @Test
    void rejectsPrematureEndOfStreamEndingPopulatedObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\":1";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 7:  Expected either , or ] but reached end of input."));
    }

    @Test
    void handlesIncompleteEscapedUnicodeCorrectly() {
        final String inputString = "\"\\u";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 4:  Expected 4 hexadecimal digits, but reached end of input."));
    }

    @Test
    void handlesIncompleteTrueCorrectly() {
        final String inputString = "t";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 2:  Expected 't' to be followed by [[r, u, e]], but reached end of input."));
    }

    @Test
    void handlesIncompleteFalseCorrectly() {
        final String inputString = "f";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 2:  Expected 'f' to be followed by [[a, l, s, e]], but reached end of input."));
    }

    @Test
    void handlesIncompleteNullCorrectly() {
        final String inputString = "n";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 2:  Expected 'n' to be followed by [[u, l, l]], but reached end of input."));
    }

    @Test
    void rethrowsIOExceptionFromReader() {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> new SajParser().parse(new BrokenReader(ioException), BLACK_HOLE_JSON_LISTENER));
        assertThat(actualException, sameInstance(ioException));
    }

}
