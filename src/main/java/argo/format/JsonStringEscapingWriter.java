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

import java.io.IOException;
import java.io.Writer;

import static argo.format.JsonEscapedString.escapeCharBufferTo;

final class JsonStringEscapingWriter extends Writer {
    private Writer out;

    JsonStringEscapingWriter(final Writer out) {
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
        if (out == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void write(final char[] cbuf, final int offset, final int length) throws IOException {
        validateArguments(cbuf, offset, length);
        ensureOpen();
        escapeCharBufferTo(out, cbuf, offset, length);
    }

    @Override
    public void flush() throws IOException {
        ensureOpen();
        out.flush();
    }

    @Override
    public void close() {
        out = null;
    }
}
