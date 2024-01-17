/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.internal;

public enum NumberParserState {
    BEFORE_START {
        public NumberParserState handle(final int character) {
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
                    return ERROR_EXPECTED_DIGIT_OR_MINUS;
            }
        }
    }, NEGATIVE {
        public NumberParserState handle(final int character) {
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
                    return ERROR_EXPECTED_DIGIT;
            }
        }
    }, ZERO {
        public NumberParserState handle(final int character) {
            switch (character) {
                case '.':
                    return DECIMAL_POINT;
                case 'e':
                case 'E':
                    return EXPONENT_MARKER;
                default:
                    return END;
            }
        }
    }, INTEGER_PART {
        public NumberParserState handle(final int character) {
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
                    return END;
            }
        }
    }, DECIMAL_POINT {
        public NumberParserState handle(final int character) {
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
                    return ERROR_EXPECTED_DIGIT;
            }
        }
    }, FRACTIONAL_PART {
        public NumberParserState handle(final int character) {
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
                    return END;
            }
        }
    }, EXPONENT_MARKER {
        public NumberParserState handle(final int character) {
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
                    return ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS;
            }
        }
    }, EXPONENT_SIGN {
        public NumberParserState handle(final int character) {
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
                    return ERROR_EXPECTED_DIGIT;
            }
        }
    }, EXPONENT {
        public NumberParserState handle(final int character) {
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
                    return END;
            }
        }
    }, END {
        public NumberParserState handle(final int character) {
            return this;
        }
    },
    ERROR_EXPECTED_DIGIT {
        public NumberParserState handle(final int character) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to continue parsing when in error state");
        }
    },
    ERROR_EXPECTED_DIGIT_OR_MINUS {
        public NumberParserState handle(final int character) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to continue parsing when in error state");
        }
    },
    ERROR_EXPECTED_DIGIT_PLUS_OR_MINUS {
        public NumberParserState handle(final int character) {
            throw new RuntimeException("Coding failure in Argo:  Attempt to continue parsing when in error state");
        }
    };

    public abstract NumberParserState handle(int character);

}
