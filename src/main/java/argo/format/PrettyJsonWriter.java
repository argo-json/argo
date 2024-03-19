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

import java.io.Writer;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;

/**
 * JsonWriter that writes JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 *
 * @deprecated Replaced by {@link JsonGenerator}.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class PrettyJsonWriter extends AbstractJsonWriter {

    /**
     * @deprecated Replaced by {@link JsonGenerator#generate(Writer, JsonNode)} and other similar {@link JsonGenerator} methods.
     * For example, use
     * {@code new JsonGenerator().style(PRETTY).generate(Writer, JsonNode) }
     * or
     * {@code new JsonGenerator().style(PRETTY).generate(Writer, WriteableJsonObject) }
     */
    public PrettyJsonWriter() {
        super(new JsonGenerator().style(PRETTY));
    }

}
