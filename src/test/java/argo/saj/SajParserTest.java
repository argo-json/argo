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

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static argo.saj.BlackHoleJsonListener.BLACK_HOLE_JSON_LISTENER;
import static argo.saj.InvalidSyntaxExceptionMatcher.anInvalidSyntaxExceptionAtPosition;
import static argo.saj.RecordingJsonListener.*;
import static argo.saj.RecordingJsonListener.NumberValue.numberValue;
import static argo.saj.RecordingJsonListener.StartField.startField;
import static argo.saj.RecordingJsonListener.StringValue.stringValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SajParserTest { // NOPMD TODO this should be turned off in the rules

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

    @Test
    void tokenizesValidNumber() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("12.123E-2", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                numberValue("12.123E-2"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesValidNumberWithLowerCaseExponent() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("12.123e-2", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                numberValue("12.123e-2"),
                END_DOCUMENT
        ));
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
    void rejectsNumberWithTwoDecimalPoints() {
        final String inputString = "[1.1.1]";
        final InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(exception.getMessage(), equalTo("At line 1, column 5:  Expected either , or ] but got [.]."));
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

}
