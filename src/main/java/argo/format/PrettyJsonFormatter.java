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

import static argo.format.JsonNodeWritingWrapper.FIELD_ORDER_NORMALISING_JSON_NODE_WRITING_WRAPPER;
import static argo.format.JsonNodeWritingWrapper.STRAIGHT_THROUGH_JSON_NODE_WRITING_WRAPPER;

/**
 * JsonFormat that formats JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
public final class PrettyJsonFormatter extends AbstractJsonFormatter {

    private static final JsonWriter PRETTY_JSON_WRITER = new PrettyJsonWriter();

    private final JsonNodeWritingWrapper jsonNodeWritingWrapper;

    /**
     * Constructs a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     */
    public PrettyJsonFormatter() {
        this(STRAIGHT_THROUGH_JSON_NODE_WRITING_WRAPPER);
    }

    private PrettyJsonFormatter(final JsonNodeWritingWrapper jsonNodeWritingWrapper) {
        this.jsonNodeWritingWrapper = jsonNodeWritingWrapper;
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
     *
     * @return a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in alphabetic order.
     */
    public static PrettyJsonFormatter fieldOrderNormalisingPrettyJsonFormatter() {
        return new PrettyJsonFormatter(FIELD_ORDER_NORMALISING_JSON_NODE_WRITING_WRAPPER);
    }

    public void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        jsonNodeWritingWrapper.write(writer, jsonNode, PRETTY_JSON_WRITER);
    }

}
