package argo.xml;

import argo.saj.JsonListener;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Stack;

/**
 * Makes a stream of JsonListener events into a stream of XML ContentHandler events, as per the BadgerFish convention.
 */
public final class JsonListenerToSaxAdapter implements JsonListener {

    private final ContentHandler contentHandler;
    private final Stack<String> fieldNames = new Stack<String>();

    public JsonListenerToSaxAdapter(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void startDocument() {
        try {
            contentHandler.startDocument();
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void endDocument() {
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

    public void startField(final String name) {
        fieldNames.push(name);
        try {
            contentHandler.startElement("", name, name, new AttributesImpl());
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void endField() {
        final String name = fieldNames.pop();
        try {
            contentHandler.endElement("", name, name);
        } catch (final SAXException e) {
            throw new JsonListenerException(e);
        }
    }

    public void stringValue(final String value) {
        aValue(value, "string");
    }

    public void numberValue(final String value) {
        aValue(value, "number");
    }

    public void trueValue() {
        aValue("true", "true");
    }

    public void falseValue() {
        aValue("false", "false");
    }

    public void nullValue() {
        aValue("null", "null");
    }

    private void aValue(final String value, final String type) {
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
