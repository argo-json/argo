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

import argo.ChoppingReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PositionTrackingPushbackReaderTest {

    @Test
    void readingACharacterFromAnEmptyReaderReturnsMinusOne() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void readingFromAnEmptyReaderIntoABufferReturnsMinusOne() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        final char[] buffer = new char[1];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(-1));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeRead() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'F'));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeReadIntoABuffer() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        final char[] buffer = new char[3];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo("Bar".toCharArray()));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeReadIntoABufferThatIsTooLarge() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        final char[] buffer = new char[4];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r', 0}));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void countsAreCorrectAfterReadingIntoABufferThatIsTooLarge() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
    }

    @Test
    void countsAreCorrectAfterReadingALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterReadingACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterReadingACarriageReturnLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        final char[] buffer = new char[5];
        positionTrackingPushbackReader.read(buffer);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterPushingBackALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void countsAreCorrectAfterPushingBackACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void countsAreCorrectAfterPushingBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        final char[] buffer = new char[5];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterReadingAPushedBackLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterReadingAPushedBackCarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        final char[] buffer = new char[4];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void countsAreCorrectAfterReadingAPushedBackLineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        final char[] buffer = new char[5];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void canReadPastEndOfStreamAndThenPushBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        final char[] buffer = new char[6];
        positionTrackingPushbackReader.read(buffer);
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void readsFullComplementOfCharactersEvenIfDelegateStallsPartWay() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new ChoppingReader(new StringReader("Bar")));
        final char[] buffer = new char[3];
        final int readSize = positionTrackingPushbackReader.read(buffer);
        assertThat(readSize, equalTo(3));
        assertThat(buffer[0], equalTo('B'));
        assertThat(buffer[1], equalTo('a'));
        assertThat(buffer[2], equalTo('r'));
    }

    @Test
    void readToOversizeBufferReturnsCorrectLengthEvenIfDelegateStallsPartWay() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new ChoppingReader(new StringReader("Bar")));
        final char[] buffer = new char[10];
        final int readSize = positionTrackingPushbackReader.read(buffer);
        assertThat(readSize, equalTo(3));
        assertThat(buffer[0], equalTo('B'));
        assertThat(buffer[1], equalTo('a'));
        assertThat(buffer[2], equalTo('r'));
    }
}