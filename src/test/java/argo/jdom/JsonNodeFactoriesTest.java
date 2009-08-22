/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public final class JsonNodeFactoriesTest {

    @Test
    public void createsJsonArrayNodeWithJavaArrayOfElements() throws Exception {
        assertThat(
                aJsonArray(
                        aJsonString("Way there")
                        , aJsonNumber("0.5")
                )
                , equalTo(
                        aJsonArray(
                                Arrays.asList(
                                        aJsonString("Way there")
                                        , aJsonNumber("0.5")
                                )
                        )
                ));
    }

    @Test
    public void createsJsonObjectNodeWithFieldArray() throws Exception {
        assertThat(
                aJsonObject(
                aJsonField("Gina", aJsonString("Dreams of running away"))
                , aJsonField("Tommy", aJsonString("Used to work on the dock"))
        )
                ,equalTo(aJsonObject(new HashMap<JsonStringNode, JsonNode>(){{
                    put(aJsonString("Gina"), aJsonString("Dreams of running away"));
                    put(aJsonString("Tommy"), aJsonString("Used to work on the dock"));
                }}))
        );
    }

}
