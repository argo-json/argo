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

final class StringJsonGeneratorJsonWriterTestCase implements JsonGeneratorJsonWriterTestCase {
    private final JsonGenerator jsonGenerator;

    StringJsonGeneratorJsonWriterTestCase(final JsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
    }

    @Override
    public String write(final WriteableJsonArray writeableJsonArray) throws IOException {
        return jsonGenerator.generate(writeableJsonArray);
    }

    @Override
    public String write(final WriteableJsonObject writeableJsonObject) throws IOException {
        return jsonGenerator.generate(writeableJsonObject);
    }

    @Override
    public String write(final WriteableJsonString writeableJsonString) throws IOException {
        return jsonGenerator.generate(writeableJsonString);
    }

    @Override
    public String write(final WriteableJsonNumber writeableJsonNumber) throws IOException {
        return jsonGenerator.generate(writeableJsonNumber);
    }

    @Override
    public String write(final JsonNode jsonNode) {
        return jsonGenerator.generate(jsonNode);
    }
}
