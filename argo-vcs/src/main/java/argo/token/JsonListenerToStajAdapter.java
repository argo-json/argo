package argo.token;

import argo.staj.JsonStreamElementType;
import argo.staj.JsonStreamException;
import argo.staj.JsonStreamReader;

import javax.xml.stream.Location;

public final class JsonListenerToStajAdapter implements JsonListener, JsonStreamReader {

    public void startDocument() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void endDocument() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void startArray() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void endArray() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void startObject() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void endObject() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void numberValue(final String value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void trueValue() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void falseValue() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void nullValue() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void stringValue(final String value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void startField(final String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void endField() {
        throw new UnsupportedOperationException("Not implemented yet");
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
