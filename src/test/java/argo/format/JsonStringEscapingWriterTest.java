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

import org.apache.commons.io.output.BrokenWriter;
import org.apache.commons.io.output.NullWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class JsonStringEscapingWriterTest {

    @Test
    @SuppressWarnings("resource")
    void rejectsNullDelegate() {
        assertThrows(NullPointerException.class, () -> new JsonStringEscapingWriter(null, new WriteBufferHolder()));
    }

    @Test
    void writesStringToDelegate() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write("a");
        }
        assertThat(stringWriter.toString(), equalTo("a"));
    }

    @Test
    void writesCharacterToDelegate() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write('a');
        }
        assertThat(stringWriter.toString(), equalTo("a"));
    }

    @Test
    void writesSubstringsImmediately() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write("a");
            assertThat(stringWriter.toString(), equalTo("a"));
            jsonStringEscapingWriter.write("b");
            assertThat(stringWriter.toString(), equalTo("ab"));
        }
    }

    @Test
    void writesCharactersImmediately() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder())) {
            jsonStringEscapingWriter.write('a');
            assertThat(stringWriter.toString(), equalTo("a"));
            jsonStringEscapingWriter.write('b');
            assertThat(stringWriter.toString(), equalTo("ab"));
        }
    }

    // TODO test escaping

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
        try (CloseCountingWriter closeCountingWriter = new CloseCountingWriter()) {
            final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(closeCountingWriter, new WriteBufferHolder());
            jsonStringEscapingWriter.write("a");
            jsonStringEscapingWriter.close();
            jsonStringEscapingWriter.close();
        }
    }

    @Test
    void afterClosingWritingASubstringThrowsIOException() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write("a");
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, () -> jsonStringEscapingWriter.write("b"));
        assertThat(stringWriter.toString(), equalTo("a"));
    }

    @Test
    void afterClosingWritingACharacterThrowsIOException() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write('a');
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, () -> jsonStringEscapingWriter.write('b'));
        assertThat(stringWriter.toString(), equalTo("a"));
    }

    @Test
    void afterClosingFlushThrowsIOException() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonStringEscapingWriter jsonStringEscapingWriter = new JsonStringEscapingWriter(stringWriter, new WriteBufferHolder());
        jsonStringEscapingWriter.write("a");
        jsonStringEscapingWriter.close();
        assertThrows(IOException.class, jsonStringEscapingWriter::flush);
        assertThat(stringWriter.toString(), equalTo("a"));
    }

}