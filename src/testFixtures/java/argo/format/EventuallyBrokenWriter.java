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

public final class EventuallyBrokenWriter extends Writer {
    private final IOException ioException;
    private int charactersBeforeBreaking;

    public EventuallyBrokenWriter(final IOException ioException, final int charactersBeforeBreaking) {
        this.ioException = ioException;
        this.charactersBeforeBreaking = charactersBeforeBreaking;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (charactersBeforeBreaking > 0) {
            charactersBeforeBreaking -= len;
        }
        if (charactersBeforeBreaking <= 0) {
            throw ioException;
        }
    }

    @Override
    public void flush() throws IOException {
        if (charactersBeforeBreaking <= 0) {
            throw ioException;
        }
    }

    @Override
    public void close() throws IOException {
        if (charactersBeforeBreaking <= 0) {
            throw ioException;
        }
    }
}
