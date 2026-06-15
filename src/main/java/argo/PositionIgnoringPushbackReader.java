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
    private final char[] buffer;
    private int position = 0;
    private int end = 0;

    PositionIgnoringPushbackReader(final Reader delegate, final int bufferSize) {
        this.delegate = delegate;
        buffer = new char[bufferSize];
    }

    public void unread() {
        position--;
    }

    public int read() throws IOException {
        final int result;
        if (position >= end) {
            final int readResult = delegate.read(buffer);
            if (readResult <= 0) {
                result = -1;
            } else {
                result = buffer[0];
                position = 1;
                end = readResult;
            }
        } else {
            result = buffer[position++];
        }
        return result;
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
