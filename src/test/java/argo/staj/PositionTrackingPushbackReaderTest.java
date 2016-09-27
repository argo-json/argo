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
    public void readingAFixedNumberOfCharactersFromAnEmptyReaderZero() throws Exception {
        final PositionTrackingPushbackReader positionTrackingPushbackReader = new PositionTrackingPushbackReader(new StringReader(""));
        assertThat(positionTrackingPushbackReader.read(new char[1]), equalTo(0));
    }
}