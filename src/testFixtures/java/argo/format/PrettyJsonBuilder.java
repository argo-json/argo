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

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;

public final class PrettyJsonBuilder {

    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(PRETTY);

    public static String json(final JsonNode jsonNode) {
        return JSON_GENERATOR.generate(jsonNode);
    }

    private PrettyJsonBuilder() {
    }
}
