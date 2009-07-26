package argo.token;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.StringWriter;

public final class JsonListenerToSaxAdapterTest {

    @Test
    public void handlesHelloWorldExample() throws Exception {
         new JsonParser().parse(new StringReader("{\"hello\":\"world\"}"), new JsonListenerToSaxAdapter(new ContentHandler() {
             public void setDocumentLocator(Locator locator) {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void startDocument() throws SAXException {
                 System.out.println("<xml>");
             }

             public void endDocument() throws SAXException {
                 System.out.println("</xml>");
             }

             public void startPrefixMapping(String prefix, String uri) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void endPrefixMapping(String prefix) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                 StringWriter element = new StringWriter();
                 element.append("<").append(localName);
                 for (int i = 0; i < atts.getLength(); i++) {
                     element.append(" ");
                     element.append(atts.getLocalName(i));
                     element.append("=\"");
                     element.append(atts.getValue(i));
                     element.append("\"");
                 }
                 element.append(">");
                 System.out.println(element);
             }

             public void endElement(String uri, String localName, String qName) throws SAXException {
                 System.out.println("</" + localName + ">");
             }

             public void characters(char[] ch, int start, int length) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void processingInstruction(String target, String data) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }

             public void skippedEntity(String name) throws SAXException {
                 throw new UnsupportedOperationException("Not implemented yet");
             }
         }));
    }
}
