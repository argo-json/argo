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

import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Writer;

/**
 * JsonFormat that formats JSON as compactly as possible.  Instances of this class can safely be shared between threads.
 */
public final class CompactJsonFormatter extends AbstractJsonFormatter {

    private static final AbstractJsonWriter COMPACT_JSON_WRITER = new CompactJsonWriter();
    private static final JsonWriter FIELD_SORTING_COMPACT_JSON_WRITER = COMPACT_JSON_WRITER.withFieldSorting(true);

    private final JsonWriter jsonWriter;

    public CompactJsonFormatter() {
        this(COMPACT_JSON_WRITER);
    }

    private CompactJsonFormatter(final JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
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
        return new CompactJsonFormatter(FIELD_SORTING_COMPACT_JSON_WRITER);
    }

    public void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        jsonWriter.write(writer, jsonNode);
    }

}
