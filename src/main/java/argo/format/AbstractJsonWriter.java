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

abstract class AbstractJsonWriter implements JsonWriter {

    public final void write(final Writer writer, final WriteableJsonArray writeableJsonArray) throws IOException {
        newJsonPrinter(writer).write(writeableJsonArray);
    }

    public final void write(final Writer writer, final WriteableJsonObject writeableJsonObject) throws IOException {
        newJsonPrinter(writer).write(writeableJsonObject);
    }

    public final void write(final Writer writer, final WriteableJsonString writeableJsonString) throws IOException {
        newJsonPrinter(writer).write(writeableJsonString);
    }

    public final void write(final Writer writer, final WriteableJsonNumber writeableJsonNumber) throws IOException {
        newJsonPrinter(writer).write(writeableJsonNumber);
    }

    public final void write(final Writer writer, final JsonNode jsonNode) throws IOException {
        try {
            jsonNode.visit(newJsonPrinter(writer));
        } catch (AbstractJsonPrinter.IORuntimeException e) {
            throw e.getCause();
        }
    }

    abstract AbstractJsonPrinter newJsonPrinter(Writer writer);

}
