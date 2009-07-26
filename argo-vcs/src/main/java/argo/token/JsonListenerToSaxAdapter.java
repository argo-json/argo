package argo.token;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Stack;

public final class JsonListenerToSaxAdapter implements JsonListener {

    private final ContentHandler contentHandler;
    private final Stack<String> fieldNames = new Stack<String>();

    public JsonListenerToSaxAdapter(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void startDocument() throws JsonListenerException {
        try {
            contentHandler.startDocument();
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void endDocument() throws JsonListenerException {
        try {
            contentHandler.endDocument();
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void startArray() {
        //do nothing
    }

    public void endArray() {
        //do nothing
    }

    public void startObject() {
        //do nothing
    }

    public void endObject() {
        //do nothing
    }

    public void startField(final String name) throws JsonListenerException {
        fieldNames.push(name);
        try {
            contentHandler.startElement("", name, name, new AttributesImpl());
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void endField() throws JsonListenerException {
        final String name = fieldNames.pop();
        try {
            contentHandler.endElement("", name, name);
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void stringValue(final String value) throws JsonListenerException {
        aValue(value, "string");
    }

    public void numberValue(final String value) throws JsonListenerException {
        aValue(value, "number");
    }

    public void trueValue() throws JsonListenerException {
        aValue("true", "true");
    }

    public void falseValue() throws JsonListenerException {
        aValue("false", "false");
    }

    public void nullValue() throws JsonListenerException {
        aValue("null", "null");
    }

    private void aValue(final String value, final String type) throws JsonListenerException {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "type", "type", "CDATA", type);
        try {
            contentHandler.startElement("", value, value, attributes);
            contentHandler.endElement("", value, value);
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }
}
