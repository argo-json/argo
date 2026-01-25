/*
 *  Copyright 2026 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package argo;

import argo.format.WriteableJsonArray;
import argo.format.WriteableJsonNumber;
import argo.format.WriteableJsonObject;
import argo.format.WriteableJsonString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static argo.jdom.JsonNodeTestingFactories.aJsonNode;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonGeneratorTest {
    @Nested
    final class CapacityExceptionPropagation {
        @Test
        void writeableJsonArray() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generate((WriteableJsonArray) arrayWriter -> {}, -1));
        }

        @Test
        void writeableJsonObject() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generate((WriteableJsonObject) objectWriter -> {}, -1));
        }

        @Test
        void writeableJsonString() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generate((WriteableJsonString) writer -> {}, -1));
        }

        @Test
        void writeableJsonNumber() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generate((WriteableJsonNumber) writer -> {}, -1));
        }

        @Test
        void jsonNode() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generate(aJsonNode(), -1));
        }

        @Test
        void jsonNodeWithFieldSorting() {
            final JsonGenerator jsonGenerator = new JsonGenerator();
            assertThrows(NegativeArraySizeException.class, () -> jsonGenerator.generateWithFieldSorting(aJsonNode(), -1));
        }
    }
}