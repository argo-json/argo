package argo.staj;

import javax.xml.stream.Location;
import java.io.Reader;

public final class JsonStreamReaderTokenizer implements JsonStreamReader {

    private final Reader reader;

    public JsonStreamReaderTokenizer(final Reader reader) {
        this.reader = reader;
    }

    public JsonStreamElementType next() throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void require(final JsonStreamElementType type, final String namespaceURI, final String localName) throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getElementText() throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public JsonStreamElementType nextTag() throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasNext() throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void close() throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isStartElement() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isEndElement() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isCharacters() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isWhiteSpace() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public JsonStreamElementType getEventType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getText() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public char[] getTextCharacters() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws JsonStreamException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getTextStart() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getTextLength() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getEncoding() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasText() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Location getLocation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String getLocalName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
