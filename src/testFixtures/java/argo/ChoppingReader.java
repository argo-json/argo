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

/**
 * Implementation of Reader that wraps underlying Reader, but forces Reader.read(...) to read at most 1 character.
 * This makes it useful for exposing the case when a 'some input' is available, but not enough to fill the supplied buffer.
 * It's expected of the Reader's consumer to keep calling read, until the supplied buffer is filled.
 *
 * @author Henrik Sjöstrand
 */
public final class ChoppingReader extends Reader {
    private final Reader reader;

    public ChoppingReader(final Reader reader) {
        this.reader = reader;
    }

    @Override
    public int read(@SuppressWarnings("NullableProblems") final char[] chars, final int off, final int len) throws IOException {
        return reader.read(chars, off, (len == 0) ? 0 : 1);
    }

    @Override
    public void close() {
    }
}
