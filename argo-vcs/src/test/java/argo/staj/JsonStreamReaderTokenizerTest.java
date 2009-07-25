package argo.staj;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public final class JsonStreamReaderTokenizerTest {

    @Test
    public void canParseASimpleJsonStream() throws Exception {
        final Reader reader = new StringReader("{\"Hello\":\"World\"}");
        final JsonStreamReaderTokenizer jsonStreamReaderTokenizer = new JsonStreamReaderTokenizer(reader);
        assertThat(jsonStreamReaderTokenizer.next(), equalTo(JsonStreamElementType.START_DOCUMENT));
        assertThat(jsonStreamReaderTokenizer.next(), equalTo(JsonStreamElementType.START_ELEMENT));
        assertThat(jsonStreamReaderTokenizer.hasName(), equalTo(true));
        assertThat(jsonStreamReaderTokenizer.getLocalName(), equalTo("Hello"));
        assertThat(jsonStreamReaderTokenizer.hasText(), equalTo(true));
        assertThat(jsonStreamReaderTokenizer.getElementText(), equalTo("World"));
        assertThat(jsonStreamReaderTokenizer.next(), equalTo(JsonStreamElementType.END_ELEMENT));
        assertThat(jsonStreamReaderTokenizer.next(), equalTo(JsonStreamElementType.END_DOCUMENT));
    }
}
