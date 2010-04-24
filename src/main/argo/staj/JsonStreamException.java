/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

/**
 * Thrown to indicate that it was not possible to read any further along the JSON stream.
 */
public final class JsonStreamException extends Exception {
    public JsonStreamException() {
        super();
    }

    public JsonStreamException(final String message) {
        super(message);
    }

    public JsonStreamException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonStreamException(final Throwable cause) {
        super(cause);
    }
}
