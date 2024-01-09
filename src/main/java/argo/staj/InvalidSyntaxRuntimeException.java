/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import static argo.staj.CharacterUtilities.asPrintableString;

/**
 * Thrown to indicate a given character stream is not valid JSON.
 */
public final class InvalidSyntaxRuntimeException extends RuntimeException {

    private final Position position;

    InvalidSyntaxRuntimeException(final String explanation, final Position position) {
        this(explanation, null, position);
    }

    InvalidSyntaxRuntimeException(final String explanation, final Throwable cause, final Position position) {
        super("At " + lineLabel(position.line) + ", " + columnLabel(position.column) + ":  " + explanation, cause);
        this.position = position;
    }

    static InvalidSyntaxRuntimeException unexpectedCharacterInvalidSyntaxRuntimeException(final String expectation, final int actualCharacter, final Position position) {
        final String explanation = expectation + (-1 == actualCharacter ? " but reached end of input" : " but got [" + asPrintableString((char) actualCharacter) + "]");
        return new InvalidSyntaxRuntimeException(explanation, position);
    }

    private static String lineLabel(final int line) {
        return line == -1 ? "unknown line" : "line " + line;
    }

    private static String columnLabel(final int column) {
        return column == -1 ? "unknown column" : "column " + column;
    }

    /**
     * The column number at which the invalid syntax occurred, or -1 if the column number is unknown.
     *
     * @return the column number at which the invalid syntax occurred, or -1 if the column number is unknown.
     */
    public int getColumn() {
        return position.column;
    }

    /**
     * The line number at which the invalid syntax occurred, or -1 if the line number is unknown.
     *
     * @return the line number at which the invalid syntax occurred, or -1 if the line number is unknown.
     */
    public int getLine() {
        return position.line;
    }

}
