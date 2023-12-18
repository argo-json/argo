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
final class PositionTrackingPushbackReader { // TODO should delegate to java.io.PushbackReader?
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final Reader delegate;
    private int column = 0; // TODO test for overflow
    private int line = 1; // TODO test for overflow; should start at 0 and immediately increment?

    private int previousLineEnd;

    private int readsSinceLastCarriageReturn = 2;

    private boolean endOfStream = false; // TODO can this be integrated into reads since last carriage return to gain some performance?

    private int pushbackBuffer = -1;

    PositionTrackingPushbackReader(final Reader in) {
        this.delegate = in;
    }

    void unread(final int character) {
        pushbackBuffer = character;

        if (CARRIAGE_RETURN == character) {
            column = previousLineEnd;
            line--;
            readsSinceLastCarriageReturn = 2;
        } else {
            if (NEWLINE == character) {
                if (readsSinceLastCarriageReturn != 1) {
                    column = previousLineEnd;
                    line--;
                }
            } else {
                column--;
            }
            if (endOfStream) { // TODO needs more testing
                column--;
                endOfStream = false;
            }
            if (readsSinceLastCarriageReturn < 2) {
                readsSinceLastCarriageReturn--;
            }
        }
    }

    void uncount(final char[] resultCharArray) { // TODO this should take offset and length, and should just iterate over unread calls
        column = column - resultCharArray.length;
        if (column < 0) {
            column = 0;
        }
    }

    
    int read() throws IOException {
        final int character;
        if (pushbackBuffer < 0) {
            character = delegate.read();
        } else {
            character = pushbackBuffer;
            pushbackBuffer = -1;
        }

        if (CARRIAGE_RETURN == character) {
            previousLineEnd = column;
            column = 0;
            line++;
            readsSinceLastCarriageReturn = 0;
        } else {
            if (NEWLINE == character) {
                if (readsSinceLastCarriageReturn != 0) {
                    previousLineEnd = column;
                    column = 0;
                    line++;
                }
            } else {
                if (!endOfStream) {
                    column++;
                }
                if (character == -1) {
                    endOfStream = true;
                }
            }
            if (!endOfStream && readsSinceLastCarriageReturn < 2) {
                readsSinceLastCarriageReturn++;
            }
        }
        return character;
    }

    int read(final char[] buffer) throws IOException { // TODO test performance vs reading an array from the delegate
        int i = 0;
        int nextCharacater;
        for (; i < buffer.length && (nextCharacater = read()) != -1; i++) {
            buffer[i] = (char) nextCharacater;
        }
        return i == 0 ? -1 : i;
    }

    Position position() {
        return new Position(column, line);
    }

    // TODO close?
}
