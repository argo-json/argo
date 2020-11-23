/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.util.NoSuchElementException;
import java.util.Stack;

import static argo.staj.InvalidSyntaxRuntimeException.END_OF_STREAM;
import static argo.staj.InvalidSyntaxRuntimeException.invalidSyntaxRuntimeException;
import static argo.staj.InvalidSyntaxRuntimeException.unexpectedCharacterInvalidSyntaxRuntimeException;
import static argo.staj.JsonStreamElement.*;

/**
 * Types of element a {@code StajParser} can produce.
 */
public enum JsonStreamElementType { // NOPMD TODO this should be turned off in the rules
    START_ARRAY {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            final int secondChar = readNextNonWhitespaceChar(pushbackReader);
            if (']' != secondChar) {
                pushbackReader.unreadLastCharacter();
                return aJsonValue(pushbackReader, stack);
            }
            stack.pop();
            return endArray();
        }
    },
    END_ARRAY {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_OBJECT {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFieldOrObjectEnd(pushbackReader, stack);
        }
    },
    END_OBJECT {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_FIELD {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            final int separatorChar = readNextNonWhitespaceChar(pushbackReader);
            if (separatorChar != ':') {
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected object identifier to be followed by :", separatorChar, pushbackReader);
            }
            return aJsonValue(pushbackReader, stack);
        }
    },
    END_FIELD {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFieldOrObjectEnd(pushbackReader, stack);
        }
    },
    STRING {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    TRUE {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    FALSE {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    NULL {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    NUMBER {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_DOCUMENT {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            return aJsonValue(pushbackReader, stack);
        }
    },
    END_DOCUMENT {
        @Override
        JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
            throw new NoSuchElementException("Document complete");
        }
    };

    private static final char DOUBLE_QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char BACKSPACE = '\b';
    private static final char TAB = '\t';
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char FORM_FEED = '\f';

    abstract JsonStreamElement parseNext(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack);

    static JsonStreamElement startDocument(final Stack<JsonStreamElementType> stack) {
        stack.push(START_DOCUMENT);
        return JsonStreamElement.startDocument();
    }

    private static JsonStreamElement parseFieldOrObjectEnd(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        if ('}' != nextChar) {
            pushbackReader.unreadLastCharacter();
            return aFieldToken(pushbackReader, stack);
        }
        stack.pop();
        return endObject();
    }

    private static JsonStreamElement parseFromEndOfNode(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) { // NOPMD TODO this should be turned off in the rules
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        final JsonStreamElementType peek = stack.peek();
        if (peek.equals(START_OBJECT)) {
            switch (nextChar) {
                case ',':
                    return aJsonValue(pushbackReader, stack);
                case '}':
                    stack.pop();
                    return endObject();
                default:
                    throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected either , or }", nextChar, pushbackReader);
            }
        } else if (peek.equals(START_ARRAY)) {
            switch (nextChar) {
                case ',':
                    return aJsonValue(pushbackReader, stack);
                case ']':
                    stack.pop();
                    return endArray();
                default:
                    throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected either , or ]", nextChar, pushbackReader);
            }
        } else if (peek.equals(START_FIELD)) {
            switch (nextChar) {
                case ',':
                    stack.pop();
                    return endField();
                case '}':
                    stack.pop();
                    pushbackReader.unreadLastCharacter();
                    return endField();
                default:
                    throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected either , or ]", nextChar, pushbackReader);
            }
        } else if (peek.equals(START_DOCUMENT)) {
            if (nextChar == -1) {
                return endDocument();
            } else {
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected only whitespace", nextChar, pushbackReader);
            }
        } else {
            throw new RuntimeException("Coding failure in Argo: Stack contained unexpected element type " + peek);
        }
    }

    private static int readNextNonWhitespaceChar(final PositionTrackingPushbackReader in) {
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

    private static JsonStreamElement aJsonValue(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) { // NOPMD TODO this should be turned off in the rules
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        switch (nextChar) {
            case '"':
                pushbackReader.unreadLastCharacter();
                return string(stringToken(pushbackReader));
            case 't':
                final char[] remainingTrueTokenCharacters = new char[3];
                final int trueTokenCharactersRead = pushbackReader.read(remainingTrueTokenCharacters);
                if (trueTokenCharactersRead == 3 && remainingTrueTokenCharacters[0] == 'r' && remainingTrueTokenCharacters[1] == 'u' && remainingTrueTokenCharacters[2] == 'e') {
                    return trueValue();
                } else {
                    throw readBufferInvalidSyntaxRuntimeException("Expected 't' to be followed by [[r, u, e]]", trueTokenCharactersRead, remainingTrueTokenCharacters, pushbackReader);
                }
            case 'f':
                final char[] remainingFalseTokenCharacters = new char[4];
                final int falseTokenCharactersRead = pushbackReader.read(remainingFalseTokenCharacters);
                if (falseTokenCharactersRead == 4 && remainingFalseTokenCharacters[0] == 'a' && remainingFalseTokenCharacters[1] == 'l' && remainingFalseTokenCharacters[2] == 's' && remainingFalseTokenCharacters[3] == 'e') {
                    return falseValue();
                } else {
                    throw readBufferInvalidSyntaxRuntimeException("Expected 'f' to be followed by [[a, l, s, e]]", falseTokenCharactersRead, remainingFalseTokenCharacters, pushbackReader);
                }
            case 'n':
                final char[] remainingNullTokenCharacters = new char[3];
                final int nullTokenCharactersRead = pushbackReader.read(remainingNullTokenCharacters);
                if (nullTokenCharactersRead == 3 && remainingNullTokenCharacters[0] == 'u' && remainingNullTokenCharacters[1] == 'l' && remainingNullTokenCharacters[2] == 'l') {
                    return nullValue();
                } else {
                    throw readBufferInvalidSyntaxRuntimeException("Expected 'n' to be followed by [[u, l, l]]", nullTokenCharactersRead, remainingNullTokenCharacters, pushbackReader);
                }
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
                pushbackReader.unreadLastCharacter();
                return number(numberToken(pushbackReader));
            case '{':
                stack.push(START_OBJECT);
                return startObject();
            case '[':
                stack.push(START_ARRAY);
                return startArray();
            default:
                throw invalidSyntaxRuntimeException(END_OF_STREAM == nextChar ? "Expected a value but reached end of input." : "Invalid character at start of value [" + nextChar + "].", pushbackReader);
        }
    }

    private static String stringify(final char[] chars, final int maxLength) {
        final StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < chars.length && i < maxLength; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(chars[i]);
        }
        result.append("]");
        return result.toString();
    }

    private static JsonStreamElement aFieldToken(final PositionTrackingPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        if (DOUBLE_QUOTE != nextChar) {
            throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected object identifier to begin with [\"]", nextChar, pushbackReader);
        }
        pushbackReader.unreadLastCharacter();
        stack.push(START_FIELD);
        return startField(stringToken(pushbackReader));
    }

    private static String stringToken(final PositionTrackingPushbackReader in) {
        final int firstChar = in.read();
        if (DOUBLE_QUOTE != firstChar) {
            throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected [" + DOUBLE_QUOTE + "]", firstChar, in);
        }
        final ThingWithPosition openDoubleQuotesPosition = in.snapshotOfPosition();
        final StringBuilder result = new StringBuilder();
        boolean stringClosed = false;
        while (!stringClosed) {
            final int nextChar = in.read();
            if (-1 == nextChar) {
                throw invalidSyntaxRuntimeException("Got opening [" + DOUBLE_QUOTE + "] without matching closing [" + DOUBLE_QUOTE + "]", openDoubleQuotesPosition);
            }
            switch (nextChar) {
                case DOUBLE_QUOTE:
                    stringClosed = true;
                    break;
                case BACK_SLASH:
                    final char escapedChar = escapedStringChar(in);
                    result.append(escapedChar);
                    break;
                default:
                    result.append((char) nextChar);
            }
        }
        return result.length() == 0 ? "" : result.toString();
    }

    private static char escapedStringChar(final PositionTrackingPushbackReader in) { // NOPMD TODO this should be turned off in the rules
        final char result;
        final int firstChar = in.read();
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
                result = (char) hexadecimalNumber(in);
                break;
            default:
                throw invalidSyntaxRuntimeException(END_OF_STREAM == firstChar ? "Unexpectedly reached end of input during escaped character." : "Unrecognised escape character [" + firstChar + "].", in);
        }
        return result;
    }

    private static int hexadecimalNumber(final PositionTrackingPushbackReader in) {
        final char[] resultCharArray = new char[4];
        final int readSize = in.read(resultCharArray);
        if (readSize != 4) {
            throw readBufferInvalidSyntaxRuntimeException("Expected 4 hexadecimal digits", readSize, resultCharArray, in);
        }
        int result;
        try {
            result = Integer.parseInt(String.valueOf(resultCharArray), 16);
        } catch (final NumberFormatException e) {
            in.uncount(resultCharArray);
            throw invalidSyntaxRuntimeException("Unable to parse [" + String.valueOf(resultCharArray) + "] as a hexadecimal number.", e, in);
        }
        return result;
    }

    private static String numberToken(final PositionTrackingPushbackReader in) {
        final StringBuilder result = new StringBuilder();
        final int firstChar = in.read();
        if ('-' == firstChar) {
            result.append('-');
        } else {
            in.unreadLastCharacter();
        }
        result.append(nonNegativeNumberToken(in));
        final String numberToken = result.toString();
        if ("0".equals(numberToken)) {
            return "0";
        } else if ("1".equals(numberToken)) {
            return "1";
        } else {
            return numberToken;
        }
    }

    private static StringBuilder nonNegativeNumberToken(final PositionTrackingPushbackReader in) {
        final StringBuilder result = new StringBuilder();
        final int firstChar = in.read();
        if ('0' == firstChar) {
            result.append('0');
            result.append(possibleFractionalComponent(in));
            result.append(possibleExponent(in));
        } else {
            in.unreadLastCharacter();
            result.append(nonZeroDigitToken(in));
            result.append(digitString(in));
            result.append(possibleFractionalComponent(in));
            result.append(possibleExponent(in));
        }
        return result;
    }

    private static char nonZeroDigitToken(final PositionTrackingPushbackReader in) { // NOPMD TODO this should be turned off in the rules
        final char result;
        final int nextChar = in.read();
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
                result = (char) nextChar;
                break;
            default:
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected a digit 1 - 9", nextChar, in);
        }
        return result;
    }

    private static char digitToken(final PositionTrackingPushbackReader in) { // NOPMD TODO this should be turned off in the rules
        final char result;
        final int nextChar = in.read();
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
                result = (char) nextChar;
                break;
            default:
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected a digit 0 - 9", nextChar, in);
        }
        return result;
    }

    private static StringBuilder digitString(final PositionTrackingPushbackReader in) { // NOPMD TODO this should be turned off in the rules
        final StringBuilder result = new StringBuilder();
        boolean gotANonDigit = false;
        while (!gotANonDigit) {
            final int nextChar = in.read();
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
                    result.append((char) nextChar);
                    break;
                default:
                    gotANonDigit = true;
                    in.unreadLastCharacter();
            }
        }
        return result;
    }

    private static StringBuilder possibleFractionalComponent(final PositionTrackingPushbackReader pushbackReader) {
        final StringBuilder result = new StringBuilder();
        final int firstChar = pushbackReader.read();
        if ('.' == firstChar) {
            result.append('.');
            result.append(digitToken(pushbackReader));
            result.append(digitString(pushbackReader));
        } else {
            pushbackReader.unreadLastCharacter();
        }
        return result;
    }

    private static StringBuilder possibleExponent(final PositionTrackingPushbackReader pushbackReader) {
        final StringBuilder result = new StringBuilder();
        final int firstChar = pushbackReader.read();
        switch (firstChar) {
            case 'E':
                result.append('E');
                result.append(possibleSign(pushbackReader));
                result.append(digitToken(pushbackReader));
                result.append(digitString(pushbackReader));
                break;
            case 'e':
                result.append('e');
                result.append(possibleSign(pushbackReader));
                result.append(digitToken(pushbackReader));
                result.append(digitString(pushbackReader));
                break;
            default:
                pushbackReader.unreadLastCharacter();
                break;
        }
        return result;
    }

    private static StringBuilder possibleSign(final PositionTrackingPushbackReader pushbackReader) {
        final StringBuilder result = new StringBuilder();
        final int firstChar = pushbackReader.read();
        if ('+' == firstChar) {
            result.append('+');
        } else if ('-' == firstChar) {
            result.append('-');
        } else {
            pushbackReader.unreadLastCharacter();
        }
        return result;
    }

    private static InvalidSyntaxRuntimeException readBufferInvalidSyntaxRuntimeException(final String expectation, final int charactersRead, final char[] readBuffer, final ThingWithPosition thingWithPosition) {
        return invalidSyntaxRuntimeException(expectation + ", but " + (charactersRead == -1 ? "reached end of input." : "got [" + stringify(readBuffer, charactersRead) + "]."), thingWithPosition);
    }

}
