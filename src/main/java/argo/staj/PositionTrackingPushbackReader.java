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
//    private static final int NEWLINE = '\n';
//    private static final int CARRIAGE_RETURN = '\r';

    private final Reader delegate;
//    private int column = 0; // TODO test for overflow
//    private int line = 1; // TODO test for overflow; should start at 0 and immediately increment?
//
//    private int readsSinceLastCarriageReturn = 3;
//
//    private boolean endOfStream = false;

    private char pushbackBuffer;
    private boolean bufferPopulated = false;

    PositionTrackingPushbackReader(final Reader in) {
        this.delegate = in;
    }

    void unread(final int character) {
        pushbackBuffer = (char) character;
        bufferPopulated = true;
//
//        if (CARRIAGE_RETURN == character) {
//            column = 0; // TODO this is wrong... need to remember the last column :(
//            line--;
//            readsSinceLastCarriageReturn = 2;
//        } else {
//            if (NEWLINE == character) {
//                if (readsSinceLastCarriageReturn != 1) { // TODO decrement readsSinceLastCarriageReturn
//                    column = 0; // TODO this is wrong... need to remember the last column :(
//                    line--;
//                }
//            } else {
//                column--;
//                endOfStream = true;
//            }
//            if (readsSinceLastCarriageReturn <= 1) {
//                readsSinceLastCarriageReturn--;
//            }
//        }
    }

    void uncount(final char[] resultCharArray) { // TODO this should take offset and length, and should just iterate over unread calls
//        column = column - resultCharArray.length;
//        if (column < 0) {
//            column = 0;
//        }
    }

    
    int read() throws IOException {
        final int character;
        if (bufferPopulated) {
            bufferPopulated = false;
            character = pushbackBuffer;
        } else {
            character = delegate.read();
        }

//        if (CARRIAGE_RETURN == character) {
//            column = 0;
//            line++;
//            readsSinceLastCarriageReturn = 0;
//        } else {
//            if (NEWLINE == character) {
//                if (readsSinceLastCarriageReturn != 0) {
//                    column = 0;
//                    line++;
//                }
//            } else {
//                if (!endOfStream) {
//                    column++;
//                }
//                if (character == -1) {
//                    endOfStream = true;
//                }
//            }
//            if (readsSinceLastCarriageReturn <= 1) {
//                readsSinceLastCarriageReturn++;
//            }
//        }
        return character;
    }

    int read(final char[] buffer) throws IOException {
        int charactersRead = 0;
        if (bufferPopulated) {
            bufferPopulated = false;
            buffer[0] = pushbackBuffer;
            charactersRead = 1;
        }
        int extraCharactersRead;
        while(charactersRead < buffer.length && (extraCharactersRead = delegate.read(buffer, charactersRead, buffer.length - charactersRead)) != -1) {
            charactersRead += extraCharactersRead;
        }
        return charactersRead == 0 ? -1 : charactersRead;
    }

    Position position() {
        return new Position(0, 1);
    }

    // TODO close?
}
