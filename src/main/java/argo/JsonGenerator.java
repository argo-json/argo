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

import argo.format.*;
import argo.internal.StringBuilderWriter;
import argo.jdom.JsonNode;

import java.io.IOException;
import java.io.Writer;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;

/**
 * Provides operations create {@code String}s of valid JSON and to stream valid JSON text to a {@code java.io.Writer}.
 * <p>
 * Instances of this class are immutable, reusable, and thread-safe.
 */
public final class JsonGenerator {

    private final JsonGeneratorStyle jsonGeneratorStyle;

    public JsonGenerator() {
        this(PRETTY);
    }

    private JsonGenerator(final JsonGeneratorStyle jsonGeneratorStyle) {
        this.jsonGeneratorStyle = jsonGeneratorStyle;
    }

    /**
     * Streams a JSON representation of the given {@code WriteableJsonArray} to the given {@code Writer}.
     *
     * @param target             the {@code Writer} to output to.
     * @param writeableJsonArray the {@code WriteableJsonArray} to output.
     * @throws IOException       if there was a problem writing to the {@code Writer}.
     */
    public void generate(final Writer target, final WriteableJsonArray writeableJsonArray) throws IOException {
        jsonGeneratorStyle.jsonWriter.write(target, writeableJsonArray);
    }

    /**
     * Generates a JSON representation of the given {@code WriteableJsonArray} as a {@code String}.
     *
     * @param writeableJsonArray the {@code WriteableJsonArray} to output.
     * @return                   a JSON representation of the given {@code WriteableJsonArray} as a {@code String}
     * @throws IOException       propagated from {@link WriteableJsonArray#writeTo(ArrayWriter)}.
     */
    public String generate(final WriteableJsonArray writeableJsonArray) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generate(stringBuilderWriter, writeableJsonArray);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Streams a JSON representation of the given {@code WriteableJsonObject} to the given {@code Writer}.
     *
     * @param target              the {@code Writer} to output to.
     * @param writeableJsonObject the {@code WriteableJsonObject} to output.
     * @throws IOException        if there was a problem writing to the {@code Writer}.
     */
    public void generate(final Writer target, final WriteableJsonObject writeableJsonObject) throws IOException {
        jsonGeneratorStyle.jsonWriter.write(target, writeableJsonObject);
    }

    /**
     * Generates a JSON representation of the given {@code WriteableJsonObject} as a {@code String}.
     *
     * @param writeableJsonObject the {@code WriteableJsonObject} to output.
     * @return                    a JSON representation of the given {@code WriteableJsonObject} as a {@code String}
     * @throws IOException        propagated from {@link WriteableJsonObject#writeTo(ObjectWriter)}.
     */
    public String generate(final WriteableJsonObject writeableJsonObject) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generate(stringBuilderWriter, writeableJsonObject);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Streams a JSON representation of the given {@code WriteableJsonString} to the given {@code Writer}.
     *
     * @param target              the {@code Writer} to output to.
     * @param writeableJsonString the {@code WriteableJsonString} to output.
     * @throws IOException        if there was a problem writing to the {@code Writer}.
     */
    public void generate(final Writer target, final WriteableJsonString writeableJsonString) throws IOException {
        jsonGeneratorStyle.jsonWriter.write(target, writeableJsonString);
    }

    /**
     * Generates a JSON representation of the given {@code WriteableJsonString} as a {@code String}.
     *
     * @param writeableJsonString the {@code WriteableJsonString} to output.
     * @return                    a JSON representation of the given {@code WriteableJsonString} as a {@code String}
     * @throws IOException        propagated from {@link WriteableJsonString#writeTo(Writer)}.
     */
    public String generate(final WriteableJsonString writeableJsonString) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generate(stringBuilderWriter, writeableJsonString);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Streams a JSON representation of the given {@code WriteableJsonNumber} to the given {@code Writer}.
     *
     * @param target                    the {@code Writer} to output to.
     * @param writeableJsonNumber       the {@code WriteableJsonNumber} to output.
     * @throws IOException              if there was a problem writing to the {@code Writer}.
     * @throws IllegalArgumentException if the characters written by the {@code WriteableJsonNumber} don't constitute a complete JSON number.
     */
    public void generate(final Writer target, final WriteableJsonNumber writeableJsonNumber) throws IOException {
        jsonGeneratorStyle.jsonWriter.write(target, writeableJsonNumber);
    }

