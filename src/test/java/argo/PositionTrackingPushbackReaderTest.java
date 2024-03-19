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

import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.*;

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
    void afterACharacterHasBeenPushedBackItCanBeRead() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        final int character = positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unread(character);
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'F'));
    }

    @Test
    void positionIsCorrectAfterPushingBackASingleCharacter() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Hello"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
        assertThat(positionTrackingPushbackReader.column(), equalTo(4));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackFourCharacters() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Hello"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('o');
        positionTrackingPushbackReader.unread('l');
        positionTrackingPushbackReader.unread('l');
        positionTrackingPushbackReader.unread('e');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingACarriageReturnLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterPushingBackALineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.column(), equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.column(), equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterPushingBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackLineFeed() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackCarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\r');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingAPushedBackLineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        positionTrackingPushbackReader.read();
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void canReadPastEndOfStreamAndThenPushBackALineFeedThatFollowedACarriageReturn() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 6; i++) {
            positionTrackingPushbackReader.read();
        }
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void continuingToReadPastEndOfStreamDoesNotAlterPosition() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        for (int i = 0; i < 3; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'o'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(3));
        assertThat(positionTrackingPushbackReader.column(), equalTo(3));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\r'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedThatFollowedACarriageReturnAndRereading() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        for (int i = 0; i < 3; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('o');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'o'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(4));
        assertThat(positionTrackingPushbackReader.column(), equalTo(4));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(1));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\n"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackACarriageReturnAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r"));
        for (int i = 0; i < 4; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\r');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\r'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    void positionIsCorrectAfterReadingPastEndOfStreamThenPushingBackALineFeedThatFollowedACarriageReturnAndRereadingPastEndOfStream() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar\r\n"));
        for (int i = 0; i < 5; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread('\n');
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
        assertThat(positionTrackingPushbackReader.column(), equalTo(1));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
        assertThat(positionTrackingPushbackReader.line(), equalTo(2));
    }

    @Test
    @Tag("slow")
    void handlesMoreCarriageReturnsThanLargestInteger() throws IOException {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new InfiniteReader('\r'));
        for (int i = -1; i != Integer.MAX_VALUE; i++) {
            positionTrackingPushbackReader.read();
        }
        assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
        assertThat(positionTrackingPushbackReader.column(), equalTo(0));
        assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
        assertThat(positionTrackingPushbackReader.line(), equalTo(-1));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    final class ColumnOverflow {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new BoundedRepeatingReader('a', Integer.MAX_VALUE),
                new StringReader("a\n")
        ));

        @BeforeAll
        void overflowColumn() throws Exception {
            for (int i = -1; i != Integer.MAX_VALUE; i++) {
                positionTrackingPushbackReader.read();
            }
        }

        @Test
        @Order(0)
        @Tag("slow")
        void handlesLinesLongerThanLargestInteger() {
            assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
            assertThat(positionTrackingPushbackReader.column(), equalTo(-1));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
            assertThat(positionTrackingPushbackReader.line(), equalTo(1));
        }

        @Test
        @Order(1)
        @Tag("slow")
        void canUnreadAfterLineLongerThanLargestInteger() throws IOException {
            positionTrackingPushbackReader.unread('a');
            assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
            assertThat(positionTrackingPushbackReader.column(), equalTo(-1));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
            assertThat(positionTrackingPushbackReader.line(), equalTo(1));
            positionTrackingPushbackReader.read(); // reset positionTrackingPushbackReader for next test
        }

        @Test
        @Order(2)
        @Tag("slow")
        void afterLineLongerThanLargestIntegerNextLineHasCorrectPosition() throws IOException {
            assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
            assertThat(positionTrackingPushbackReader.position().column, equalTo(0));
            assertThat(positionTrackingPushbackReader.column(), equalTo(0));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(2));
            assertThat(positionTrackingPushbackReader.line(), equalTo(2));
        }

        @Test
        @Order(3)
        @Tag("slow")
        void pushingBackNewlineAfterLineLongerThanLargestIntegerFollowedByNewlineHasCorrectPosition() {
            positionTrackingPushbackReader.unread('\n');
            assertThat(positionTrackingPushbackReader.position().column, equalTo(-1));
            assertThat(positionTrackingPushbackReader.column(), equalTo(-1));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(1));
            assertThat(positionTrackingPushbackReader.line(), equalTo(1));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    final class LineOverflow {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new SequenceReader(
                new BoundedRepeatingReader('\n', Integer.MAX_VALUE - 1),
                new StringReader("ab\n"),
                new StringReader("a\n"),
                new StringReader("\n")
        ));

        @BeforeAll
        void overflowLines() throws Exception {
            for (int i = -1; i != Integer.MAX_VALUE; i++) {
                positionTrackingPushbackReader.read();
            }
        }

        @Test
        @Order(0)
        @Tag("slow")
        void pushingBackNewlineAfterMoreLinesThanLargestIntegerHasCorrectPosition() throws IOException {
            assertThat(positionTrackingPushbackReader.position().column, equalTo(2));
            assertThat(positionTrackingPushbackReader.column(), equalTo(2));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(Integer.MAX_VALUE));
            assertThat(positionTrackingPushbackReader.line(), equalTo(Integer.MAX_VALUE));
            assertThat(positionTrackingPushbackReader.read(), equalTo((int) '\n'));
            positionTrackingPushbackReader.unread('\n');
            assertThat(positionTrackingPushbackReader.position().column, equalTo(2));
            assertThat(positionTrackingPushbackReader.column(), equalTo(2));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
            assertThat(positionTrackingPushbackReader.line(), equalTo(-1));
            positionTrackingPushbackReader.read(); // advance positionTrackingPushbackReader for next test
        }

        @Test
        @Order(1)
        @Tag("slow")
        void afterMoreLinesThanLargestIntegerNextColumnIsCorrect() throws IOException {
            assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'a'));
            assertThat(positionTrackingPushbackReader.position().column, equalTo(1));
            assertThat(positionTrackingPushbackReader.column(), equalTo(1));
            assertThat(positionTrackingPushbackReader.position().line, equalTo(-1));
            assertThat(positionTrackingPushbackReader.line(), equalTo(-1));
        }

    }

}