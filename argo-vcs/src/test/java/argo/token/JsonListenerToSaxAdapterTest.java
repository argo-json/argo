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

public final class JsonListenerToSaxAdapterTest {

    private final Mockery context = new Mockery();
    private static final TypeSafeMatcher<Attributes> EMPTY_ATTRIBUTES_MATCHER = new TypeSafeMatcher<Attributes>() {
        public boolean matchesSafely(final Attributes attributes) {
            return attributes.getLength() == 0;
        }

        public void describeTo(Description description) {
            description.appendText("empty Attributes");
        }
    };

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

    @Test
    public void nestedFieldsProduceNestedValues() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("Mary")), with(equalTo("Mary")), with(EMPTY_ATTRIBUTES_MATCHER)); inSequence(expectedSequence);
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("Martha")), with(equalTo("Martha")), with(EMPTY_ATTRIBUTES_MATCHER)); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "Martha", "Martha"); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "Mary", "Mary"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.startField("Mary");
        jsonListenerToSaxAdapter.startField("Martha");
        jsonListenerToSaxAdapter.endField();
        jsonListenerToSaxAdapter.endField();
        context.assertIsSatisfied();
    }

    @Test
    public void stringValueProducesAnElementWithCorrectTypeAttribute() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("Foo")), with(equalTo("Foo")), with(new SingleAttributeMatcher("type", "string"))); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "Foo", "Foo"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.stringValue("Foo");
        context.assertIsSatisfied();
    }

    @Test
    public void numberValueProducesAnElementWithCorrectTypeAttribute() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("12")), with(equalTo("12")), with(new SingleAttributeMatcher("type", "number"))); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "12", "12"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.numberValue("12");
        context.assertIsSatisfied();
    }

    @Test
    public void nullValueProducesAnElementWithCorrectTypeAttribute() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("null")), with(equalTo("null")), with(new SingleAttributeMatcher("type", "null"))); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "null", "null"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.nullValue();
        context.assertIsSatisfied();
    }

    @Test
    public void trueValueProducesAnElementWithCorrectTypeAttribute() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("true")), with(equalTo("true")), with(new SingleAttributeMatcher("type", "true"))); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "true", "true"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.trueValue();
        context.assertIsSatisfied();
    }

    @Test
    public void falseValueProducesAnElementWithCorrectTypeAttribute() throws Exception {
        final ContentHandler contentHandler = context.mock(ContentHandler.class);
        final JsonListenerToSaxAdapter jsonListenerToSaxAdapter = new JsonListenerToSaxAdapter(contentHandler);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(contentHandler).startElement(with(equalTo("")), with(equalTo("false")), with(equalTo("false")), with(new SingleAttributeMatcher("type", "false"))); inSequence(expectedSequence);
            oneOf(contentHandler).endElement("", "false", "false"); inSequence(expectedSequence);
        }});
        jsonListenerToSaxAdapter.falseValue();
        context.assertIsSatisfied();
    }

    private static final class SingleAttributeMatcher extends TypeSafeMatcher<Attributes> {
        private final String attributeName;
        private final String attributeValue;

        public SingleAttributeMatcher(final String attributeName, final String attributeValue) {
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }


        public boolean matchesSafely(final Attributes attributes) {
            return attributes.getLength() == 1 && (attributeName.equals(attributes.getLocalName(0)) && attributeValue.equals(attributes.getValue(0)));
        }

        public void describeTo(final Description description) {
            description.appendText("an attributes with a single entry, named \"" + attributeName + "\" with value \"" + attributeValue + "\"");
        }
    }
}