    /**
     * Generates a JSON representation of the given {@code WriteableJsonNumber} as a {@code String}.
     *
     * @param writeableJsonNumber       the {@code WriteableJsonNumber} to output.
     * @return                          a JSON representation of the given {@code WriteableJsonNumber} as a {@code String}
     * @throws IOException              propagated from {@link WriteableJsonNumber#writeTo(Writer)}.
     * @throws IllegalArgumentException if the characters written by the {@code WriteableJsonNumber} don't constitute a complete JSON number.
     */
    public String generate(final WriteableJsonNumber writeableJsonNumber) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generate(stringBuilderWriter, writeableJsonNumber);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Streams a JSON representation of the given {@code JsonNode} to the given {@code Writer}.
     *
     * @param target       the {@code Writer} to output to.
     * @param jsonNode     the {@code JsonNode} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    public void generate(final Writer target, final JsonNode jsonNode) throws IOException {
        jsonGeneratorStyle.jsonWriter.write(target, jsonNode);
    }

    /**
     * Generates a JSON representation of the given {@code JsonNode} as a {@code String}.
     *
     * @param jsonNode the {@code JsonNode} to output.
     * @return         a JSON representation of the given {@code JsonNode} as a {@code String}
     */
    public String generate(final JsonNode jsonNode) {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generate(stringBuilderWriter, jsonNode);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringBuilderWriter threw an IOException", e);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Streams a JSON representation of the given {@code JsonNode} to the given {@code Writer}, outputting fields in lexicographic order.
     * JSON does not mandate a particular ordering for the fields of an object, but for comparing JSON documents it can be convenient for field order to be consistent.
     * In the event that more than one field with the same name exists in an object, the relative order of those fields is preserved.
     * @param target       the {@code Writer} to output to.
     * @param jsonNode     the {@code JsonNode} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    public void generateWithFieldSorting(final Writer target, final JsonNode jsonNode) throws IOException {
        jsonGeneratorStyle.fieldSortingJsonFormatter.format(jsonNode, target);
    }

    /**
     * Generates a JSON representation of the given {@code JsonNode} as a {@code String}, outputting fields in lexicographic order.
     * JSON does not mandate a particular ordering for the fields of an object, but for comparing JSON documents it can be convenient for field order to be consistent.
     * In the event that more than one field with the same name exists in an object, the relative order of those fields is preserved.
     * @param jsonNode the {@code JsonNode} to output.
     * @return         a JSON representation of the given {@code JsonNode} as a {@code String}
     */
    public String generateWithFieldSorting(final JsonNode jsonNode) {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            generateWithFieldSorting(stringBuilderWriter, jsonNode);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringBuilderWriter threw an IOException", e);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    /**
     * Returns a {@code JsonGenerator} with the given output style.
     * Defaults to {@link JsonGeneratorStyle#PRETTY}.
     *
     * @param style the style of output to produce.
     * @return a {@code JsonGenerator} with the given output style.
     */
    public JsonGenerator style(final JsonGeneratorStyle style) {
        return new JsonGenerator(style);
    }

    /**
     * Styles of output a {@code JsonGenerator} can produce.
     */
    public enum JsonGeneratorStyle {

        /**
         * Includes newlines, tabs, and spaces to improve readability.
         */
        PRETTY(new PrettyJsonWriter(), PrettyJsonFormatter.fieldOrderNormalisingPrettyJsonFormatter()),

        /**
         * Excludes all optional whitespace to produce the briefest valid JSON.
         */
        COMPACT(new CompactJsonWriter(), CompactJsonFormatter.fieldOrderNormalisingCompactJsonFormatter());

        private final JsonWriter jsonWriter;
        private final JsonFormatter fieldSortingJsonFormatter;

        JsonGeneratorStyle(final JsonWriter jsonWriter, final JsonFormatter fieldSortingJsonFormatter) {
            this.jsonWriter = jsonWriter;
            this.fieldSortingJsonFormatter = fieldSortingJsonFormatter;
        }
    }
}
