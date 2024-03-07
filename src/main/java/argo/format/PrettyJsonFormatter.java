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

import argo.JsonGenerator;
import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Writer;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;

/**
 * JsonFormat that formats JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
@SuppressWarnings("deprecation")
@Deprecated public final class PrettyJsonFormatter extends AbstractJsonFormatter {

    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(PRETTY);

    private final boolean fieldOrderNormalising;

    /**
     * Constructs a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     *
     * @deprecated Replaced by {@link JsonGenerator#generate(JsonNode)} and {@link JsonGenerator#generate(Writer, JsonNode)}.
     *   For example, use
     *     {@code new JsonGenerator().style(PRETTY).generate(Writer, JsonNode) }
     *   or
     *     {@code new JsonGenerator().style(PRETTY).generate(JsonNode) }
     */
    public PrettyJsonFormatter() {
        this(false);
    }

    private PrettyJsonFormatter(final boolean fieldOrderNormalising) {
        this.fieldOrderNormalising = fieldOrderNormalising;
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     *
     * @return a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in the order they were defined.
     *
     * @deprecated Replaced by {@link JsonGenerator#generate(JsonNode)} and {@link JsonGenerator#generate(Writer, JsonNode)}.
     *   For example, use
     *     {@code new JsonGenerator().style(PRETTY).generate(Writer, JsonNode) }
     *   or
     *     {@code new JsonGenerator().style(PRETTY).generate(JsonNode) }
     */
    public static PrettyJsonFormatter fieldOrderPreservingPrettyJsonFormatter() {
        return new PrettyJsonFormatter();
    }

    /**
     * Gets a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in alphabetic order.
     * In the event that more than one field with the same name exists in an object, the relative order of those fields is preserved.
     *
     * @return a {@code JsonFormatter} that formats JSON in a human-readable form, outputting the fields of objects in alphabetic order.
     *
     * @deprecated Replaced by {@link JsonGenerator#generateWithFieldSorting(JsonNode)} and {@link JsonGenerator#generateWithFieldSorting(Writer, JsonNode)}.
     *   For example, use
     *     {@code new JsonGenerator().style(PRETTY).generateWithFieldSorting(Writer, JsonNode) }
     *   or
     *     {@code new JsonGenerator().style(PRETTY).generateWithFieldSorting(JsonNode) }
     */
    public static PrettyJsonFormatter fieldOrderNormalisingPrettyJsonFormatter() {
        return new PrettyJsonFormatter(true);
    }

    public void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        if (fieldOrderNormalising) {
            JSON_GENERATOR.generateWithFieldSorting(writer, jsonNode);
        } else {
            JSON_GENERATOR.generate(writer, jsonNode);
        }
    }

}
