/*
 * Copyright 2013 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import static argo.jdom.JsonNodeBuilderTestBuilder.aJsonNodeBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class JsonObjectNodeBuilderTest {
    @Test
    public void permitsDuplicatedFieldNames() throws Exception {
        final JsonStringNode fieldName = aStringNode();
        final JsonNodeBuilder firstValue = aJsonNodeBuilder();
        final JsonNodeBuilder secondValue = aJsonNodeBuilder();
        assertThat(
                anObjectBuilder()
                        .withField(fieldName, firstValue)
                        .withField(fieldName, secondValue)
                        .build(),
                equalTo(
                        object(
                                field(fieldName, firstValue.build()),
                                field(fieldName, secondValue.build())
                        )));
    }

}
