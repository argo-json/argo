/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static argo.format.JsonNodeWritingWrapper.FIELD_ORDER_NORMALISING_JSON_NODE_WRITING_WRAPPER;
import static argo.format.JsonNodeWritingWrapper.STRAIGHT_THROUGH_JSON_NODE_WRITING_WRAPPER;

/**
 * JsonFormat that formats JSON as compactly as possible.  Instances of this class can safely be shared between threads.
 */
public final class CompactJsonFormatter implements JsonFormatter {

    private static final JsonWriter COMPACT_JSON_WRITER = new CompactJsonWriter();

    private final JsonNodeWritingWrapper jsonNodeWritingWrapper;

    public CompactJsonFormatter() {
        this(STRAIGHT_THROUGH_JSON_NODE_WRITING_WRAPPER);
    }

    private CompactJsonFormatter(final JsonNodeWritingWrapper jsonNodeWritingWrapper) {
        this.jsonNodeWritingWrapper = jsonNodeWritingWrapper;
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON as compactly as possible, outputting the fields of objects in the order they were defined.
     *
     * @return a {@code JsonFormatter} that formats JSON as compactly as possible, outputting the fields of objects in the order they were defined.
     */
    public static CompactJsonFormatter fieldOrderPreservingCompactJsonFormatter() {
        return new CompactJsonFormatter();
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON as compactly as possible, outputting the fields of objects in alphabetic order.
     *
     * @return a {@code JsonFormatter} that formats JSON as compactly as possible, outputting the fields of objects in alphabetic order.
     */
    public static CompactJsonFormatter fieldOrderNormalisingCompactJsonFormatter() {
        return new CompactJsonFormatter(FIELD_ORDER_NORMALISING_JSON_NODE_WRITING_WRAPPER);
    }

    public String format(final JsonNode jsonNode) {
        final StringWriter stringWriter = new StringWriter();
        try {
            format(jsonNode, stringWriter);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringWriter threw an IOException", e);
        }
        return stringWriter.toString();
    }

    public void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        jsonNodeWritingWrapper.write(writer, jsonNode, COMPACT_JSON_WRITER);
    }

}
