/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNumberValidator;

import java.io.IOException;
import java.io.Writer;

final class JsonNumberValidatingWriter extends Writer {

    private final JsonNumberValidator jsonNumberValidator = new JsonNumberValidator();
    private final Writer writer;

    JsonNumberValidatingWriter(final Writer writer) {
        this.writer = writer;
    }

    public void write(final char[] cbuf, final int offset, final int length) throws IOException {
        try {
            jsonNumberValidator.appendCharacters(cbuf, offset, length);
        } catch (JsonNumberValidator.ParsingFailedException e) {
            throw new IllegalArgumentException("Attempted to write characters that do not conform to the JSON number specification"); // TODO which characters?
        }
        writer.write(cbuf, offset, length);
    }

    public void flush() {
    }

    public void close() {
    }

    boolean isEndState() {
        return jsonNumberValidator.isEndState();
    }
}
