/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import argo.saj.InvalidSyntaxException;

/**
 * Thrown to indicate a given character stream is not valid JSON.
 */
public abstract class InvalidSyntaxRuntimeException extends RuntimeException {

    static final int END_OF_STREAM = -1; // TODO inline

    private final int column;
    private final int line;

    private InvalidSyntaxRuntimeException(final String s, final Position position) {
        super("At line " + position.line + ", column " + position.column + ":  " + s);
        this.column = position.column;
        this.line = position.line;
    }

    private InvalidSyntaxRuntimeException(final String s, final Throwable throwable, final Position position) {
        super("At line " + position.line + ", column " + position.column + ":  " + s, throwable);
        this.column = position.column;
        this.line = position.line;
    }

    static InvalidSyntaxRuntimeException invalidSyntaxRuntimeException(final String s, final Position position) {
        return new InvalidSyntaxRuntimeException(s, position) {
            @Override
            public InvalidSyntaxException asInvalidSyntaxException() {
                return new InvalidSyntaxException(s, position.line, position.column);
            }
        };
    }

    static InvalidSyntaxRuntimeException invalidSyntaxRuntimeException(final String s, final Throwable throwable, final Position position) {
        return new InvalidSyntaxRuntimeException(s, throwable, position) {
            @Override
            public InvalidSyntaxException asInvalidSyntaxException() {
                return new InvalidSyntaxException(s, throwable, position.line, position.column);
            }
        };
    }

    static InvalidSyntaxRuntimeException unexpectedCharacterInvalidSyntaxRuntimeException(final String expectation, final int actual, final Position position) {
        return new InvalidSyntaxRuntimeException(expectation, position) {
            @Override
            public InvalidSyntaxException asInvalidSyntaxException() {
                final String message = expectation + (END_OF_STREAM == actual ? " but reached end of input." : " but got [" + (char) actual + "].");
                return new InvalidSyntaxException(message, position.line, position.column);
            }
        };
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public abstract InvalidSyntaxException asInvalidSyntaxException();
}
