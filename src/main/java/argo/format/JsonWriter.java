/*
 *  Copyright  2020 Mark Slater
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
 * A {@code JsonWriter} provides operations to stream valid JSON text to a {@code java.io.Writer}.
 */
public interface JsonWriter {

    /**
     * Streams the specified {@code WriteableJsonArray} formatted to the specified {@code Writer}.
     *
     * @param writer             the {@code Writer} to output to.
     * @param writeableJsonArray the {@code WriteableJsonArray} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void write(Writer writer, WriteableJsonArray writeableJsonArray) throws IOException;

    /**
     * Streams the specified {@code WriteableJsonObject} formatted to the specified {@code Writer}.
     *
     * @param writer              the {@code Writer} to output to.
     * @param writeableJsonObject the {@code WriteableJsonObject} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void write(Writer writer, WriteableJsonObject writeableJsonObject) throws IOException;

    /**
     * Streams the specified {@code WriteableJsonString} formatted to the specified {@code Writer}.
     *
     * @param writer              the {@code Writer} to output to.
     * @param writeableJsonString the {@code WriteableJsonString} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void write(Writer writer, WriteableJsonString writeableJsonString) throws IOException;

    /**
     * Streams the specified {@code WriteableJsonNumber} formatted to the specified {@code Writer}.
     *
     * @param writer              the {@code Writer} to output to.
     * @param writeableJsonNumber the {@code WriteableJsonNumber} to output.
     * @throws IOException              if there was a problem writing to the {@code Writer}.
     * @throws IllegalArgumentException if the characters written by the {@code WriteableJsonNumber} didn't constitute a complete JSON number.
     */
    void write(Writer writer, WriteableJsonNumber writeableJsonNumber) throws IOException;

    /**
     * Streams the specified {@code JsonNode} formatted to the specified {@code Writer}.
     *
     * @param writer the {@code Writer} to output to.
     * @param jsonNode the {@code JsonNode} to output.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void write(Writer writer, JsonNode jsonNode) throws IOException;
}
