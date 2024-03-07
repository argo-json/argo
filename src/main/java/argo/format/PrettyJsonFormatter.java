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
 * JsonFormat that formats JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
@SuppressWarnings("deprecation")
@Deprecated public final class PrettyJsonFormatter extends AbstractJsonFormatter { // TODO document deprecation

    private static final AbstractJsonWriter PRETTY_JSON_WRITER = new PrettyJsonWriter();
    private static final JsonWriter FIELD_SORTING_PRETTY_JSON_WRITER = PRETTY_JSON_WRITER.withFieldSorting(true);

    private final JsonWriter jsonWriter;

    /**
     * Constructs a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     */
    public PrettyJsonFormatter() {
        this(PRETTY_JSON_WRITER);
    }

    private PrettyJsonFormatter(final JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     *
     * @return a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     */
    public static PrettyJsonFormatter fieldOrderPreservingPrettyJsonFormatter() {
        return new PrettyJsonFormatter();
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in alphabetic order.
     * In the event that more than one field with the same name exists in an object, the relative order of those fields is preserved.
     *
     * @return a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in alphabetic order.
     */
    public static PrettyJsonFormatter fieldOrderNormalisingPrettyJsonFormatter() {
        return new PrettyJsonFormatter(FIELD_SORTING_PRETTY_JSON_WRITER);
    }

    public void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        jsonWriter.write(writer, jsonNode);
    }

}
