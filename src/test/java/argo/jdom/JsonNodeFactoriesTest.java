/*
 * Copyright 2012 Mark Slater
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class JsonNodeFactoriesTest {

    @Test
    public void createsJsonArrayNodeWithJavaArrayOfElements() throws Exception {
        assertThat(
                array(string("Way there"), number(new BigDecimal("0.5")))
                , equalTo(
                array(asList(
                        string("Way there")
                        , number("0.5")
                ))
        ));
    }

    @Test
    public void createsALazyJsonArrayNode() throws Exception {
        List<JsonNode> elements = new ArrayList<>();
        JsonRootNode jsonRootNode = lazyArray(elements);
        elements.add(string("late element"));
        assertThat(
                jsonRootNode
                , equalTo(
                array(asList(
                        (JsonNode) string("late element")
                ))
        ));
    }

    @Test
    public void createsJsonObjectNodeWithFieldArray() throws Exception {
        assertThat(
                object(field("Gina", string("Dreams of running away")), field(string("Tommy"), string("Used to work on the dock")))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Gina"), string("Dreams of running away"));
            put(string("Tommy"), string("Used to work on the dock"));
        }}))
        );
    }

    @Test
    public void createsJsonObjectNodeWithFieldIterable() throws Exception {
        assertThat(object(asList(
                field("Gina", string("Dreams of running away"))
                , field("Tommy", string("Used to work on the dock"))
        )),
                equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
                    put(string("Gina"), string("Dreams of running away"));
                    put(string("Tommy"), string("Used to work on the dock"));
                }}))
        );
    }

    @Test
    public void createsALazyJsonObjectNode() throws Exception {
        List<JsonField> fields = new ArrayList<>();
        JsonRootNode jsonRootNode = lazyObject(fields);
        fields.add(field("late", string("field")));
        assertThat(
                jsonRootNode
                , equalTo(
                object(field("late", string("field")))
        ));
    }

    @Test
    public void createsJsonNumberNodeUsingABigInteger() throws Exception {
        assertThat(
                object(field("Number of shots to give it", number(BigInteger.ONE)))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Number of shots to give it"), number("1"));
        }}))
        );
    }

    @Test
    public void createsJsonNumberNodeUsingALong() throws Exception {
        assertThat(
                object(field("Number of shots to give it", number(1)))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Number of shots to give it"), number("1"));
        }}))
        );
    }

    @Test
    public void createsAJsonTrueFromAJavaBoolean() throws Exception {
        assertThat(
                booleanNode(true)
                , equalTo(trueNode())
        );
    }

    @Test
    public void createsAJsonFalseFromAJavaBoolean() throws Exception {
        assertThat(
                booleanNode(false)
                , equalTo(falseNode())
        );
    }

}
