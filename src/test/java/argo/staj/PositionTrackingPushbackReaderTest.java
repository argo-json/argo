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
import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.Disabled;
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
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r'}));
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
    void positionIsCorrectAfterPushingBackASingleCharacter() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Hello"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackFourCharacters() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Hello"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('o');
        positionTrackingPushbackReader.unread('l');
        positionTrackingPushbackReader.unread('l');
        positionTrackingPushbackReader.unread('e');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    @Disabled("Decide about this")
    void positionIsCorrectAfterPushingBackFourNewlines() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("H\n\n\n\n"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    @Disabled("Decide about this")
    void positionIsCorrectAfterPushingBackFourCarriageReturns() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("H\r\r\r\r"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    @Disabled("Decide about this")
    void positionIsCorrectAfterPushingBackTwoCarriageReturnNewlines() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("H\r\n\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
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
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r'}));
    }

    @Test
    void readToOversizeBufferReturnsCorrectLengthEvenIfDelegateStallsPartWay() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new ChoppingReader(new StringReader("Bar")));
        final char[] buffer = new char[4];
        final int readSize = positionTrackingPushbackReader.read(buffer);
        assertThat(readSize, equalTo(3));
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r', 0}));
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
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'o'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        positionTrackingPushbackReader.read(new char[4]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\r'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedThatFollowedACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        positionTrackingPushbackReader.read(new char[5]);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        assertThat(positionTrackingPushbackReader.read(new char[3]), equalTo(3));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'o'));
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
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
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
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\r'));
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
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void handlesLinesLongerThanLargestInteger() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new InfiniteReader('a'));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(-1)); // TODO Needs documenting
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void afterLineLongerThanLargestIntegerNextLineHasCorrectPosition() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new FastBoundedReader(new InfiniteReader('a'), Integer.MAX_VALUE),
                new StringReader("a\n")
        ));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
    }

    @Test
    void pushingBackNewlineAfterLineLongerThanLargestIntegerFollowedByNewlineHasCorrectPosition() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new FastBoundedReader(new InfiniteReader('a'), Integer.MAX_VALUE),
                new StringReader("a\n")
        ));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void canUnreadAfterLineLongerThanLargestInteger() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new InfiniteReader('a'));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('a');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
    }

    @Test
    void handlesMoreLinesThanLargestInteger() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new InfiniteReader('\n'));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1)); // TODO Needs documenting
    }

    @Test
    void afterMoreLinesThanLargestIntegerNextColumnIsCorrect() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new FastBoundedReader(new InfiniteReader('\n'), Integer.MAX_VALUE),
                new StringReader("\na")
        ));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'a'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
    }

    @Test
    void pushingBackNewlineAfterMoreLinesThanLargestIntegerHasCorrectPosition() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new FastBoundedReader(new InfiniteReader('\n'), Integer.MAX_VALUE - 1),
                new StringReader("ab\n")
        ));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(2));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(Integer.MAX_VALUE));
        assertThat(positionTrackingPushbackReader.read(), equalTo((int)'\n'));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(2));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
    }

    @Test
    void canUnreadAfterMoreLinesThanLargestInteger() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new InfiniteReader('\n'));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
    }

}