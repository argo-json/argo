/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

public final class JsonNumberValidator {
    private ParserState parserState = ParserState.BEFORE_START;

    public void appendCharacter(final int nextCharacter) throws ParsingFailedException {
        parserState = parserState.handle(nextCharacter);
    }


    public boolean isEndState() {
        return parserState.isEndState;
    }

    private enum ParserState {
        BEFORE_START(false) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
                    case '-':
                        return NEGATIVE;
                    case '0':
                        return ZERO;
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        return INTEGER_PART;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, NEGATIVE(false) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
                    case '0':
                        return ZERO;
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        return INTEGER_PART;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, ZERO(true) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
                    case '.':
                        return DECIMAL_POINT;
                    case 'e':
                    case 'E':
                        return EXPONENT_MARKER;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, INTEGER_PART(true) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
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
                        return INTEGER_PART;
                    case '.':
                        return DECIMAL_POINT;
                    case 'e':
                    case 'E':
                        return EXPONENT_MARKER;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, DECIMAL_POINT(false) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
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
                        return FRACTIONAL_PART;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, FRACTIONAL_PART(true) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
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
                        return FRACTIONAL_PART;
                    case 'e':
                    case 'E':
                        return EXPONENT_MARKER;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, EXPONENT_MARKER(false) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
                    case '+':
                    case '-':
                        return EXPONENT_SIGN;
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
                        return EXPONENT;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, EXPONENT_SIGN(false) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
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
                        return EXPONENT;
                    default:
                        throw new ParsingFailedException();
                }
            }
        }, EXPONENT(true) {
            ParserState handle(final int character) throws ParsingFailedException {
                switch (character) {
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
                        return EXPONENT;
                    default:
                        throw new ParsingFailedException();
                }
            }
        };

        private final boolean isEndState;

        ParserState(final boolean isEndState) {
            this.isEndState = isEndState;
        }

        abstract ParserState handle(int character) throws ParsingFailedException;
    }

    public static final class ParsingFailedException extends Exception {
        ParsingFailedException() {
        }
    }
}
