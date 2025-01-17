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
final class PositionTrackingPushbackReader implements PositionedPushbackReader {
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final Reader delegate;
    private int column = 0;
    private boolean columnOverflow = false;

    private int previousLineEnd;

    private boolean previousColumnOverflow;

    private int line = 1;

    private boolean lineOverflow = false;

    private int readsSinceLastCarriageReturn = 2;

    private boolean endOfStream = false;

    private int pushbackBuffer = -1;

    PositionTrackingPushbackReader(final Reader in) {
        this.delegate = in;
    }

    public void unread(final int character) {
        pushbackBuffer = character;

        if (CARRIAGE_RETURN == character) {
            column = previousLineEnd;
            columnOverflow = previousColumnOverflow;
            line--;
            readsSinceLastCarriageReturn = 2;
        } else {
            if (NEWLINE == character) {
                if (readsSinceLastCarriageReturn != 1) {
                    column = previousLineEnd;
                    columnOverflow = previousColumnOverflow;
                    line--;
                }
            } else {
                column--;
            }
            if (readsSinceLastCarriageReturn < 2) {
                readsSinceLastCarriageReturn--;
            }
        }
        if (endOfStream) {
            column--;
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

        if (CARRIAGE_RETURN == character) {
            previousLineEnd = column;
            previousColumnOverflow = columnOverflow;
            column = 0;
            columnOverflow = false;
            line++;
            if (line < 0) {
                lineOverflow = true;
            }
            readsSinceLastCarriageReturn = 0;
        } else {
            if (NEWLINE == character) {
                if (readsSinceLastCarriageReturn != 0) {
                    previousLineEnd = column;
                    previousColumnOverflow = columnOverflow;
                    column = 0;
                    columnOverflow = false;
                    line++;
                    if (line < 0) {
                        lineOverflow = true;
                    }
                }
            } else {
                if (!endOfStream) {
                    column++;
                    if (column < 0) {
                        columnOverflow = true;
                    }
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

    public int column() {
        return columnOverflow ? -1 : column;
    }

    public int line() {
        return lineOverflow ? -1 : line;
    }

    public Position position() {
        return new Position(column(), line());
    }

}
