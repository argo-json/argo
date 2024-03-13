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

@SuppressWarnings("deprecation")
abstract class AbstractJsonFormatter implements JsonFormatter {

    private final JsonGenerator jsonGenerator;
    private final boolean fieldOrderNormalising;

    AbstractJsonFormatter(final JsonGenerator jsonGenerator, final boolean fieldOrderNormalising) {
        this.jsonGenerator = jsonGenerator;
        this.fieldOrderNormalising = fieldOrderNormalising;
    }

    public final String format(final JsonNode jsonNode) {
        if (fieldOrderNormalising) {
            return jsonGenerator.generateWithFieldSorting(jsonNode);
        } else {
            return jsonGenerator.generate(jsonNode);
        }
    }

    public final void format(final JsonNode jsonNode, final Writer writer) throws IOException {
        if (fieldOrderNormalising) {
            jsonGenerator.generateWithFieldSorting(writer, jsonNode);
        } else {
            jsonGenerator.generate(writer, jsonNode);
        }
    }
}
