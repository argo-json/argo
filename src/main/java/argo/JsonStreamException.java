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

import java.io.IOException;

/**
 * Thrown to indicate that it was not possible to read any further along the JSON stream.
 */
public final class JsonStreamException extends RuntimeException {

    private final IOException typedCause;

    JsonStreamException(final String message, final IOException cause) {
        super(message, cause);
        if (cause == null) {
            throw new NullPointerException("cause is null");
        }
        this.typedCause = cause;
    }

    @Override
    public IOException getCause() {
        return typedCause;
    }
}
