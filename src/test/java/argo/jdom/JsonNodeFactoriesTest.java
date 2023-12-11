/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.MapBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static argo.MapBuilder.mapBuilder;
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
        final List<JsonNode> elements = new ArrayList<>();
        final JsonNode jsonNode = lazyArray(elements);
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
    void createsNullableJsonArrayNodeWithJavaIterableOfElements() {
        assertThat(
                nullableArray(asList(string("Way there"), number(new BigDecimal("0.5"))))
                , equalTo(
                        array(asList(
                                string("Way there")
                                , number("0.5")
                        ))
                ));
    }

    @Test
    void createsNullableJsonArrayNodeWithJavaIteratorOfElements() {
        assertThat(
                nullableArray(asList(string("Way there"), number(new BigDecimal("0.5"))).iterator())
                , equalTo(
                        array(asList(
                                string("Way there")
                                , number("0.5")
                        ))
                ));
    }

    @Test
    void createsNullableJsonArrayNodeWithJavaArrayOfElements() {
        assertThat(
                nullableArray(string("Way there"), number(new BigDecimal("0.5")))
                , equalTo(
                        array(asList(
                                string("Way there")
                                , number("0.5")
                        ))
                ));
    }

    @Test
    void createsANullableLazyJsonArrayNode() {
        final List<JsonNode> elements = new ArrayList<>();
        final JsonNode jsonNode = nullableLazyArray(elements);
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
    void createsNullNodeWithNullJavaIterableOfElements() {
        assertThat(
                nullableArray((Iterable<? extends JsonNode>) null)
                , equalTo(nullNode()));
    }

    @Test
    void createsNullNodeWithNullJavaIteratorOfElements() {
        assertThat(
                nullableArray((Iterator<? extends JsonNode>) null)
                , equalTo(nullNode()));
    }

    @Test
    void createsNullNodeWithNullJavaArrayOfElements() {
        assertThat(
                nullableArray((JsonNode[]) null)
                , equalTo(nullNode()));
    }

    @Test
    void createsANullNodeWithLazyJsonArrayNull() {
        final JsonNode jsonNode = nullableLazyArray(null);
        assertThat(
                jsonNode
                , equalTo(nullNode()));
    }

    @Test
    void createsJsonObjectNodeWithFieldArray() {
        assertThat(
                object(field("Gina", string("Dreams of running away")), field(string("Tommy"), string("Used to work on the dock"))),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                        .put(string("Tommy"), string("Used to work on the dock"))
                        .build()
                ))
        );
    }

    @Test
    void createsJsonObjectNodeWithFieldIterator() {
        assertThat(object(asList(
                field("Gina", string("Dreams of running away"))
                , field("Tommy", string("Used to work on the dock"))
                ).iterator()),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsJsonObjectNodeWithFieldIterable() {
        assertThat(object(asList(
                field("Gina", string("Dreams of running away"))
                , field("Tommy", string("Used to work on the dock"))
                )),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsALazyJsonObjectNode() {
        final List<JsonField> fields = new ArrayList<>();
        final JsonNode jsonNode = lazyObject(fields);
        fields.add(field("late", string("field")));
        assertThat(
                jsonNode
                , equalTo(
                        object(field("late", string("field")))
                ));
    }

    @Test
    void createsNullableJsonObjectNodeWithMap() {
        final Map<JsonStringNode, JsonNode> fields = MapBuilder.<JsonStringNode, JsonNode>mapBuilder(string("Gina"), string("Dreams of running away"))
                .put(string("Tommy"), string("Used to work on the dock"))
                .build();
        assertThat(
                nullableObject(fields),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsNullableJsonObjectNodeWithFieldArray() {
        assertThat(
                nullableObject(field("Gina", string("Dreams of running away")), field(string("Tommy"), string("Used to work on the dock"))),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsNullableJsonObjectNodeWithFieldIterator() {
        assertThat(nullableObject(asList(
                        field("Gina", string("Dreams of running away")),
                        field("Tommy", string("Used to work on the dock"))
                ).iterator()),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsNullableJsonObjectNodeWithFieldIterable() {
        assertThat(nullableObject(asList(
                        field("Gina", string("Dreams of running away")),
                        field("Tommy", string("Used to work on the dock"))
                )),
                equalTo(object(
                        mapBuilder(string("Gina"), string("Dreams of running away"))
                                .put(string("Tommy"), string("Used to work on the dock"))
                                .build()
                ))
        );
    }

    @Test
    void createsANullableLazyJsonObjectNode() {
        final List<JsonField> fields = new ArrayList<>();
        final JsonNode jsonNode = nullableLazyObject(fields);
        fields.add(field("late", string("field")));
        assertThat(
                jsonNode
                , equalTo(
                        object(field("late", string("field")))
                ));
    }

    @Test
    void nullableJsonObjectNodeWithMapCreatesNullNode() {
        assertThat(
                nullableObject((Map<JsonStringNode, ? extends JsonNode>) null)
                , equalTo(nullNode())
        );
    }

    @Test
    void nullableJsonObjectNodeWithFieldArrayCreatesNullNode() {
        assertThat(
                nullableObject((JsonField[]) null)
                , equalTo(nullNode())
        );
    }

    @Test
    void nullableJsonObjectNodeWithFieldIteratorCreatesNullNode() {
        assertThat(
                nullableObject((Iterator<JsonField>) null)
                , equalTo(nullNode())
        );
    }

    @Test
    void nullableJsonObjectNodeWithFieldIterableCreatesNullNode() {
        assertThat(
                nullableObject((Iterable<JsonField>) null)
                , equalTo(nullNode())
        );
    }

    @Test
    void aNullableLazyJsonObjectNodeCreatesNullNode() {
        final JsonNode jsonNode = nullableLazyObject(null);
        assertThat(
                jsonNode
                , equalTo(
                        nullNode()
                ));
    }

    @Test
    void createsJsonNumberNodeUsingABigInteger() {
        assertThat(
                object(field("Number of shots to give it", number(BigInteger.ONE))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void createsJsonNumberNodeUsingALong() {
        assertThat(
                object(field("Number of shots to give it", number(1))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNumberNodeUsingABigInteger() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber(BigInteger.ONE))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNumberNodeUsingABigDecimal() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber(BigDecimal.ONE))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNumberNodeUsingALong() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber(1L))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNumberNodeUsingAString() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber("1"))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), number("1")).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNullNodeUsingANullBigInteger() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber((BigInteger) null))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), nullNode()).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNullNodeUsingANullBigDecimal() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber((BigDecimal) null))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), nullNode()).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNullNodeUsingANullLong() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber((Long) null))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), nullNode()).build()))
        );
    }

    @Test
    void nullableNumberCreatesJsonNullNodeUsingANullString() {
        assertThat(
                object(field("Number of shots to give it", nullableNumber((String) null))),
                equalTo(object(mapBuilder(string("Number of shots to give it"), nullNode()).build()))
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
    void nullableStringNodeCreatesAJsonStringFromAJavaString() {
        assertThat(
                nullableString("Push pineapple")
                , equalTo(string("Push pineapple"))
        );
    }

    @Test
    void nullableStringNodeCreatesAJsonNullFromAJavaNull() {
        assertThat(
                nullableString(null)
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
