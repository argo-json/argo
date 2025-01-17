/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.internal.NumberParserState;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Stack;

import static argo.CharacterUtilities.asPrintableString;
import static argo.InvalidSyntaxRuntimeException.unexpectedCharacterInvalidSyntaxRuntimeException;
import static argo.JsonStreamElement.*;

/**
 * Types of element a {@code StajParser} can produce.
 */
public enum JsonStreamElementType {
    START_ARRAY {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            final int secondChar = readNextNonWhitespaceChar(pushbackReader);
            if (']' != secondChar) {
                if (secondChar != -1) {
                    pushbackReader.unread(secondChar);
                }
                return aJsonValue(pushbackReader, stack);
            }
            stack.pop();
            return NonTextJsonStreamElement.END_ARRAY;
        }
    },
    END_ARRAY {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_OBJECT {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFieldOrObjectEnd(pushbackReader, stack);
        }
    },
    END_OBJECT {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_FIELD {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            final int nextChar = readNextNonWhitespaceChar(pushbackReader);
            if (nextChar != ':') {
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected object identifier to be followed by :", nextChar, pushbackReader.position());
            }
            return aJsonValue(pushbackReader, stack);
        }
    },
    END_FIELD {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFieldOrObjectEnd(pushbackReader, stack);
        }
    },
    STRING {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    TRUE {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    FALSE {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    NULL {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    NUMBER {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return parseFromEndOfNode(pushbackReader, stack);
        }
    },
    START_DOCUMENT {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
            return aJsonValue(pushbackReader, stack);
        }
    },
    END_DOCUMENT {
        @Override
        JsonStreamElement parseNext(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) {
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

    private static JsonStreamElement parseFieldOrObjectEnd(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        if ('}' != nextChar) {
            if (nextChar != -1) {
                pushbackReader.unread(nextChar);
            }
            return aFieldToken(pushbackReader, stack);
        }
        stack.pop();
        return NonTextJsonStreamElement.END_OBJECT;
    }

    private static JsonStreamElement parseFromEndOfNode(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        final JsonStreamElementType peek = stack.peek();
        switch (peek) {
            case START_ARRAY:
                switch (nextChar) {
                    case ',':
                        return aJsonValue(pushbackReader, stack);
                    case ']':
                        stack.pop();
                        return NonTextJsonStreamElement.END_ARRAY;
                    default:
                        throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected either , or ]", nextChar, pushbackReader.position());
                }
            case START_FIELD:
                switch (nextChar) {
                    case ',':
                        stack.pop();
                        return NonTextJsonStreamElement.END_FIELD;
                    case '}':
                        stack.pop();
                        pushbackReader.unread(nextChar);
                        return NonTextJsonStreamElement.END_FIELD;
                    default:
                        throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected either , or ]", nextChar, pushbackReader.position());
                }
            case START_DOCUMENT:
                if (nextChar == -1) {
                    return NonTextJsonStreamElement.END_DOCUMENT;
                } else {
                    throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected end of stream or whitespace", nextChar, pushbackReader.position());
                }
            default:
                throw new RuntimeException("Coding failure in Argo:  Stack contained unexpected element type " + peek);
        }
    }

    private static int readNextNonWhitespaceChar(final PositionedPushbackReader in) throws IOException {
        while (true) {
            final int nextChar = in.read();
            if (nextChar != ' ' && nextChar != TAB && nextChar != NEWLINE && nextChar != CARRIAGE_RETURN) {
                return nextChar;
            }
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static JsonStreamElement aJsonValue(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        switch (nextChar) {
            case '"':
                return string(new StringReader(pushbackReader, pushbackReader.column(), pushbackReader.line()));
            case 't':
                return constant(pushbackReader, "true", NonTextJsonStreamElement.TRUE);
            case 'f':
                return constant(pushbackReader, "false", NonTextJsonStreamElement.FALSE);
            case 'n':
                return constant(pushbackReader, "null", NonTextJsonStreamElement.NULL);
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
                return number(new NumberReader(pushbackReader));
            case '{':
                stack.push(START_OBJECT);
                return NonTextJsonStreamElement.START_OBJECT;
            case '[':
                stack.push(START_ARRAY);
                return NonTextJsonStreamElement.START_ARRAY;
            default:
                final String explanation = -1 == nextChar ? "Expected a value but reached end of input" : "Invalid character [" + asPrintableString((char) nextChar) + "] at start of value";
                throw new InvalidSyntaxRuntimeException(explanation, pushbackReader.position());
        }
    }

    private static JsonStreamElement constant(final PositionedPushbackReader pushbackReader, final String expectedCharacters, final JsonStreamElement result) throws IOException {
        final char[] actual = new char[expectedCharacters.length() - 1];
        for (int i = 0; i < actual.length; i++) {
            final int character = pushbackReader.read();
            if (character == expectedCharacters.charAt(i + 1)) {
                actual[i] = (char) character;
            } else {
                if (character != -1) {
                    actual[i] = (char) character;
                }
                final char[] expected = new char[expectedCharacters.length() - 1];
                System.arraycopy(expectedCharacters.toCharArray(), 1, expected, 0, expectedCharacters.length() - 1);
                final String explanation = "Expected '" + expectedCharacters.charAt(0) + "' to be followed by " + Arrays.toString(expected) + ", but "
                        + (character == -1 && i == 0 ? "reached end of input" : "got " + asPrintableString(actual, i + (character == -1 ? 0 : 1)));
                throw new InvalidSyntaxRuntimeException(explanation, pushbackReader.position());
            }
        }
        return result;
    }

    private static JsonStreamElement aFieldToken(final PositionedPushbackReader pushbackReader, final Stack<JsonStreamElementType> stack) throws IOException {
        final int nextChar = readNextNonWhitespaceChar(pushbackReader);
        if (DOUBLE_QUOTE != nextChar) {
            throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected object identifier to begin with [\"]", nextChar, pushbackReader.position());
        }
        stack.push(START_FIELD);
        return startField(new StringReader(pushbackReader, pushbackReader.column(), pushbackReader.line()));
    }

    private static char escapedStringChar(final PositionedPushbackReader in) throws IOException {
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
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u", firstChar, in.position());
        }
        return result;
    }

    private static int hexadecimalNumber(final PositionedPushbackReader in) throws IOException {
        final Position startPosition = in.position();
        final char[] resultCharArray = new char[4];
        for (int i = 0; i < resultCharArray.length; i++) {
            final int character = in.read();
            if (character == -1) {
                throw new InvalidSyntaxRuntimeException("Expected 4 hexadecimal digits" + ", but " + (i == 0 ? "reached end of input" : "got " + asPrintableString(resultCharArray, i)), in.position());
            } else {
                resultCharArray[i] = (char) character;
            }
        }
        try {
            return Integer.parseInt(String.valueOf(resultCharArray), 16);
        } catch (final NumberFormatException e) {
            throw new InvalidSyntaxRuntimeException("Unable to parse escaped character " + asPrintableString(resultCharArray, resultCharArray.length) + " as a hexadecimal number", e, startPosition);
        }
    }

    abstract JsonStreamElement parseNext(PositionedPushbackReader pushbackReader, Stack<JsonStreamElementType> stack) throws IOException;

    private static abstract class SingleCharacterReader extends Reader {

        private static void validateArguments(final char[] cbuf, final int offset, final int length) {
            if (offset < 0 || offset > cbuf.length || length < 0 ||
                    offset + length > cbuf.length || offset + length < 0) {
                throw new IndexOutOfBoundsException();
            }
        }

        public final int read(@SuppressWarnings("NullableProblems") final char[] cbuf, final int offset, final int length) throws IOException {
            validateArguments(cbuf, offset, length);
            int n = 0;
            int nextChar;
            while (n < length && (nextChar = read()) != -1) {
                cbuf[offset + n++] = (char) nextChar;
            }
            return n == 0 && length != 0 ? -1 : n;
        }

    }

    private static final class NumberReader extends SingleCharacterReader {

        private PositionedPushbackReader in;
        private NumberParserState parserState = NumberParserState.BEFORE_START;

        NumberReader(final PositionedPushbackReader in) {
            this.in = in;
        }

        private void ensureOpen() throws IOException {
            if (in == null) {
                throw new IOException("Stream closed");
            }
        }

        @Override
        public int read() throws IOException {
            ensureOpen();
            final int nextChar = in.read();
            parserState = parserState.handle(nextChar);
            if (parserState == NumberParserState.ERROR_EXPECTED_DIGIT) {
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected a digit 0 - 9", nextChar, in.position());
            } else if (parserState == NumberParserState.ERROR_EXPECTED_DIGIT_OR_MINUS) {
                throw new RuntimeException("Coding failure in Argo:  Began parsing number despite invalid first character " + asPrintableString((char) nextChar));
            } else if (parserState == NumberParserState.ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS) {
                throw unexpectedCharacterInvalidSyntaxRuntimeException("Expected '+' or '-' or a digit 0 - 9", nextChar, in.position());
            } else if (parserState == NumberParserState.END) {
                if (nextChar != -1) {
                    in.unread(nextChar);
                }
                return -1;
            } else {
                return nextChar;
            }
        }

        @Override
        public boolean ready() throws IOException {
            ensureOpen();
            return false;
        }

        @Override
        public long skip(final long n) throws IOException {
            if (n < 0) {
                throw new IllegalArgumentException("Skip value is negative: " + n);
            }
            ensureOpen();
            for (long i = 0; i < n; i++) {
                if (read() == -1) {
                    return i;
                }
            }
            return n;
        }

        @SuppressWarnings({"PMD.EmptyControlStatement", "StatementWithEmptyBody"})
        public void close() throws IOException {
            if (in != null) {
                while (read() != -1) {
                    // do nothing
                }
                in = null;
            }
        }

    }

    private static final class StringReader extends SingleCharacterReader {

        private final int openDoubleQuotesColumn;
        private final int openDoubleQuotesLine;
        private PositionedPushbackReader in;
        private boolean ended = false;

        StringReader(final PositionedPushbackReader in, final int column, final int line) {
            this.in = in;
            this.openDoubleQuotesColumn = column;
            this.openDoubleQuotesLine = line;
        }

        private void ensureOpen() throws IOException {
            if (in == null) {
                throw new IOException("Stream closed");
            }
        }

        @Override
        public int read() throws IOException {
            ensureOpen();
            if (ended) {
                return -1;
            } else {
                final int nextChar = in.read();
                switch (nextChar) {
                    case -1:
                        throw new InvalidSyntaxRuntimeException("Got opening [" + DOUBLE_QUOTE + "] without matching closing [" + DOUBLE_QUOTE + "]", new Position(openDoubleQuotesColumn, openDoubleQuotesLine));
                    case DOUBLE_QUOTE:
                        ended = true;
                        return -1;
                    case BACK_SLASH:
                        return escapedStringChar(in);
                    default:
                        return nextChar;
                }
            }
        }

        @Override
        public boolean ready() throws IOException {
            ensureOpen();
            return false;
        }

        @Override
        public long skip(final long n) throws IOException {
            if (n < 0) {
                throw new IllegalArgumentException("Skip value is negative: " + n);
            }
            ensureOpen();
            for (long i = 0; i < n; i++) {
                if (read() == -1) {
                    return i;
                }
            }
            return n;
        }

        @Override
        @SuppressWarnings({"PMD.EmptyControlStatement", "StatementWithEmptyBody"})
        public void close() throws IOException {
            if (in != null) {
                while (read() != -1) {
                    // do nothing
                }
                in = null;
            }
        }
    }

}
