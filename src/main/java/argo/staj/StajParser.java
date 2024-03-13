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

import argo.InvalidSyntaxRuntimeException;
import argo.JsonParser;
import argo.JsonStreamElement;
import argo.JsonStreamException;

import java.io.Reader;
import java.util.Iterator;

/**
 * Parses a JSON character stream into an {@code Iterator} of {@code JsonStreamElement}s.
 *
 * @deprecated Replaced by {@link argo.JsonParser}.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated public final class StajParser implements Iterator<JsonStreamElement> {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private final Iterator<JsonStreamElement> delegate;

    /**
     * Constructs a StajParser reading from the specified {@code Reader}.
     *
     * @param in the {@code Reader} to convert into {@code JsonStreamElement}s.
     *
     * @deprecated Replaced by {@link argo.JsonParser#parseStreaming(Reader)}.
     */
    public StajParser(final Reader in) {
        delegate = JSON_PARSER.parseStreaming(in);
    }

    /**
     * Constructs a StajParser reading from the given {@code String}.
     *
     * @param json the {@code String} to convert into {@code JsonStreamElement}s.
     *
     * @deprecated Replaced by {@link argo.JsonParser#parseStreaming(Reader)}.
     */
    public StajParser(final String json) {
        delegate = JSON_PARSER.parseStreaming(json);
    }

    /**
     * Determines whether there are any more elements.
     *
     * @return true if there are more elements.
     * @throws InvalidSyntaxRuntimeException if the next element could not be read, for example if the next element turns out not to be valid JSON
     * @throws JsonStreamException           if the underlying character stream failed.
     */
    public boolean hasNext() {
        return delegate.hasNext();
    }

    /**
     * Gets the next element in the stream.
     *
     * @return the next element in the stream.
     * @throws InvalidSyntaxRuntimeException    if the next element could not be read, for example if the next element turns out not to be valid JSON
     * @throws JsonStreamException              if the underlying character stream failed.
     * @throws java.util.NoSuchElementException if there are no more elements to read.
     */
    public JsonStreamElement next() {
        return delegate.next();
    }

    /**
     * Not supported.
     */
    public void remove() {
        delegate.remove();
    }
}
