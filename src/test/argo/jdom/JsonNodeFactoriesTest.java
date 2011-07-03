/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class JsonNodeFactoriesTest {

    @Test
    public void createsJsonArrayNodeWithJavaArrayOfElements() throws Exception {
        assertThat(
                aJsonArray(
                        aJsonString("Way there")
                        , aJsonNumber(new BigDecimal("0.5"))
                )
                , equalTo(
                aJsonArray(
                        asList(
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
                        , aJsonField(aJsonString("Tommy"), aJsonString("Used to work on the dock"))
                )
                , equalTo(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Gina"), aJsonString("Dreams of running away"));
            put(aJsonString("Tommy"), aJsonString("Used to work on the dock"));
        }}))
        );
    }

    @Test
    public void createsJsonObjectNodeWithFieldIterable() throws Exception {
        assertThat(aJsonObject(
                asList(
                        aJsonField("Gina", aJsonString("Dreams of running away"))
                        , aJsonField("Tommy", aJsonString("Used to work on the dock"))
                )
        ),
                equalTo(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
                    put(aJsonString("Gina"), aJsonString("Dreams of running away"));
                    put(aJsonString("Tommy"), aJsonString("Used to work on the dock"));
                }}))
        );
    }

    @Test
    public void createsJsonNumberNodeUsingABigInteger() throws Exception {
        assertThat(
                aJsonObject(
                        aJsonField("Number of shots to give it", aJsonNumber(new BigInteger("1")))
                )
                , equalTo(aJsonObject(new HashMap<JsonStringNode, JsonNode>() {{
            put(aJsonString("Number of shots to give it"), aJsonNumber("1"));
        }}))
        );
    }

    @Test
    public void createsAJsonTrueFromAJavaBoolean() throws Exception {
        assertThat(
                JsonNodeFactories.aJsonBoolean(true)
                , equalTo(aJsonTrue())
        );
    }

    @Test
    public void createsAJsonFalseFromAJavaBoolean() throws Exception {
        assertThat(
                JsonNodeFactories.aJsonBoolean(false)
                , equalTo(aJsonFalse())
        );
    }

}
