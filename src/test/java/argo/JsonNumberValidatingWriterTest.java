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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonNumberValidatingWriterTest {

    @Test
    @SuppressWarnings("resource")
    void rejectsNullDelegate() {
        assertThrows(NullPointerException.class, () -> new JsonNumberValidatingWriter(null, new WriteBufferHolder()));
    }

    @Test
    void rejectsNullWriteBufferHolder() {
        assertThrows(NullPointerException.class, () -> new JsonNumberValidatingWriter(NullWriter.INSTANCE, null));
    }

    @Test
    @SuppressWarnings({"PMD.CloseResource", "DataFlowIssue"})
    void rejectsWriteOfNullString() {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder());
        assertThrows(NullPointerException.class, () -> jsonNumberValidatingWriter.write((String) null));
    }

    @Test
    @SuppressWarnings({"PMD.CloseResource", "DataFlowIssue"})
    void rejectsWriteOfNullCharArray() {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder());
        assertThrows(NullPointerException.class, () -> jsonNumberValidatingWriter.write((char[]) null));
    }

    @Test
    @SuppressWarnings({"PMD.CloseResource", "DataFlowIssue"})
    void rejectsWriteOfNullStringWithOffsetAndLength() {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder());
        assertThrows(NullPointerException.class, () -> jsonNumberValidatingWriter.write((String) null, 0, 0));
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void rejectsWriteOfNullCharArrayWithOffsetAndLength() {
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder());
        assertThrows(NullPointerException.class, () -> jsonNumberValidatingWriter.write((char[]) null, 0, 0));
    }

    @Test
    void writesValidNumberToDelegate() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write("0");
            }
            assertThat(stringBuilderWriter.toString(), equalTo("0"));
        }
    }

    @Test
    void writesLongValidNumberToDelegate() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 2048; i++) {
                stringBuilder.append('1');
            }
            String value = stringBuilder.toString();
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(value);
            }
            assertThat(stringBuilderWriter.toString(), equalTo(value));
        }
    }

    @Test
    void writingValidNumberToDelegateUsesReusableWriteBuffer() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        final WriteBufferHolder writeBufferHolder = new WriteBufferHolder();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, writeBufferHolder)) {
            jsonNumberValidatingWriter.write("0");
        }
        assertThat(writeBufferHolder.writeBuffer()[0], equalTo('0'));
    }

    @Test
    void writingLongValidNumberDoesNotUseReusableWriteBuffer() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter()) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 2048; i++) {
                stringBuilder.append('1');
            }
            String value = stringBuilder.toString();
            final WriteBufferHolder writeBufferHolder = new WriteBufferHolder();
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, writeBufferHolder)) {
                jsonNumberValidatingWriter.write(value);
            }
            assertThat(writeBufferHolder.writeBuffer()[0], equalTo((char) 0));
        }
    }

    @Test
    void writesValidNumberAsCharactersToDelegate() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write('0');
        }
        assertThat(stringBuilderWriter.toString(), equalTo("0"));
    }

    @Test
    void writesValidNumberSubstringsImmediately() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("4");
            assertThat(stringBuilderWriter.toString(), equalTo("4"));
            jsonNumberValidatingWriter.write("2");
            assertThat(stringBuilderWriter.toString(), equalTo("42"));
        }
    }

    @Test
    void writesValidNumberCharactersImmediately() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write('4');
            assertThat(stringBuilderWriter.toString(), equalTo("4"));
            jsonNumberValidatingWriter.write('2');
            assertThat(stringBuilderWriter.toString(), equalTo("42"));
        }
    }

    @Test
    void rejectsNumberWithLeadingInvalidSubstring() {
        final StringWriter stringWriter = new StringWriter();
        assertThrows(IllegalArgumentException.class, () -> {
                    try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter, new WriteBufferHolder())) {
                        jsonNumberValidatingWriter.write(".");
                    }
                }
        );
    }

    @Test
    void rejectsNumberWithLeadingInvalidCharacter() {
        final StringWriter stringWriter = new StringWriter();
        assertThrows(IllegalArgumentException.class, () -> {
                    try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter, new WriteBufferHolder())) {
                        jsonNumberValidatingWriter.write('.');
                    }
                }
        );
    }

    @Test
    void rejectsNumberWithTrailingInvalidSubstring() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("0");
            assertThrows(IllegalArgumentException.class, () -> jsonNumberValidatingWriter.write("p"));
        }
    }

    @Test
    void rejectsNumberWithTrailingInvalidCharacter() throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("0");
            assertThrows(IllegalArgumentException.class, () -> jsonNumberValidatingWriter.write('p'));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(new char[3], -1, 0);
            }
        });
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(new char[3], 4, 0);
            }
        });
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(new char[3], 0, -1);
            }
        });
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(new char[3], 1, 3);
            }
        });
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE, new WriteBufferHolder())) {
                jsonNumberValidatingWriter.write(new char[3], 1, Integer.MAX_VALUE);
            }
        });
    }

    @Test
    void propagatesIoExceptionWritingString() {
        final IOException ioException = new IOException("An IOException");
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(new BrokenWriter(ioException), new WriteBufferHolder())) {
            final IOException actualException = assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write("0"));
            assertThat(actualException, sameInstance(ioException));
        }
    }

    @Test
    void propagatesIoExceptionWritingCharacter() {
        final IOException ioException = new IOException("An IOException");
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(new BrokenWriter(ioException), new WriteBufferHolder())) {
            final IOException actualException = assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write('0'));
            assertThat(actualException, sameInstance(ioException));
        }
    }

    @Test
    void flushIsPropagatedToDelegate() throws IOException {
        try (FlushCountingWriter flushCountingWriter = new FlushCountingWriter(); JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(flushCountingWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("2");
            jsonNumberValidatingWriter.flush();
            assertThat(flushCountingWriter.flushCount(), equalTo(1));
        }
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void closeIsNotPropagatedToDelegate() throws IOException {
        try (CloseCountingWriter closeCountingWriter = new CloseCountingWriter()) {
            final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(closeCountingWriter, new WriteBufferHolder());
            jsonNumberValidatingWriter.write("2");
            jsonNumberValidatingWriter.close();
            assertThat(closeCountingWriter.closeCount(), equalTo(0));
        }
    }

    @Test
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingCanCallCloseAgain() throws IOException {
        try (CloseCountingWriter closeCountingWriter = new CloseCountingWriter()) {
            final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(closeCountingWriter, new WriteBufferHolder());
            jsonNumberValidatingWriter.write("2");
            jsonNumberValidatingWriter.close();
            jsonNumberValidatingWriter.close();
        }
    }

    @Test
    void afterClosingWritingASubstringThrowsIOException() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter(); JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("2");
            jsonNumberValidatingWriter.close();
            assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write("0"));
            assertThat(stringBuilderWriter.toString(), equalTo("2"));
        }
    }

    @Test
    void afterClosingWritingACharacterThrowsIOException() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter(); JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write('2');
            jsonNumberValidatingWriter.close();
            assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write('0'));
            assertThat(stringBuilderWriter.toString(), equalTo("2"));
        }
    }

    @Test
    void afterClosingFlushThrowsIOException() throws IOException {
        try (StringBuilderWriter stringBuilderWriter = new StringBuilderWriter(); JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringBuilderWriter, new WriteBufferHolder())) {
            jsonNumberValidatingWriter.write("2");
            jsonNumberValidatingWriter.close();
            assertThrows(IOException.class, jsonNumberValidatingWriter::flush);
            assertThat(stringBuilderWriter.toString(), equalTo("2"));
        }
    }

}