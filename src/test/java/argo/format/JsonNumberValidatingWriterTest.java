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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.BrokenWriter;
import org.apache.commons.io.output.NullWriter;
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
        assertThrows(NullPointerException.class, () -> new JsonNumberValidatingWriter(null));
    }

    @Test
    void writesValidNumberToDelegate() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter)) {
            jsonNumberValidatingWriter.write("0");
        }
        assertThat(stringWriter.toString(), equalTo("0"));
    }

    @Test
    void rejectsNumberWithTrailingInvalidCharacters() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter)) {
            jsonNumberValidatingWriter.write("0");
            assertThrows(IllegalArgumentException.class, () -> jsonNumberValidatingWriter.write("p"));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonNumberValidatingWriter.write(new char[3], -1, 0));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonNumberValidatingWriter.write(new char[3], 4, 0));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonNumberValidatingWriter.write(new char[3], 0, -1));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonNumberValidatingWriter.write(new char[3], 1, 3));
        }
    }

    @Test
    void attemptingToWriteABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() {
        try (JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(NullWriter.INSTANCE)) {
            assertThrows(IndexOutOfBoundsException.class, () -> jsonNumberValidatingWriter.write(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @Test
    @SuppressWarnings("PMD.UseTryWithResources")
    void propagatesIoExceptionWriting() {
        final IOException ioException = new IOException("An IOException");
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(new BrokenWriter(ioException));
        try {
            final IOException actualException = assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write("0"));
            assertThat(actualException, sameInstance(ioException));
        } finally {
            IOUtils.closeQuietly(jsonNumberValidatingWriter);
        }
    }

    @Test
    void flushIsPropagatedToDelegate() throws IOException {
        final FlushCountingWriter flushCountingWriter = new FlushCountingWriter();
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(flushCountingWriter);
        jsonNumberValidatingWriter.write("2");
        jsonNumberValidatingWriter.flush();
        assertThat(flushCountingWriter.flushCount(), equalTo(1));
    }

    @Test
    void closeIsNotPropagatedToDelegate() throws IOException {
        final CloseCountingWriter closeCountingWriter = new CloseCountingWriter();
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(closeCountingWriter);
        jsonNumberValidatingWriter.write("2");
        jsonNumberValidatingWriter.close();
        assertThat(closeCountingWriter.closeCount(), equalTo(0));
    }

    @Test
    void afterClosingCanCallCloseAgain() throws IOException {
        final CloseCountingWriter closeCountingWriter = new CloseCountingWriter();
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(closeCountingWriter);
        jsonNumberValidatingWriter.write("2");
        jsonNumberValidatingWriter.close();
        jsonNumberValidatingWriter.close();
    }

    @Test
    void afterClosingWritingACharacterThrowsIOException() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter);
        jsonNumberValidatingWriter.write("2");
        jsonNumberValidatingWriter.close();
        assertThrows(IOException.class, () -> jsonNumberValidatingWriter.write("0"));
        assertThat(stringWriter.toString(), equalTo("2"));
    }

    @Test
    void afterClosingFlushThrowsIOException() throws IOException {
        final StringWriter stringWriter = new StringWriter();
        final JsonNumberValidatingWriter jsonNumberValidatingWriter = new JsonNumberValidatingWriter(stringWriter);
        jsonNumberValidatingWriter.write("2");
        jsonNumberValidatingWriter.close();
        assertThrows(IOException.class, jsonNumberValidatingWriter::flush);
        assertThat(stringWriter.toString(), equalTo("2"));
    }

}