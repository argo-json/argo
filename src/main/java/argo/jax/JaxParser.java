package argo.jax;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;

public final class JaxParser {

    private static final char DOUBLE_QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char BACKSPACE = '\b';
    private static final char TAB = '\t';
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char FORM_FEED = '\f';

    public JaxParser() {
    }

    public void parse(final Reader in, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final PushbackReader pushbackReader = new PushbackReader(in);
        final char nextChar = (char) pushbackReader.read();
        switch (nextChar) {
            case '{':
                pushbackReader.unread(nextChar);
                jsonListener.startDocument();
                objectString(pushbackReader, jsonListener);
                break;
            case '[':
                pushbackReader.unread(nextChar);
                jsonListener.startDocument();
                arrayString(pushbackReader, jsonListener);
                break;
            default:
                throw new InvalidSyntaxException("Expected either [ or { but got [" + nextChar + "].");
        }
        final int trailingCharacter = readNextNonWhitespaceChar(pushbackReader);
        if (trailingCharacter != -1) {
            throw new InvalidSyntaxException("Got unexpected trailing character [" + trailingCharacter + "].");
        }
        jsonListener.endDocument();
    }

    private void arrayString(final PushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char firstChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (firstChar != '[') {
            throw new InvalidSyntaxException("Expected object to start with [ but got [" + firstChar + "].");
        }
        jsonListener.startArray();
        final char secondChar = (char) readNextNonWhitespaceChar(pushbackReader);
        pushbackReader.unread(secondChar);
        if (secondChar != ']') {
            aJsonValue(pushbackReader, jsonListener);
        }
        boolean gotEndOfArray = false;
        while (!gotEndOfArray) {
            final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
            switch (nextChar) {
                case ',':
                    aJsonValue(pushbackReader, jsonListener);
                    break;
                case ']':
                    gotEndOfArray = true;
                    break;
                default:
                    throw new InvalidSyntaxException("Expected either , or ] but got [" + nextChar + "].");
            }
        }
        jsonListener.endArray();
    }

    private void objectString(final PushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char firstChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (firstChar != '{') {
            throw new InvalidSyntaxException("Expected object to start with { but got [" + firstChar + "].");
        }
        jsonListener.startObject();
        final char secondChar = (char) readNextNonWhitespaceChar(pushbackReader);
        pushbackReader.unread(secondChar);
        if (secondChar != '}') {
            aFieldToken(pushbackReader, jsonListener);
        }
        boolean gotEndOfObject = false;
        while (!gotEndOfObject) {
            final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
            switch (nextChar) {
                case ',':
                    aFieldToken(pushbackReader, jsonListener);
                    break;
                case '}':
                    gotEndOfObject = true;
                    break;
                default:
                    throw new InvalidSyntaxException("Expected either , or } but got [" + nextChar + "].");
            }
        }
        jsonListener.endObject();
    }

    private void aFieldToken(final PushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (DOUBLE_QUOTE != nextChar) {
            throw new InvalidSyntaxException("Expected object identifier to begin with [\"] but got [" + nextChar + "].");
        }
        pushbackReader.unread(nextChar);
        jsonListener.startField(stringToken(pushbackReader));
        final char separatorChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (separatorChar != ':') {
            throw new InvalidSyntaxException("Expected object identifier to be followed by : but got [" + separatorChar + "].");
        }
        aJsonValue(pushbackReader, jsonListener);
        jsonListener.endField();
    }

