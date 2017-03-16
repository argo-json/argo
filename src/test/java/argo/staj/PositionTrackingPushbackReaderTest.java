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
        assertThat(positionTrackingPushbackReader.read(new char[1]), equalTo(0));
    }

    @Test
    public void pushingBackMinusOneCausesNextCharacterToBeMinusOne() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
        positionTrackingPushbackReader.unread(-1);
        assertThat(positionTrackingPushbackReader.read(), equalTo(-1));
    }
}