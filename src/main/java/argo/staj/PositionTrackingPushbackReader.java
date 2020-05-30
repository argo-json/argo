/*
 *  Copyright  2020 Mark Slater
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
final class PositionTrackingPushbackReader implements ThingWithPosition {
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final Reader delegate;
    private int characterCount = 0;
    private int lineCount = 1;

    private int lastCharacter;
    private boolean pushedBackValueAvailable = false;

    PositionTrackingPushbackReader(final Reader in) {
        this.delegate = in;
    }

    void unreadLastCharacter() {
        if (pushedBackValueAvailable) {
            throw new RuntimeException("Coding failure in Argo: Tried to pushback when pushback buffer is already full with " + lastCharacter);
        } else {
            characterCount--;
            if (characterCount < 0) {
                characterCount = 0;
            }
            pushedBackValueAvailable = true;
        }
    }

    void uncount(final char[] resultCharArray) {
        characterCount = characterCount - resultCharArray.length;
        if (characterCount < 0) {
            characterCount = 0;
        }
    }

    /**
     * @throws JsonStreamException if delegate.read() throws an IOException
     */
    int read() {
        if (!pushedBackValueAvailable) {
            try {
                lastCharacter = delegate.read();
            } catch (final IOException e) {
                throw new JsonStreamException("Failed to read from Reader", e);
            }
        }
        pushedBackValueAvailable = false;
        updateCharacterAndLineCounts(lastCharacter);
        return lastCharacter;
    }

    /**
     * @throws JsonStreamException if delegate.read() throws an IOException
     */
    int read(final char[] buffer) {
        if (buffer.length == 0) {
            return 0;
        } else {
            int result = 0;
            if (pushedBackValueAvailable) {
                if (lastCharacter == -1) {
                    updateCharacterAndLineCounts(lastCharacter);
                    pushedBackValueAvailable = false;
                    return -1;
                } else {
                    buffer[0] = (char) lastCharacter;
                    result = 1;
                }
            }
            try {
                int latestCharactersRead;
                do {
                    latestCharactersRead = delegate.read(buffer, result, buffer.length - result);
                    if (latestCharactersRead != -1 || result == 0) {
                        result = result + latestCharactersRead;
                    }
                } while (latestCharactersRead != -1 && result < buffer.length);
                if (result == -1) {
                    updateCharacterAndLineCounts(-1);
                } else {
                    for (int i = 0; i < result; i++) {
                        updateCharacterAndLineCounts(buffer[i]);
                    }
                    if (result < buffer.length) {
                        updateCharacterAndLineCounts(-1);
                    }
                }
                if (result > 0) {
                    lastCharacter = buffer[result - 1];
                    pushedBackValueAvailable = false;
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
        } else {
            if (NEWLINE == result && CARRIAGE_RETURN != lastCharacter) {
                characterCount = 0;
                lineCount++;
            } else {
                characterCount++;
            }
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
