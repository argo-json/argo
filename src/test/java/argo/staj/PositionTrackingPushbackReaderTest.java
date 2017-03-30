package argo.staj;

import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PositionTrackingPushbackReaderTest {

    @Test
    public void readingACharacterFromAnEmptyReaderReturnsMinusOne() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    public void readingAFixedNumberOfCharactersFromAnEmptyReaderReturnsZero() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(new char[1]), equalTo(-1));
    }

    @Test
    public void pushingBackMinusOneCausesNextCharacterToBeMinusOne() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unreadLastCharacter();
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    public void afterACharacterHasBeenPushedBackItCanBeRead() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Foo"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        assertThat(positionTrackingPushbackReader.read(), equalTo((int) 'F'));
    }

    @Test
    public void afterACharacterHasBeenPushedBackItCanBeReadIntoABuffer() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        final char[] buffer = new char[3];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo("Bar".toCharArray()));
    }

    @Test
    public void afterACharacterHasBeenPushedBackItCanBeReadIntoABufferThatIsTooLarge() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read();
        positionTrackingPushbackReader.unreadLastCharacter();
        final char[] buffer = new char[4];
        assertThat(positionTrackingPushbackReader.read(buffer), equalTo(3));
        assertThat(buffer, equalTo(new char[]{'B', 'a', 'r', 0}));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }

    @Test
    public void countsAreCorrectAfterReadingIntoABufferThatIsTooLarge() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader("Bar"));
        positionTrackingPushbackReader.read(new char[10]);
        assertThat(positionTrackingPushbackReader.getColumn(), equalTo(4));
    }
}