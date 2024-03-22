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

import org.apache.commons.io.output.BrokenWriter;
import org.apache.commons.io.output.NullWriter;
import org.apache.commons.io.output.StringBuilderWriter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.Writer;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonStringEscapingWriterTest {

    static TypeSafeDiagnosingMatcher<String> writtenAs(final String expected) {
        final Matcher<String> stringMatcher = equalTo(expected);
        return new TypeSafeDiagnosingMatcher<String>() {
            @Override
            protected boolean matchesSafely(final String item, final Description mismatchDescription) {
                final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
                try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
                    try {
                        jsonStringEscapingWriter.write(item);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                final boolean matches = stringMatcher.matches(stringBuilderWriter.toString());
                if (!matches) {
                    stringMatcher.describeMismatch(stringBuilderWriter.toString(), mismatchDescription);
                }
                return matches;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Writes output ").appendDescriptionOf(stringMatcher);
            }
        };
    }

    @Test
    @SuppressWarnings("resource")
    void rejectsNullDelegate() {
        assertThrows(NullPointerException.class, () -> new JsonStringEscapingWriter(null, new WriteBufferHolder()));
    }

    @Test
    void rejectsNullWriteBufferHolder() {
        assertThrows(NullPointerException.class, () -> new JsonStringEscapingWriter(NullWriter.INSTANCE, null));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void rejectsWriteOfNullString() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(NullPointerException.class, () -> jsonStringEscapingWriter.write((String) null));
        }
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void rejectsWriteOfNullCharArray() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(NullPointerException.class, () -> jsonStringEscapingWriter.write((char[]) null));
        }
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void rejectsWriteOfNullStringWithOffsetAndLength() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(NullPointerException.class, () -> jsonStringEscapingWriter.write((String) null, 0, 0));
        }
    }

    @Test
    void rejectsWriteOfNullCharArrayWithOffsetAndLength() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(NullPointerException.class, () -> jsonStringEscapingWriter.write((char[]) null, 0, 0));
        }
    }

    @Test
    void writesStringToDelegate() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write("a");
        }
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void writesLongStringToDelegate() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2048; i++) {
            stringBuilder.append('a');
        }
        final String value = stringBuilder.toString();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write(value);
        }
        assertThat(stringBuilderWriter.toString(), equalTo(value));
    }

    @Test
    void writingStringToDelegateUsesReusableWriteBuffer() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final WriteBufferHolder writeBufferHolder = new WriteBufferHolder();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, writeBufferHolder)) {
            jsonStringEscapingWriter.write("a");
        }
        assertThat(writeBufferHolder.writeBuffer()[0], equalTo('a'));
    }

    @Test
    void writingLongJsonStringDoesNotUseReusableWriteBuffer() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2048; i++) {
            stringBuilder.append('a');
        }
        final String value = stringBuilder.toString();
        final WriteBufferHolder writeBufferHolder = new WriteBufferHolder();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, writeBufferHolder)) {
            jsonStringEscapingWriter.write(value);
        }
        assertThat(writeBufferHolder.writeBuffer()[0], equalTo((char) 0));
    }

    @Test
    void writesCharacterToDelegate() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write('a');
        }
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void writesSubstringsImmediately() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write("a");
            assertThat(stringBuilderWriter.toString(), equalTo("a"));
            jsonStringEscapingWriter.write("b");
            assertThat(stringBuilderWriter.toString(), equalTo("ab"));
        }
    }

    @Test
    void writesCharactersImmediately() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write('a');
            assertThat(stringBuilderWriter.toString(), equalTo("a"));
            jsonStringEscapingWriter.write('b');
            assertThat(stringBuilderWriter.toString(), equalTo("ab"));
        }
    }

    @Test
    void passesThroughUncontentiousCharactersVerbatim() {
        final String uncontentiousCharacters = "abcdefghijklmnopqrstuvwxyz";
        assertThat(uncontentiousCharacters, is(writtenAs(uncontentiousCharacters)));
    }

    @Test
    void writesReverseSolidusAsEscapedReverseSolidus() {
        assertThat("\\", is(writtenAs("\\\\")));
    }

    @Test
    void writesDoubleQuoteAsEscapedDoubleQuote() {
        assertThat("\"", is(writtenAs("\\\"")));
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
    void writesControlCharactersAsEscapedUnicodeCharacters(final int character, final String expectedRepresentation) {
        assertThat(String.valueOf((char) character), is(writtenAs(expectedRepresentation)));
    }

    @Test
    void writesACharArray() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write(new char[]{'a', 'b', 'c'}, 0, 3);
        }
        assertThat(stringBuilderWriter.toString(), equalTo("abc"));
    }

    @Test
    void writesACharArrayWithOffset() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write(new char[]{'a', 'b', 'c'}, 1, 2);
        }
        assertThat(stringBuilderWriter.toString(), equalTo("bc"));
    }

    @Test
    void writesACharArrayWithLength() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write(new char[]{'a', 'b', 'c'}, 0, 2);
        }
        assertThat(stringBuilderWriter.toString(), equalTo("ab"));
    }

    @Test
    void writesACharArrayWithOffsetAndLength() throws Exception {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write(new char[]{'a', 'b', 'c'}, 1, 1);
        }
        assertThat(stringBuilderWriter.toString(), equalTo("b"));
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonStringEscapingWriter.write(new char[3], -1, 0));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonStringEscapingWriter.write(new char[3], 4, 0));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonStringEscapingWriter.write(new char[3], 0, -1));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonStringEscapingWriter.write(new char[3], 1, 3));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() {
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonStringEscapingWriter.write(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @Test
    void propagatesIoExceptionWritingString() {
        final IOException ioException = new IOException("An IOException");
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(new BrokenWriter(ioException), new WriteBufferHolder())) {
            final IOException actualException = assertThrows(IOException.class, () -> jsonStringEscapingWriter.write("a"));
            assertThat(actualException, sameInstance(ioException));
        }
    }

    @Test
    void propagatesIoExceptionWritingCharacter() {
        final IOException ioException = new IOException("An IOException");
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(new BrokenWriter(ioException), new WriteBufferHolder())) {
            final IOException actualException = assertThrows(IOException.class, () -> jsonStringEscapingWriter.write('a'));
            assertThat(actualException, sameInstance(ioException));
        }
    }

    @Test
    void flushIsPropagatedToDelegate() throws IOException {
        try (FlushCountingWriter flushCountingWriter = new FlushCountingWriter(); JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(flushCountingWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write("a");
            jsonStringEscapingWriter.flush();
            assertThat(flushCountingWriter.flushCount(), equalTo(1));
        }
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void closeIsNotPropagatedToDelegate() throws IOException {
        try (CloseCountingWriter closeCountingWriter = new CloseCountingWriter()) {
            final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(closeCountingWriter, new WriteBufferHolder());
            jsonStringEscapingWriter.write("a");
            jsonStringEscapingWriter.close();
            assertThat(closeCountingWriter.closeCount(), equalTo(0));
        }
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingCanCallCloseAgain() throws IOException {
        try (Writer delegate = NullWriter.INSTANCE) {
            final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(delegate, new WriteBufferHolder());
            jsonStringEscapingWriter.write("a");
            jsonStringEscapingWriter.close();
            jsonStringEscapingWriter.close();
        }
    }

    @Test
    void afterClosingWritingASubstringThrowsIOException() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write("a");
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, () -> jsonStringEscapingWriter.write("b"));
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void afterClosingWritingACharacterThrowsIOException() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write('a');
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, () -> jsonStringEscapingWriter.write('b'));
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }

    @Test
    void afterClosingFlushThrowsIOException() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringBuilderWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write("a");
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, jsonStringEscapingWriter::flush);
        assertThat(stringBuilderWriter.toString(), equalTo("a"));
    }
}