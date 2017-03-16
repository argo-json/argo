/*
 * Copyright 2016 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * @author Mark Slater
 * @author Henrik Sjöstrand
 */
final class PositionTrackingPushbackReader implements ThingWithPosition {
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final PushbackReader pushbackReader; // TODO the way this behaves with -1 really doesn't help - replace
    private int characterCount = 0;
    private int lineCount = 1;
    private boolean lastCharacterWasCarriageReturn = false;
    private boolean pastEndOfStream = false;

    PositionTrackingPushbackReader(final Reader in) {
        this.pushbackReader = new PushbackReader(in);
    }

    void unread(final int c) throws JsonStreamException { // TODO why do you have to specify the character to push back?
        characterCount--;
        if (characterCount < 0) characterCount = 0;
        if (c == -1) {
            pastEndOfStream = true;
        } else {
            try {
                pushbackReader.unread(c);
            } catch (final IOException e) {
                throw new JsonStreamException("Failed to read from Reader", e);
            }
        }
    }

    void uncount(final char[] resultCharArray) {
        characterCount = characterCount - resultCharArray.length;
        if (characterCount < 0) characterCount = 0;
    }

    int read() throws JsonStreamException {
        try {
            final int result = pastEndOfStream ? -1 : pushbackReader.read();
            updateCharacterAndLineCounts(result);
            return result;
        } catch (final IOException e) {
            throw new JsonStreamException("Failed to read from Reader", e);
        }
    }

    int read(final char[] buffer) throws JsonStreamException {
        if (pastEndOfStream) {
            return -1;
        } else {
            try {
                int result = 0;
                for (int latestCharactersRead = 0; latestCharactersRead != -1 && result < buffer.length; latestCharactersRead = pushbackReader.read(buffer, result, buffer.length - result)) {
                    result = result + latestCharactersRead;
                }
                for (char character : buffer) {
                    updateCharacterAndLineCounts(character);
                }
                return result;
            } catch (final IOException e) {
                throw new JsonStreamException("Failed to read from Reader", e);
            }
        }
    }

    private void updateCharacterAndLineCounts(final int result) {
        if (CARRIAGE_RETURN == result) {
            characterCount = 0;
            lineCount++;
            lastCharacterWasCarriageReturn = true;
        } else {
            if (NEWLINE == result && !lastCharacterWasCarriageReturn) {
                characterCount = 0;
                lineCount++;
            } else {
                characterCount++;
            }
            lastCharacterWasCarriageReturn = false;
        }
    }

    public int getColumn() {
        return characterCount;
    }

    public int getRow() {
        return lineCount;
    }

    ThingWithPosition snapshotOfPosition() {
        return new ThingWithPosition() {
            private final int localCharacterCount = characterCount;
            private final int localLineCount = lineCount;

            public int getColumn() {
                return localCharacterCount;
            }

            public int getRow() {
                return localLineCount;
            }
        };
    }
}
