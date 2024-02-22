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
        assertThat(positionTrackingPushbackReader.read(new char[1]), equalTo(-1));
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
        assertThat(buffer, equalTo(new char[] {'B', 'a', 'r'}));
    }

    @Test
    void afterACharacterHasBeenPushedBackItCanBeReadIntoAnOversizeBuffer() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        final char[] buffer = new char[4];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r', 0}));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void positionIsCorrectAfterReadingIntoAnOversizeBuffer() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
    }

    @Test
    void positionIsCorrectAfterReadingALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingACarriageReturnLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterPushingBackALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackCarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackLineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void canReadPastEndOfStreamAndThenPushBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[6]);
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
        assertThat(buffer, equalTo(new char[] {'B', 'a', 'r'}));
    }

    @Test
    void readToOversizeBufferReturnsCorrectLengthEvenIfDelegateStallsPartWay() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new ChoppingReader(new StringReader("Bar")));
        final char[] buffer = new char[4];
        final int readSize = positionTrackingPushbackReader.read(buffer);
        assertThat(readSize, equalTo(3));
        assertThat(buffer, equalTo(new char[] {'B', 'a', 'r', 0}));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    void continuingToReadPastEndOfStreamDoesNotAlterPosition() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        assertThat(positionTrackingPushbackReader.read(new char[3]), equalTo(3));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'o'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\r'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedThatFollowedACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        assertThat(positionTrackingPushbackReader.read(new char[3]), equalTo(3));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'o'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackACarriageReturnAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\r'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedThatFollowedACarriageReturnAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

}