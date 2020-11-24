/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static argo.saj.InvalidSyntaxExceptionMatcher.anInvalidSyntaxExceptionAtPosition;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

final class SajParserTest { // NOPMD TODO this should be turned off in the rules
    private static final JsonListener BLACK_HOLE_LISTENER = new JsonListener() {
        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startArray() {
        }

        public void endArray() {
        }

        public void startObject() {
        }

        public void endObject() {
        }

        public void startField(String name) {
        }

        public void endField() {
        }

        public void stringValue(String value) {
        }

        public void numberValue(String value) {
        }

        public void trueValue() {
        }

        public void falseValue() {
        }

        public void nullValue() {
        }
    };
    private final Mockery context = new Mockery();

    @Test
    void tokenizesValidString() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"hello world\"", "hello world");
    }

    @Test
    void tokenizesValidStringWithEscapedChars() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"\\\"hello world\\\"\"", "\"hello world\"");
    }

    @Test
    void tokenizesValidStringWithEscapedUnicodeChars() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"hello world \\uF001\"", "hello world \uF001");
    }

    @Test
    void rejectsStringWithInvalidEscapedUnicodeChars() {
        final String inputString = "[\"hello world \\uF0\"]";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(16, 1));
        }
    }

    @Test
    void rejectsInvalidString() {
        final String inputString = "[hello world\"]";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(2, 1));
        }
    }

    @Test
    void tokenizesValidNumber() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12.123E-2");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("[12.123E-2]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesValidNumberWithLowerCaseExponent() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12.123e-2");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("[12.123e-2]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonStringObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"hello\":\"world\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonStringObjectFromJsonString() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse("{\"hello\":\"world\"}", jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonStringObjectWithWhitespace() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"hello\": \"world\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesMultiElementArrayWithWhitespace() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("1");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("2");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("[ 1, 2 ]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesJsonStringObjectPair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("foo");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("bar");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"hello\":\"world\",\"foo\":\"bar\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonNumberValue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("room");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"room\":101}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonNull() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("points");
            inSequence(expectedSequence);
            oneOf(jsonListener).nullValue();
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"points\":null}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonTrue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("points");
            inSequence(expectedSequence);
            oneOf(jsonListener).trueValue();
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"points\":true}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSimpleJsonFalse() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("points");
            inSequence(expectedSequence);
            oneOf(jsonListener).falseValue();
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"points\":false}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesJsonNumberValuePair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("room");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("answer");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("42");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"room\":101,\"answer\":42}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesJsonMixedPair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("room");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("foo");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("bar");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"room\":101,\"foo\":\"bar\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesEmptyObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "{}";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesNestedObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test");
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("Inner test");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "{\"Test\":{\"Inner test\":12}}";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesEmptyArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "[]";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesSingleElementArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "[12]";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesMultiElementArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("test");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "[12,\"test\"]";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesNestedArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "[[12]]";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesObjectWithArrayValue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test");
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("{\"Test\":[12]}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesArrayWithObjectElement() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).startObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test");
            inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12");
            inSequence(expectedSequence);
            oneOf(jsonListener).endField();
            inSequence(expectedSequence);
            oneOf(jsonListener).endObject();
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("[{\"Test\":12}]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void rejectsEmptyDocument() {
        final String inputString = "";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(1, 1));
        }
    }


    @Test
    void rejectsLeadingNonWhitespaceCharacters() {
        final String inputString = "whoops[]";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(1, 1));
        }
    }

    @Test
    void rejectsTrailingNonWhitespaceCharacters() {
        final String inputString = "[]whoops";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(3, 1));
        }
    }

    @Test
    void rejectsMismatchedDoubleQuotes() {
        final String inputString = "{\"}";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(2, 1));
        }
    }

    @Test
    void rejectsLeadingNonWhitespaceCharactersWithNewLines() {
        final String inputString = "\n whoops[\n]";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(2, 2));
        }
    }

    @Test
    void rejectsTrailingNonWhitespaceCharactersWithNewLines() {
        final String inputString = "[\n]\n whoops";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e, anInvalidSyntaxExceptionAtPosition(2, 3));
        }
    }

    @Test
    void tokenizesObjectLeadingWhitespaceCharacters() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = " []";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void tokenizesObjectTrailingWhitespaceCharacters() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        final String inputString = "[] ";
        new SajParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    private void assertJsonValueFragmentResultsInStringValue(final String jsonFragment, final String expectedStringValue) throws InvalidSyntaxException {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument();
            inSequence(expectedSequence);
            oneOf(jsonListener).startArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).stringValue(expectedStringValue);
            inSequence(expectedSequence);
            oneOf(jsonListener).endArray();
            inSequence(expectedSequence);
            oneOf(jsonListener).endDocument();
            inSequence(expectedSequence);
        }});
        new SajParser().parse(new StringReader("[" + jsonFragment + "]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    void rejectsPrematureEndOfStreamDuringTrueValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[tru";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 5:  Expected 't' to be followed by [[r, u, e]], but got [[r, u]]."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFalseValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[fal";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 5:  Expected 'f' to be followed by [[a, l, s, e]], but got [[a, l]]."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNullValueWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[nul";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 5:  Expected 'n' to be followed by [[u, l, l]], but got [[u, l]]."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringHexCharacterWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[\"\\uab";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 7:  Expected 4 hexadecimal digits, but got [[a, b]]."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringExponentWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1E";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 4:  Expected a digit 0 - 9 but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFractionalPartOfNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1.";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 4:  Expected a digit 0 - 9 but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNegativeNumberWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[-";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 3:  Expected a digit 1 - 9 but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamDuringEscapedCharacterWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[\"\\";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 4:  Unexpectedly reached end of input during escaped character."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamStartingArrayOrObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 1:  Expected a value but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamEndingArrayWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 2:  Expected a value but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamEndingPopulatedArrayWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "[1";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamEndingObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 2:  Expected object identifier to begin with [\"] but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamFollowingFieldNameWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\"";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 5:  Expected object identifier to be followed by : but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamFollowingFieldNameAndSeparatorWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\":";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 6:  Expected a value but reached end of input."));
        }
    }

    @Test
    void rejectsPrematureEndOfStreamEndingPopulatedObjectWithoutNonPrintingCharactersInTheExceptionMessage() {
        final String inputString = "{\"a\":1";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 7:  Expected either , or ] but reached end of input."));
        }
    }

    @Test
    void rejectsNumberWithTwoDecimalPoints() {
        final String inputString = "[1.1.1]";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 5:  Expected either , or ] but got [.]."));
        }
    }

    @Test
    void handlesIncompleteEscapedUnicodeCorrectly() {
        final String inputString = "\"\\u";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 4:  Expected 4 hexadecimal digits, but reached end of input."));
        }
    }

    @Test
    void handlesIncompleteTrueCorrectly() {
        final String inputString = "t";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 2:  Expected 't' to be followed by [[r, u, e]], but reached end of input."));
        }
    }

    @Test
    void handlesIncompleteFalseCorrectly() {
        final String inputString = "f";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 2:  Expected 'f' to be followed by [[a, l, s, e]], but reached end of input."));
        }
    }

    @Test
    void handlesIncompleteNullCorrectly() {
        final String inputString = "n";
        try {
            new SajParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Parsing [" + inputString + "] should result in an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            assertThat(e.getMessage(), equalTo("At line 1, column 2:  Expected 'n' to be followed by [[u, l, l]], but reached end of input."));
        }
    }

}
