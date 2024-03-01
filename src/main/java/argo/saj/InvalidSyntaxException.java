/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import argo.staj.InvalidSyntaxRuntimeException;

/**
 * Thrown to indicate a given character stream is not valid JSON.
 */
public final class InvalidSyntaxException extends Exception { // TODO should be in parent package?

    private final int column;
    private final int line;

    private InvalidSyntaxException(final String message, final Throwable cause, final int line, final int column) {
        super(message, cause);
        this.column = column;
        this.line = line;
    }

    static InvalidSyntaxException from(final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException) {
        return new InvalidSyntaxException(
                invalidSyntaxRuntimeException.getMessage(),
                invalidSyntaxRuntimeException.getCause(),
                invalidSyntaxRuntimeException.getLine(),
                invalidSyntaxRuntimeException.getColumn()
        );
    }

    /**
     * The column number at which the invalid syntax occurred, or -1 if the column number is unknown.
     *
     * @return the column number at which the invalid syntax occurred, or -1 if the column number is unknown.
     */
    public int getColumn() {
        return column;
    }

    /**
     * The line number at which the invalid syntax occurred, or -1 if the line number is unknown.
     *
     * @return the line number at which the invalid syntax occurred, or -1 if the line number is unknown.
     */
    public int getLine() {
        return line;
    }
}
