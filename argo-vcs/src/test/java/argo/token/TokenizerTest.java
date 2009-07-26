package argo.token;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TokenizerTest {
    private final Mockery context = new Mockery();
    private final Tokenizer tokenizer = new Tokenizer(new SystemOutJsonListener());

    @Test
    public void tokenizesValidString() throws Exception {
        final String inputString = "\"hello world\"";
        final JsonString result = tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("hello world"), result);
        context.assertIsSatisfied();
    }

    @Test
    public void testTokenizesValidStringWithEscapedChars() throws Exception {
        final String inputString = "\"\\\"hello world\\\"\"";
        final JsonString result = tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("\"hello world\""), result);
    }

    @Test
    public void testTokenizesValidStringWithEscapedUnicodeChars() throws Exception {
        final String inputString = "\"hello world \\uF001\"";
        final JsonString result = tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("hello world \uF001"), result);
    }

    @Test
    public void testTokenizesStringWithInvalidEscapedUnicodeChars() throws Exception {
        final String inputString = "\"hello world \\uF0\"";
        try {
            tokenizer.stringToken(new StringReader(inputString));
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    @Test
    public void testTokenizesInvalidString() throws Exception {
        final String inputString = "hello world\"";
        try {
            tokenizer.stringToken(new StringReader(inputString));
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    @Test
    public void testTokenizesValidNumber() throws Exception {
        final String inputString = "12.123E-2";
        final JsonNumber result = tokenizer.numberToken(new PushbackReader(new StringReader(inputString)));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonNumber(inputString), result);
    }

    @Test
    public void testTokenizesSimpleJsonStringValue() throws Exception {
        final String inputString = "{\"hello\":\"world\"}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("hello"), (JsonValue) new JsonString("world"))), result);
    }

    @Test
    public void testTokenizesSimpleJsonStringValueWithWhitespace() throws Exception {
        final String inputString = "{\"hello\": \"world\"}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("hello"), (JsonValue) new JsonString("world"))), result);
    }

    @Test
    public void testTokenizesMultiElementArrayWithWhitespace() throws Exception {
        final String inputString = "[ 1, 2 ]";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Arrays.<JsonValue>asList(new JsonNumber("1"), new JsonNumber("2"))), result);
    }

    @Test
    public void testTokenizesJsonStringValuePair() throws Exception {
        final String inputString = "{\"hello\":\"world\",\"foo\":\"bar\"}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("hello"), new JsonString("world"));
        expectedFields.put(new JsonString("foo"), new JsonString("bar"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
    }

    @Test
    public void testTokenizesSimpleJsonNumberValue() throws Exception {
        final String inputString = "{\"room\":101}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("room"), (JsonValue) new JsonNumber("101"))), result);
    }

    @Test
    public void testTokenizesSimpleJsonNull() throws Exception {
        final String inputString = "{\"points\":null}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.NULL)), result);
    }

    @Test
    public void testTokenizesSimpleJsonTrue() throws Exception {
        final String inputString = "{\"points\":true}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.TRUE)), result);
    }

    @Test
    public void testTokenizesSimpleJsonFalse() throws Exception {
        final String inputString = "{\"points\":false}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.FALSE)), result);
    }

    @Test
    public void testTokenizesJsonNumberValuePair() throws Exception {
        final String inputString = "{\"room\":101,\"answer\":42}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("room"), new JsonNumber("101"));
        expectedFields.put(new JsonString("answer"), new JsonNumber("42"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
    }

    @Test
    public void testTokenizesJsonMixedPair() throws Exception {
        final String inputString = "{\"room\":101,\"foo\":\"bar\"}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("room"), new JsonNumber("101"));
        expectedFields.put(new JsonString("foo"), new JsonString("bar"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
        context.assertIsSatisfied();
    }

    @Test
    public void testTokenizesObjectWithArrayValue() throws Exception {
        final String inputString = "{\"Test\":[12]}";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new JsonArray(Collections.<JsonValue>singletonList(new JsonNumber("12"))))), result);
    }

    @Test
    public void testTokenizesArrayWithObjectElement() throws Exception {
        final String inputString = "[{\"Test\":12}]";
        final JsonValue result = tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Collections.<JsonValue>singletonList(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new JsonNumber("12"))))), result);
    }

    @Test
    public void rejectsTrailingNonWhitespaceCharacters() throws Exception {
        final String inputString = "[]whoops";
        try {
            tokenizer.json(new StringReader(inputString));
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
        new Tokenizer(jsonListener).json(new StringReader(inputString));
        context.assertIsSatisfied();
    }

}
