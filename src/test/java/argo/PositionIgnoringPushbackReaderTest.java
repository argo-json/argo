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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PositionIgnoringPushbackReaderTest {

    @Test
    void readingACharacterFromAnEmptyReaderReturnsMinusOne() throws IOException {
        final PositionIgnoringPushbackReader positionTrackingPushbackReader = new PositionIgnoringPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeRead() throws IOException {
        final PositionIgnoringPushbackReader positionTrackingPushbackReader = new PositionIgnoringPushbackReader(new StringReader("Foo"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'F'));
    }

    @Test
    void positionIsAlwaysMinusOne() throws IOException {
        final PositionIgnoringPushbackReader positionTrackingPushbackReader = new PositionIgnoringPushbackReader(new StringReader("Foo"));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(-1));
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(-1));
    }

    @Test
    void continuingToReadPastEndOfStreamContinuesToReturnMinus1() throws IOException {
        final PositionIgnoringPushbackReader positionTrackingPushbackReader = new PositionIgnoringPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void canReadPastEndOfStreamThenPushingBackAndReread() throws IOException {
        final PositionIgnoringPushbackReader positionTrackingPushbackReader = new PositionIgnoringPushbackReader(new StringReader("Foo"));
        for (int i = 0; i < 3; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'o'));
    }

}