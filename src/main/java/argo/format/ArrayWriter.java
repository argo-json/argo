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

/**
 * An {@code ArrayWriter} provides operations for outputting the elements of a JSON array.
 */
public interface ArrayWriter {

    /**
     * Writes the given {@code WriteableJsonObject} as the next element of the array.
     *
     * @param element the {@code WriteableJsonObject} to write.
     * @throws IOException if there was a problem writing the {@code WriteableJsonObject}
     */
    void writeElement(WriteableJsonObject element) throws IOException;

    /**
     * Writes the given {@code WriteableJsonArray} as the next element of the array.
     *
     * @param element the {@code WriteableJsonArray} to write.
     * @throws IOException if there was a problem writing the {@code WriteableJsonArray}
     */
    void writeElement(WriteableJsonArray element) throws IOException;

    /**
     * Writes the given {@code JsonNode} as the next element of the array.
     * @param element the {@code JsonNode} to write.
     * @throws IOException if there was a problem writing the {@code JsonNode}
     */
    void writeElement(JsonNode element) throws IOException;
}
