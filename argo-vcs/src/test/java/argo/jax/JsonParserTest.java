package argo.jax;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

public final class JsonParserTest {
    private static final JsonListener BLACK_HOLE_LISTENER = new JsonListener() {
        public void startDocument() { }
        public void endDocument() { }
        public void startArray() { }
        public void endArray() { }
        public void startObject() { }
        public void endObject() { }
        public void startField(String name) { }
        public void endField() { }
        public void stringValue(String value) { }
        public void numberValue(String value) { }
        public void trueValue() { }
        public void falseValue() { }
        public void nullValue() { }
    };
    private final Mockery context = new Mockery();

    @Test
    public void tokenizesValidString() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"hello world\"", "hello world");
    }

    @Test
    public void tokenizesValidStringWithEscapedChars() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"\\\"hello world\\\"\"", "\"hello world\"");
    }

    @Test
    public void tokenizesValidStringWithEscapedUnicodeChars() throws Exception {
        assertJsonValueFragmentResultsInStringValue("\"hello world \\uF001\"", "hello world \uF001");
    }

    @Test
    public void rejectsStringWithInvalidEscapedUnicodeChars() throws Exception {
        final String inputString = "[\"hello world \\uF0\"]";
        try {
            new JaxParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    @Test
    public void rejectsInvalidString() throws Exception {
        final String inputString = "[hello world\"]";
        try {
            new JaxParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    @Test
    public void tokenizesValidNumber() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12.123E-2"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("[12.123E-2]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonStringObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"hello\":\"world\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonStringObjectWithWhitespace() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"hello\": \"world\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesMultiElementArrayWithWhitespace() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("1"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("2"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("[ 1, 2 ]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesJsonStringObjectPair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("hello"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("world"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("foo"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("bar"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"hello\":\"world\",\"foo\":\"bar\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonNumberValue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("room"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"room\":101}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonNull() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("points"); inSequence(expectedSequence);
            oneOf(jsonListener).nullValue(); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"points\":null}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonTrue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("points"); inSequence(expectedSequence);
            oneOf(jsonListener).trueValue(); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"points\":true}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSimpleJsonFalse() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("points"); inSequence(expectedSequence);
            oneOf(jsonListener).falseValue(); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"points\":false}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesJsonNumberValuePair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("room"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("answer"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("42"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"room\":101,\"answer\":42}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesJsonMixedPair() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("room"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("101"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("foo"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("bar"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"room\":101,\"foo\":\"bar\"}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesEmptyObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "{}";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesNestedObject() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test"); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("Inner test"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "{\"Test\":{\"Inner test\":12}}";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesEmptyArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "[]";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesSingleElementArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "[12]";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesMultiElementArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue("test"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "[12,\"test\"]";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesNestedArray() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "[[12]]";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesObjectWithArrayValue() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test"); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("{\"Test\":[12]}"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void tokenizesArrayWithObjectElement() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).startObject(); inSequence(expectedSequence);
            oneOf(jsonListener).startField("Test"); inSequence(expectedSequence);
            oneOf(jsonListener).numberValue("12"); inSequence(expectedSequence);
            oneOf(jsonListener).endField(); inSequence(expectedSequence);
            oneOf(jsonListener).endObject(); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("[{\"Test\":12}]"), jsonListener);
        context.assertIsSatisfied();
    }

    @Test
    public void rejectsTrailingNonWhitespaceCharacters() throws Exception {
        final String inputString = "[]whoops";
        try {
            new JaxParser().parse(new StringReader(inputString), BLACK_HOLE_LISTENER);
            fail("Attempting to parse an String with trailing characters [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    @Test
    public void tokenizesObjectTrailingWhitespaceCharacters() throws Exception {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        final String inputString = "[] ";
        new JaxParser().parse(new StringReader(inputString), jsonListener);
        context.assertIsSatisfied();
    }

    private void assertJsonValueFragmentResultsInStringValue(final String jsonFragment, final String expectedStringValue) throws IOException, JsonListenerException, InvalidSyntaxException {
        final JsonListener jsonListener = context.mock(JsonListener.class);
        final Sequence expectedSequence = context.sequence("expectedSequence");
        context.checking(new Expectations() {{
            oneOf(jsonListener).startDocument(); inSequence(expectedSequence);
            oneOf(jsonListener).startArray(); inSequence(expectedSequence);
            oneOf(jsonListener).stringValue(expectedStringValue); inSequence(expectedSequence);
            oneOf(jsonListener).endArray(); inSequence(expectedSequence);
            oneOf(jsonListener).endDocument(); inSequence(expectedSequence);
        }});
        new JaxParser().parse(new StringReader("[" + jsonFragment + "]"), jsonListener);
        context.assertIsSatisfied();
    }

}
