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

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;

import java.io.IOException;

/**
 * An {@code ObjectWriter} provides operations for outputting the fields of a JSON object.
 */
public interface ObjectWriter {

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(String name, WriteableJsonObject value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(String name, WriteableJsonArray value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(String name, JsonNode value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(String name, WriteableJsonString value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(JsonStringNode name, WriteableJsonObject value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(JsonStringNode name, WriteableJsonArray value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(JsonStringNode name, WriteableJsonString value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param name  the name of the field.
     * @param value the value of the field.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(JsonStringNode name, JsonNode value) throws IOException;

    /**
     * Writes the given name and value as the next field of the object.
     *
     * @param jsonField the field to write.
     * @throws IOException if there was a problem writing the field.
     */
    void writeField(JsonField jsonField) throws IOException;
}
