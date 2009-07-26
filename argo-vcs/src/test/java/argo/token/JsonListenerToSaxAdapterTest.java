package argo.token;

import org.hamcrest.Description;
import static org.hamcrest.Matchers.equalTo;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.StringWriter;

public final class JsonListenerToSaxAdapterTest {

    private final Mockery context = new Mockery();
    private static final TypeSafeMatcher<Attributes> EMPTY_ATTRIBUTES_MATCHER = new TypeSafeMatcher<Attributes>() {
        public boolean matchesSafely(final Attributes attributes) {
            return attributes.getLength() == 0;
        }

        public void describeTo(Description description) { }
    };

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

    @Test
    public void aFieldProducesMatchingStartAndEndTags() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("Mary")), with(equalTo("Mary")), with(EMPTY_ATTRIBUTES_MATCHER)); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "Mary", "Mary"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.startField("Mary");
        jsonListenerToSaxAdapter.endField();
        context.assertIsSatisfied();
    }
}
