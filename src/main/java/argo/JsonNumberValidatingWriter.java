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

import argo.internal.NumberParserState;

import java.io.IOException;
import java.io.Writer;

final class JsonNumberValidatingWriter extends Writer {

    private NumberParserState numberParserState = NumberParserState.BEFORE_START;
    private Writer out;
    private final WriteBufferHolder writeBufferHolder;

    JsonNumberValidatingWriter(final Writer out, final WriteBufferHolder writeBufferHolder) {
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
        if (writeBufferHolder == null) {
            throw new NullPointerException();
        }
        this.writeBufferHolder = writeBufferHolder;
    }

    private static void validateArguments(final char[] cbuf, final int offset, final int length) {
        if (offset < 0 || offset > cbuf.length || length < 0 ||
                offset + length > cbuf.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void ensureOpen() throws IOException {
        if (out == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void write(final int c) throws IOException {
        ensureOpen();
        numberParserState = numberParserState.handle(c);
        if (numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT || numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT_OR_MINUS || numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS) {
            throw new IllegalArgumentException("Attempted to write characters that do not conform to the JSON number specification");
        }
        if (numberParserState == NumberParserState.END) {
            throw new IllegalArgumentException("Attempted to write characters that do not conform to the JSON number specification");
        }
        out.write(c);
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        ensureOpen();
        final char[] cbuf = len <= WriteBufferHolder.WRITE_BUFFER_SIZE ? writeBufferHolder.writeBuffer() : new char[len];
        str.getChars(off, off + len, cbuf, 0);
        write(cbuf, 0, len);
    }

    public void write(@SuppressWarnings("NullableProblems") final char[] cbuf, final int offset, final int length) throws IOException {
        validateArguments(cbuf, offset, length);
        ensureOpen();
        for (int i = offset; i < length; i++) {
            numberParserState = numberParserState.handle(cbuf[i]);
            if (numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT || numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT_OR_MINUS || numberParserState == NumberParserState.ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS) {
                throw new IllegalArgumentException("Attempted to write characters that do not conform to the JSON number specification");
            }
        }
        if (numberParserState == NumberParserState.END) {
            throw new IllegalArgumentException("Attempted to write characters that do not conform to the JSON number specification");
        }
        out.write(cbuf, offset, length);
    }

    public void flush() throws IOException {
        ensureOpen();
        out.flush();
    }

    public void close() {
        if (out != null) {
            out = null;
            if (numberParserState != NumberParserState.ERROR_EXPECTED_DIGIT && numberParserState != NumberParserState.ERROR_EXPECTED_DIGIT_OR_MINUS && numberParserState != NumberParserState.ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS) {
                numberParserState = numberParserState.handle(-1);
            }
            if (numberParserState != NumberParserState.END) {
                throw new IllegalStateException("Attempt to write an incomplete JSON number");
            }
        }
    }

    void endExceptionally() {
        numberParserState = NumberParserState.END;
    }
}
