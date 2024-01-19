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
 * A {@code JsonFormatter} provides operations to turn {@code JsonNode}s into valid JSON text.
 */ // TODO deprecate?
public interface JsonFormatter {

    /**
     * Returns the specified {@code JsonNode} formatted as a String.
     *
     * @param jsonNode the {@code JsonNode} to format.
     * @return the specified {@code JsonNode} formatted as a String.
     */
    String format(JsonNode jsonNode);

    /**
     * Streams the specified {@code JsonNode} formatted to the specified {@code Writer}.
     *
     * @param jsonNode the {@code JsonNode} to format.
     * @param writer       the {@code Writer} to stream the formatted {@code JsonNode} to.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void format(JsonNode jsonNode, Writer writer) throws IOException;

}
