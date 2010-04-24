/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;

final class PositionTrackingPushbackReader implements ThingWithPosition {

    private final LineNumberReader lineNumberReader;
    private final ResettingRowCounter resettingRowCounter;
    private final PushbackReader pushbackReader;

    public PositionTrackingPushbackReader(final Reader in) {
        this.lineNumberReader = new LineNumberReader(in);
        this.lineNumberReader.setLineNumber(1);
        resettingRowCounter = new ResettingRowCounter(lineNumberReader);
        this.pushbackReader = new PushbackReader(lineNumberReader);
    }

    public void unread(final char c) throws IOException {
        resettingRowCounter.count(-1);
        pushbackReader.unread(c);
    }

    public void uncount(final char[] resultCharArray) throws IOException {
        resettingRowCounter.count(-resultCharArray.length);
    }

    public int read() throws IOException {
        final int result = pushbackReader.read();
        if (result > 0) {
           resettingRowCounter.count(1);
        }
        return result;
    }

    public int read(final char[] remainingNullTokenCharacters) throws IOException {
        final int result = pushbackReader.read(remainingNullTokenCharacters);
        if (result > 0) {
           resettingRowCounter.count(result);
        }
        return result;
    }

    public int getColumn() {
        return resettingRowCounter.getCount();
    }

    public int getRow() {
        return lineNumberReader.getLineNumber();
    }

    private static final class ResettingRowCounter {
        private final LineNumberReader lineNumberReader;
        private int lineNumberAtLastCount;
        private int count;

        ResettingRowCounter(final LineNumberReader lineNumberReader) {
            this.lineNumberReader = lineNumberReader;
            lineNumberAtLastCount = lineNumberReader.getLineNumber();
            count = 0;
        }

        void count(int numberToCount) {
            if (lineNumberReader.getLineNumber() != lineNumberAtLastCount) {
                lineNumberAtLastCount = lineNumberReader.getLineNumber();
                count = 0;
            } else {
                count += numberToCount;
            }
        }

        public int getCount() {
            return count;
        }
    }
}
