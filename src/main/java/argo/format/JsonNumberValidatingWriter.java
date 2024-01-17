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

import argo.internal.NumberParserState;

import java.io.IOException;
import java.io.Writer;

final class JsonNumberValidatingWriter extends Writer {

    private NumberParserState numberParserState = NumberParserState.BEFORE_START;
    private Writer out;

    JsonNumberValidatingWriter(final Writer out) {
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
    }

    private static void validateArguments(final char[] cbuf, final int offset, final int length) {
        if (offset < 0 || offset > cbuf.length || length < 0 ||
                offset + length > cbuf.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void ensureOpen() throws IOException {
        if (out == null)
            throw new IOException("Stream closed");
    }

    public void write(final char[] cbuf, final int offset, final int length) throws IOException {
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
        numberParserState = numberParserState.handle(-1);
        out.write(cbuf, offset, length); // TODO can this happen character by character as we validate?
    }

    public void flush() throws IOException {
        ensureOpen();
        out.flush();
    }

    public void close() {
        out = null;
    }

    boolean isEndState() {
        return numberParserState == NumberParserState.END;
    }
}
