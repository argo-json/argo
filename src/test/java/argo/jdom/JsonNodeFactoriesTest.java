/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static argo.jdom.JsonNodeFactories.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

final class JsonNodeFactoriesTest {

    @Test
    void createsJsonArrayNodeWithJavaArrayOfElements() {
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
    void createsALazyJsonArrayNode() {
        List<JsonNode> elements = new ArrayList<>();
        JsonNode jsonNode = lazyArray(elements);
        elements.add(string("late element"));
        assertThat(
                jsonNode
                , equalTo(
                        array(singletonList(
                        (JsonNode) string("late element")
                ))
        ));
    }

    @Test
    void createsJsonObjectNodeWithFieldArray() {
        assertThat(
                object(field("Gina", string("Dreams of running away")), field(string("Tommy"), string("Used to work on the dock")))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Gina"), string("Dreams of running away"));
            put(string("Tommy"), string("Used to work on the dock"));
        }}))
        );
    }

    @Test
    void createsJsonObjectNodeWithFieldIterator() {
        assertThat(object(asList(
                field("Gina", string("Dreams of running away"))
                , field("Tommy", string("Used to work on the dock"))
                ).iterator()),
                equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
                    put(string("Gina"), string("Dreams of running away"));
                    put(string("Tommy"), string("Used to work on the dock"));
                }}))
        );
    }

    @Test
    void createsJsonObjectNodeWithFieldIterable() {
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
    void createsALazyJsonObjectNode() {
        List<JsonField> fields = new ArrayList<>();
        JsonNode jsonNode = lazyObject(fields);
        fields.add(field("late", string("field")));
        assertThat(
                jsonNode
                , equalTo(
                object(field("late", string("field")))
        ));
    }

    @Test
    void createsJsonNumberNodeUsingABigInteger() {
        assertThat(
                object(field("Number of shots to give it", number(BigInteger.ONE)))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Number of shots to give it"), number("1"));
        }}))
        );
    }

    @Test
    void createsJsonNumberNodeUsingALong() {
        assertThat(
                object(field("Number of shots to give it", number(1)))
                , equalTo(object(new HashMap<JsonStringNode, JsonNode>() {{
            put(string("Number of shots to give it"), number("1"));
        }}))
        );
    }

    @Test
    void createsAJsonTrueFromAJavaBoolean() {
        assertThat(
                booleanNode(true)
                , equalTo(trueNode())
        );
    }

    @Test
    void createsAJsonFalseFromAJavaBoolean() {
        assertThat(
                booleanNode(false)
                , equalTo(falseNode())
        );
    }

    @Test
    void nullableBooleanNodeCreatesAJsonTrueFromAJavaBoolean() {
        assertThat(
                nullableBooleanNode(true)
                , equalTo(trueNode())
        );
    }

    @Test
    void nullableBooleanNodeCreatesAJsonFalseFromAJavaBoolean() {
        assertThat(
                nullableBooleanNode(false)
                , equalTo(falseNode())
        );
    }

    @Test
    void nullableBooleanNodeCreatesAJsonNullFromAJavaNull() {
        assertThat(
                nullableBooleanNode(null)
                , equalTo(nullNode())
        );
    }

    @Test
    void usesAConstantForEmptyObjects() {
        assertThat(object(), sameInstance(object()));
    }


    @Test
    void usesAConstantForEmptyObjectsConstructedFromAMap() {
        assertThat(object(emptyMap()), sameInstance(object(emptyMap())));
    }

    @Test
    void usesAConstantForEmptyArrays() {
        assertThat(array(), sameInstance(array()));
    }
}
