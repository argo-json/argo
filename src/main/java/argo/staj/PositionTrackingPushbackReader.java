/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Mark Slater
 * @author Henrik Sjöstrand
 */
final class PositionTrackingPushbackReader implements Position { // TODO should delegate to java.io.PushbackReader?
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final Reader delegate;
    private int columnCount = 0; // TODO test for overflow
    private int lineCount = 1; // TODO test for overflow; should start at 0 and immediately increment?

    private boolean endOfStream = false;

    private char pushbackBuffer;
    private boolean bufferPopulated = false;

    PositionTrackingPushbackReader(final Reader in) {
        this.delegate = in;
    }

    void unread(final int character) {
        columnCount--;
        if (columnCount < 0) {
            columnCount = 0;
        }
        pushbackBuffer = (char) character;
        bufferPopulated = true;
    }

    void uncount(final char[] resultCharArray) {
        columnCount = columnCount - resultCharArray.length;
        if (columnCount < 0) {
            columnCount = 0;
        }
    }

    
    int read() throws IOException {
        final int nextCharacter;
        if (bufferPopulated) {
            bufferPopulated = false;
            nextCharacter = pushbackBuffer;
        } else {
            nextCharacter = delegate.read();
        }
        // TODO does this work when a newline has been pushed back?
        if (CARRIAGE_RETURN == nextCharacter) {
            columnCount = 0;
            lineCount++;
        } else {
            if (NEWLINE == nextCharacter && CARRIAGE_RETURN != pushbackBuffer) {
                columnCount = 0;
                lineCount++;
            } else {
                if (!endOfStream) {
                    columnCount++;
                }
                if (nextCharacter == -1) {
                    endOfStream = true;
                }
            }
        }
        return nextCharacter;
    }

    int read(final char[] buffer) throws IOException {
        if (buffer.length == 0) {
            return 0;
        } else {
            int i = 0;
            int nextChar;
            while(i < buffer.length && (nextChar = read()) != -1) { // TODO this would be improved by calling read(char[]) on the delegate
                buffer[i++] = (char) nextChar;
            }
            return i == 0 ? -1 : i;
        }
    }

    public int getColumn() { // TODO why public?
        return columnCount;
    }

    public int getRow() { // TODO why public?
        return lineCount;
    }

    Position snapshotOfPosition() {
        return new Position() {
            private final int localCharacterCount = columnCount;
            private final int localLineCount = lineCount;

            public int getColumn() {
                return localCharacterCount;
            }

            public int getRow() {
                return localLineCount;
            }
        };
    }

    // TODO close?
}
