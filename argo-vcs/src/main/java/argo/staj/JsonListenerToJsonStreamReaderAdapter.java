package argo.staj;

import argo.token.JsonListener;
import argo.token.JsonListenerException;
import argo.token.JsonParser;

import javax.xml.stream.Location;
import java.io.IOException;
import java.io.Reader;

public final class JsonListenerToJsonStreamReaderAdapter implements JsonStreamReader {

    private final JsonParser jsonParser;

    public JsonListenerToJsonStreamReaderAdapter(final Reader in) {
        this.jsonParser = new JsonParser();
        new Thread() {
            public void run() {
                final JsonListener blockingJsonListener = new BlockingJsonListener();
                try {
                    jsonParser.parse(in, blockingJsonListener);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (JsonListenerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }.run();
    }

    public JsonStreamElementType next() throws JsonStreamException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void require(JsonStreamElementType type, String namespaceURI, String localName) throws JsonStreamException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getElementText() throws JsonStreamException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JsonStreamElementType nextTag() throws JsonStreamException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasNext() throws JsonStreamException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws JsonStreamException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isStartElement() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isEndElement() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isCharacters() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWhiteSpace() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JsonStreamElementType getEventType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public char[] getTextCharacters() {
        return new char[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws JsonStreamException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getTextStart() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getTextLength() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getEncoding() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasText() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Location getLocation() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getLocalName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasName() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
