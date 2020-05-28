/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNodeTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static argo.jdom.JsonNodeFactories.field;

final class MegaLongGeneratedExampleTest {
    @Test
    void generatedExamplePerformance() {
        final List<JsonField> fields = new ArrayList<>(300_000);
        for (int i = 0; i < 300_000; i++) {
            fields.add(field(JsonStringNodeTestBuilder.aStringNode(), JsonNodeFactories.object(
                    field(JsonStringNodeTestBuilder.aStringNode(), JsonStringNodeTestBuilder.aStringNode())
            )));
        }
        final JsonNode generatedExample = JsonNodeFactories.object(fields);

        generatedExample.getFields();
        generatedExample.getFieldList();
    }
}
