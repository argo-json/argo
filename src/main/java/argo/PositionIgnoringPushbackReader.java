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
 * @author Mark Slater
 * @author Henrik Sjöstrand
 */
final class PositionIgnoringPushbackReader implements PositionedPushbackReader {

    private final Reader delegate;

    private boolean endOfStream = false;

    private int pushbackBuffer = -1;

    PositionIgnoringPushbackReader(final Reader in) {
        this.delegate = in;
    }

    public void unread(final int character) {
        pushbackBuffer = character;
        if (endOfStream) {
            endOfStream = false;
        }
    }

    public int read() throws IOException {
        final int character;
        if (pushbackBuffer < 0) {
            character = delegate.read();
        } else {
            character = pushbackBuffer;
            pushbackBuffer = -1;
        }
        if (character == -1) {
            endOfStream = true;
        }
        return character;
    }

    public int column() {
        return -1;
    }

    public int line() {
        return -1;
    }

    public Position position() {
        return new Position(column(), line());
    }

}
