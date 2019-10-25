/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PositionTrackingPushbackReaderTest {

    @Test
    void readingACharacterFromAnEmptyReaderReturnsMinusOne() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void readingAFixedNumberOfCharactersFromAnEmptyReaderReturnsZero() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(new char[1]), equalTo(-1));
    }

    @Test
    void pushingBackMinusOneCausesNextCharacterToBeMinusOne() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unreadLastCharacter();
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeRead() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'F'));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeReadIntoABuffer() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        final char[] buffer = new char[3];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo("Bar".toCharArray()));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeReadIntoABufferThatIsTooLarge() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        final char[] buffer = new char[4];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r', 0}));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void countsAreCorrectAfterReadingIntoABufferThatIsTooLarge() {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read(new char[10]);
        assertThat(positionTrackingPushbackReader.getColumn(), equalTo(4));
    }
}