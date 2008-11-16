package argo.token;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.*;

public final class Tokenizer {

    private static final char DOUBLE_QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char BACKSPACE = '\b';

    /**
     * Sole, private constructor, to prevent instantiation.
     */
    private Tokenizer() {
        // deliberately empty
    }

    public static JsonValue json(final Reader in) throws IOException {
        final JsonValue result;
        final PushbackReader pushbackReader = new PushbackReader(in);
        final char nextChar = (char) pushbackReader.read();
        switch (nextChar) {
            case '{':
                pushbackReader.unread(nextChar);
                result = objectString(pushbackReader);
                break;
            case '[':
                throw new UnsupportedOperationException("Arrays not supported yet.");
            default:
                throw new InvalidSyntaxException("Expected either [ or { but got [" + nextChar + "].");
        }
        // TODO allow whitespace??
        return result;
    }

    private static JsonObject objectString(final PushbackReader pushbackReader) throws IOException {
        final Map<JsonString, JsonValue> fields = new HashMap<JsonString, JsonValue>();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar != '{') {
            throw new InvalidSyntaxException("Expected object to start with { but got [" + firstChar + "].");
        }
        final char secondChar = (char) pushbackReader.read();
        pushbackReader.unread(secondChar);
        if (secondChar != '}') {
            final JsonField jsonField = aFieldToken(pushbackReader);
            fields.put(jsonField.getName(), jsonField.getValue());
        }
        boolean gotEndOfObject = false;
        while (!gotEndOfObject) {
            final char nextChar = (char) pushbackReader.read();
            switch (nextChar) {
                case ',':
                    final JsonField jsonField = aFieldToken(pushbackReader);
                    fields.put(jsonField.getName(), jsonField.getValue());
                case '}':
                    gotEndOfObject = true;
                    break;
                default:
                    throw new InvalidSyntaxException("Expected either , or } but got [" + nextChar + "].");
            }
        }
        return new JsonObject(fields);
    }

    private static JsonField aFieldToken(final PushbackReader pushbackReader) throws IOException {
        final JsonString name = stringToken(pushbackReader);
        final char separatorChar = (char) pushbackReader.read();
        if (separatorChar != ':') {
            throw new InvalidSyntaxException("Expected object identifier to be followed by : but got [" + separatorChar + "].");
        }
        final JsonValue value;
        final char nextChar = (char) pushbackReader.read();
        switch (nextChar) {
            case '"':
                pushbackReader.unread(nextChar);
                value = stringToken(pushbackReader);
                break;
            case 't':
                final char[] remainingTrueTokenCharacters = new char[3];
                final int trueTokenCharactersRead = pushbackReader.read(remainingTrueTokenCharacters);
                if (trueTokenCharactersRead != 3 || remainingTrueTokenCharacters[0] != 'r' || remainingTrueTokenCharacters[1] != 'u' || remainingTrueTokenCharacters[2] != 'e') {
                    throw new InvalidSyntaxException("Expected 't' to be followed by [[r, u, e]], but got [" + Arrays.toString(remainingTrueTokenCharacters) + "].");
                } else {
                    value = JsonConstants.TRUE;
                }
                break;
            case 'f':
                final char[] remainingFalseTokenCharacters = new char[4];
                final int falseTokenCharactersRead = pushbackReader.read(remainingFalseTokenCharacters);
                if (falseTokenCharactersRead != 4 || remainingFalseTokenCharacters[0] != 'a' || remainingFalseTokenCharacters[1] != 'l' || remainingFalseTokenCharacters[2] != 's' || remainingFalseTokenCharacters[3] != 'e') {
                    throw new InvalidSyntaxException("Expected 'f' to be followed by [[a, l, s, e]], but got [" + Arrays.toString(remainingFalseTokenCharacters) + "].");
                } else {
                    value = JsonConstants.FALSE;
                }
                break;
            case 'n':
                final char[] remainingNullTokenCharacters = new char[3];
                final int nullTokenCharactersRead = pushbackReader.read(remainingNullTokenCharacters);
                if (nullTokenCharactersRead != 3 || remainingNullTokenCharacters[0] != 'u' || remainingNullTokenCharacters[1] != 'l' || remainingNullTokenCharacters[2] != 'l') {
                    throw new InvalidSyntaxException("Expected 'n' to be followed by [[u, l, l]], but got [" + Arrays.toString(remainingNullTokenCharacters) + "].");
                } else {
                    value = JsonConstants.NULL;
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                pushbackReader.unread(nextChar);
                value = numberToken(pushbackReader);
                break;
            default:
                throw new InvalidSyntaxException("Invalid character at start of value [" + nextChar + "].");
        }
        return new JsonField(name, value);
    }

    public static JsonNumber numberToken(final PushbackReader in) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if ('-' == firstChar) {
            result.append('-');
        } else {
            in.unread(firstChar);
        }
        result.append(nonNegativeNumberToken(in));
        return new JsonNumber(result.toString());
    }

    private static String nonNegativeNumberToken(final PushbackReader in) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if ('0' == firstChar) {
            result.append('0');
        } else {
            in.unread(firstChar);
            result.append(nonZeroDigitToken(in));
            result.append(digitString(in));
            result.append(possibleFractionalComponent(in));
            result.append(possibleExponent(in));
        }
        return result.toString();
    }

    private static char nonZeroDigitToken(final PushbackReader in) throws IOException {
        final char result;
        final char nextChar = (char) in.read();
        switch (nextChar) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = nextChar;
                break;
            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + nextChar + "].");
        }
        return result;
    }

    private static char digitToken(final PushbackReader in) throws IOException {
        final char result;
        final char nextChar = (char) in.read();
        switch (nextChar) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = nextChar;
                break;
            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + nextChar + "].");
        }
        return result;
    }

    private static String digitString(final PushbackReader in) throws IOException {
        final StringBuilder result = new StringBuilder();
        boolean gotANonDigit = false;
        while (!gotANonDigit) {
            final char nextChar = (char) in.read();
            switch (nextChar) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    result.append(nextChar);
                    break;
                default:
                    gotANonDigit = true;
                    in.unread(nextChar);
            }
        }
        return result.toString();
    }

    private static String possibleFractionalComponent(final PushbackReader pushbackReader) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '.') {
            result.append('.');
            result.append(digitToken(pushbackReader));
            result.append(digitString(pushbackReader));
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }

    private static String possibleExponent(final PushbackReader pushbackReader) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '.' || firstChar == 'E') {
            result.append('E');
            result.append(possibleSign(pushbackReader));
            result.append(digitToken(pushbackReader));
            result.append(digitString(pushbackReader));
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }

    private static String possibleSign(final PushbackReader pushbackReader) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '+' || firstChar == '-') {
            result.append(firstChar);
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }


    static JsonString stringToken(final Reader in) throws IOException {
        final StringWriter result = new StringWriter();
        final char firstChar = (char) in.read();
        if (DOUBLE_QUOTE != firstChar) {
            throw new InvalidSyntaxException("Expected [" + DOUBLE_QUOTE + "] but got [" + firstChar + "].");
        }
        boolean stringClosed = false;
        while (!stringClosed) {
            final char nextChar = (char) in.read();
            switch (nextChar) {
                case DOUBLE_QUOTE:
                    stringClosed = true;
                    break;
                case BACK_SLASH:
                    final char escapedChar = escapedStringChar(in);
                    result.write(escapedChar);
                    break;
                default:
                    result.write(nextChar);
            }
        }
        return new JsonString(result.toString());
    }

    private static char escapedStringChar(final Reader in) throws IOException {
        final char result;
        final char firstChar = (char) in.read();
        switch (firstChar) {
            case DOUBLE_QUOTE:
                result = DOUBLE_QUOTE;
                break;
            case BACK_SLASH:
                result = BACK_SLASH;
                break;
            case '/':
                result = '/';
                break;
            case 'b':
                result = BACKSPACE;
                break;
            case 'f':
                result = '\f';
                break;
            case 'n':
                result = '\n';
                break;
            case 'r':
                result = '\r';
                break;
            case 't':
                result = '\t';
                break;
            case 'u':
                result = (char) hexidecimalNumber(in);
                break;
            default:
                throw new InvalidSyntaxException("Unrecognised escape character [" + firstChar + "].");
        }
        return result;
    }

    private static int hexidecimalNumber(final Reader in) throws IOException {
        final char[] resultCharArray = new char[4];
        final int readSize = in.read(resultCharArray);
        if (readSize != 4) {
            throw new InvalidSyntaxException("Expected a 4 digit hexidecimal number but got only [" + readSize + "], namely [" + String.valueOf(resultCharArray, 0, readSize) + "].");
        }
        int result;
        try {
            result = Integer.parseInt(String.valueOf(resultCharArray), 16);
        } catch (final NumberFormatException e) {
            throw new InvalidSyntaxException("Unable to parse [" + String.valueOf(resultCharArray) + "] as a hexidecimal number.", e);
        }
        return result;
    }
}