    private void aJsonValue(final PushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
        switch (nextChar) {
            case '"':
                pushbackReader.unread(nextChar);
                jsonListener.stringValue(stringToken(pushbackReader));
                break;
            case 't':
                final char[] remainingTrueTokenCharacters = new char[3];
                final int trueTokenCharactersRead = pushbackReader.read(remainingTrueTokenCharacters);
                if (trueTokenCharactersRead != 3 || remainingTrueTokenCharacters[0] != 'r' || remainingTrueTokenCharacters[1] != 'u' || remainingTrueTokenCharacters[2] != 'e') {
                    throw new InvalidSyntaxException("Expected 't' to be followed by [[r, u, e]], but got [" + Arrays.toString(remainingTrueTokenCharacters) + "].");
                } else {
                    jsonListener.trueValue();
                }
                break;
            case 'f':
                final char[] remainingFalseTokenCharacters = new char[4];
                final int falseTokenCharactersRead = pushbackReader.read(remainingFalseTokenCharacters);
                if (falseTokenCharactersRead != 4 || remainingFalseTokenCharacters[0] != 'a' || remainingFalseTokenCharacters[1] != 'l' || remainingFalseTokenCharacters[2] != 's' || remainingFalseTokenCharacters[3] != 'e') {
                    throw new InvalidSyntaxException("Expected 'f' to be followed by [[a, l, s, e]], but got [" + Arrays.toString(remainingFalseTokenCharacters) + "].");
                } else {
                    jsonListener.falseValue();
                }
                break;
            case 'n':
                final char[] remainingNullTokenCharacters = new char[3];
                final int nullTokenCharactersRead = pushbackReader.read(remainingNullTokenCharacters);
                if (nullTokenCharactersRead != 3 || remainingNullTokenCharacters[0] != 'u' || remainingNullTokenCharacters[1] != 'l' || remainingNullTokenCharacters[2] != 'l') {
                    throw new InvalidSyntaxException("Expected 'n' to be followed by [[u, l, l]], but got [" + Arrays.toString(remainingNullTokenCharacters) + "].");
                } else {
                    jsonListener.nullValue();
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
                jsonListener.numberValue(numberToken(pushbackReader));
                break;
            case '{':
                pushbackReader.unread(nextChar);
                objectString(pushbackReader, jsonListener);
                break;
            case '[':
                pushbackReader.unread(nextChar);
                arrayString(pushbackReader, jsonListener);
                break;
            default:
                throw new InvalidSyntaxException("Invalid character at start of value [" + nextChar + "].");
        }
    }

    private String numberToken(final PushbackReader in) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if ('-' == firstChar) {
            result.append('-');
        } else {
            in.unread(firstChar);
        }
        result.append(nonNegativeNumberToken(in));
        return result.toString();
    }

    private String nonNegativeNumberToken(final PushbackReader in) throws IOException, InvalidSyntaxException {
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

    private char nonZeroDigitToken(final PushbackReader in) throws IOException, InvalidSyntaxException {
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

    private char digitToken(final PushbackReader in) throws IOException, InvalidSyntaxException {
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

    private String digitString(final PushbackReader in) throws IOException {
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

    private String possibleFractionalComponent(final PushbackReader pushbackReader) throws IOException, InvalidSyntaxException {
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

    private String possibleExponent(final PushbackReader pushbackReader) throws IOException, InvalidSyntaxException {
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

    private String possibleSign(final PushbackReader pushbackReader) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '+' || firstChar == '-') {
            result.append(firstChar);
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }


    private String stringToken(final Reader in) throws IOException, InvalidSyntaxException {
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
        return result.toString();
    }

    private char escapedStringChar(final Reader in) throws IOException, InvalidSyntaxException {
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
                result = FORM_FEED;
                break;
            case 'n':
                result = NEWLINE;
                break;
            case 'r':
                result = CARRIAGE_RETURN;
                break;
            case 't':
                result = TAB;
                break;
            case 'u':
                result = (char) hexidecimalNumber(in);
                break;
            default:
                throw new InvalidSyntaxException("Unrecognised escape character [" + firstChar + "].");
        }
        return result;
    }

    private int hexidecimalNumber(final Reader in) throws IOException, InvalidSyntaxException {
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

    private int readNextNonWhitespaceChar(final Reader in) throws IOException {
        int nextChar;
        boolean gotNonWhitespace = false;
        do {
            nextChar = in.read();
            switch (nextChar) {
                case ' ':
                case TAB:
                case NEWLINE:
                case CARRIAGE_RETURN:
                    break;
                default:
                    gotNonWhitespace = true;
            }
        } while (!gotNonWhitespace);
        return nextChar;
    }

}
