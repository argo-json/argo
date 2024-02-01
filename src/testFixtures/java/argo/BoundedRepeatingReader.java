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

import java.io.IOException;
import java.io.Reader;

public final class BoundedRepeatingReader extends Reader {

    private final int repeatingChar;
    private final int maxChars;
    private int charactersRead = 0;

    public BoundedRepeatingReader(final char repeatingChar, final int maxChars) {
        this.repeatingChar = repeatingChar;
        this.maxChars = maxChars;
    }

    private static void validateArguments(final char[] cbuf, final int offset, final int length) {
        if (offset < 0 || offset > cbuf.length || length < 0 ||
                offset + length > cbuf.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    public int read(final char[] cbuf, final int offset, final int length) throws IOException {
        validateArguments(cbuf, offset, length);
        int n = 0;
        int nextChar;
        while (n < length && (nextChar = read()) != -1) {
            cbuf[offset + n++] = (char) nextChar;
        }
        return n == 0 && length != 0 ? -1 : n;
    }

    @Override
    public int read() {
        if (charactersRead == maxChars) {
            return -1;
        } else {
            charactersRead++;
            return repeatingChar;
        }
    }

    @Override
    public void close() {
    }
}
