package argo.token;

import junit.framework.TestCase;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TokenizerTest extends TestCase {

    public void testTokenizesValidString() throws Exception {
        final String inputString = "\"hello world\"";
        final JsonString result = Tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("hello world"), result);
    }

    public void testTokenizesValidStringWithEscapedChars() throws Exception {
        final String inputString = "\"\\\"hello world\\\"\"";
        final JsonString result = Tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("\"hello world\""), result);
    }

    public void testTokenizesValidStringWithEscapedUnicodeChars() throws Exception {
        final String inputString = "\"hello world \\uF001\"";
        final JsonString result = Tokenizer.stringToken(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonString("hello world \uF001"), result);
    }

    public void testTokenizesStringWithInvalidEscapedUnicodeChars() throws Exception {
        final String inputString = "\"hello world \\uF0\"";
        try {
            Tokenizer.stringToken(new StringReader(inputString));
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    public void testTokenizesInvalidString() throws Exception {
        final String inputString = "hello world\"";
        try {
            Tokenizer.stringToken(new StringReader(inputString));
            fail("Attempting to parse an invalid String [" + inputString + "] should throw an InvalidSyntaxException.");
        } catch (final InvalidSyntaxException e) {
            // expect to end up here
        }
    }

    public void testTokenizesValidNumber() throws Exception {
        final String inputString = "12.123E-2";
        final JsonNumber result = Tokenizer.numberToken(new PushbackReader(new StringReader(inputString)));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonNumber(inputString), result);
    }

    public void testTokenizesSimpleJsonStringValue() throws Exception {
        final String inputString = "{\"hello\":\"world\"}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("hello"), (JsonValue) new JsonString("world"))), result);
    }

    public void testTokenizesSimpleJsonStringValueWithWhitespace() throws Exception {
        final String inputString = "{\"hello\": \"world\"}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("hello"), (JsonValue) new JsonString("world"))), result);
    }

    public void testTokenizesMultiElementArrayWithWhitespace() throws Exception {
        final String inputString = "[ 1, 2 ]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Arrays.<JsonValue>asList(new JsonNumber("1"), new JsonNumber("2"))), result);
    }

    public void testTokenizesJsonStringValuePair() throws Exception {
        final String inputString = "{\"hello\":\"world\",\"foo\":\"bar\"}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("hello"), new JsonString("world"));
        expectedFields.put(new JsonString("foo"), new JsonString("bar"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
    }

    public void testTokenizesSimpleJsonNumberValue() throws Exception {
        final String inputString = "{\"room\":101}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("room"), (JsonValue) new JsonNumber("101"))), result);
    }

    public void testTokenizesSimpleJsonNull() throws Exception {
        final String inputString = "{\"points\":null}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.NULL)), result);
    }

    public void testTokenizesSimpleJsonTrue() throws Exception {
        final String inputString = "{\"points\":true}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.TRUE)), result);
    }

    public void testTokenizesSimpleJsonFalse() throws Exception {
        final String inputString = "{\"points\":false}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.singletonMap(new JsonString("points"), (JsonValue) JsonConstants.FALSE)), result);
    }

    public void testTokenizesJsonNumberValuePair() throws Exception {
        final String inputString = "{\"room\":101,\"answer\":42}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("room"), new JsonNumber("101"));
        expectedFields.put(new JsonString("answer"), new JsonNumber("42"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
    }

    public void testTokenizesJsonMixedPair() throws Exception {
        final String inputString = "{\"room\":101,\"foo\":\"bar\"}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        final Map<JsonString, JsonValue> expectedFields = new HashMap<JsonString, JsonValue>();
        expectedFields.put(new JsonString("room"), new JsonNumber("101"));
        expectedFields.put(new JsonString("foo"), new JsonString("bar"));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(expectedFields), result);
    }

    public void testTokenizesEmptyObject() throws Exception {
        final String inputString = "{}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.<JsonString, JsonValue>emptyMap()), result);
    }

    public void testTokenizesNestedObject() throws Exception {
        final String inputString = "{\"Test\":{\"Inner test\":12}}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Inner test"), new JsonNumber("12"))))), result);
    }

    public void testTokenizesEmptyArray() throws Exception {
        final String inputString = "[]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Collections.<JsonValue>emptyList()), result);
    }

    public void testTokenizesSingleElementArray() throws Exception {
        final String inputString = "[12]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Collections.singletonList((JsonValue) new JsonNumber("12"))), result);
    }

    public void testTokenizesMultiElementArray() throws Exception {
        final String inputString = "[12,\"test\"]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Arrays.asList(new JsonNumber("12"), new JsonString("test"))), result);
    }

    public void testTokenizesNestedArray() throws Exception {
        final String inputString = "[[12]]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Collections.singletonList((JsonValue) new JsonArray(Collections.singletonList((JsonValue) new JsonNumber("12"))))), result);
    }

    public void testTokenizesObjectWithArrayValue() throws Exception {
        final String inputString = "{\"Test\":[12]}";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new JsonArray(Collections.<JsonValue>singletonList(new JsonNumber("12"))))), result);
    }

    public void testTokenizesArrayWithObjectElement() throws Exception {
        final String inputString = "[{\"Test\":12}]";
        final JsonValue result = Tokenizer.json(new StringReader(inputString));
        assertEquals("Tokenizing String [" + inputString + "].", new JsonArray(Collections.<JsonValue>singletonList(new JsonObject(Collections.<JsonString, JsonValue>singletonMap(new JsonString("Test"), new JsonNumber("12"))))), result);
    }
}
