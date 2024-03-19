/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonConstantTest {

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    @SuppressWarnings("EqualsWithItself")
    void testEqualsSameObject(final JsonNode jsonNode) {
        assertThat(jsonNode.equals(jsonNode), equalTo(true));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void testNotEqualsNull(final JsonNode jsonNode) {
        assertThat(jsonNode.equals(null), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testNotEqualsObjectOfDifferentType(final JsonNode jsonNode) {
        assertThat(jsonNode.equals(JsonNodeFactories.number("42")), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testHashCode(final JsonNode jsonNode) {
        assertEquals(jsonNode.hashCode(), jsonNode.hashCode());
    }

    @Test
    void testNullToString() {
        assertThat(nullNode().toString(), equalTo("JsonNode{jsonNodeType=NULL}"));
    }

    @Test
    void testTrueToString() {
        assertThat(trueNode().toString(), equalTo("JsonNode{jsonNodeType=TRUE}"));
    }

    @Test
    void testFalseToString() {
        assertThat(falseNode().toString(), equalTo("JsonNode{jsonNodeType=FALSE}"));
    }

    @Test
    void getTypeOnNullReturnsNull() {
        assertThat(nullNode().getType(), equalTo(JsonNodeType.NULL));
    }

    @Test
    void getTypeOnTrueReturnsTrue() {
        assertThat(trueNode().getType(), equalTo(JsonNodeType.TRUE));
    }

    @Test
    void getTypeOnFalseReturnsFalse() {
        assertThat(falseNode().getType(), equalTo(JsonNodeType.FALSE));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void hasTextReturnsFalse(final JsonNode jsonNode) {
        assertThat(jsonNode.hasText(), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testGetTextThrowsUnsupportedOperationException(final JsonNode jsonNode) {
        assertThrows(UnsupportedOperationException.class, jsonNode::getText);
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void hasFieldsReturnsFalse(final JsonNode jsonNode) {
        assertThat(jsonNode.hasFields(), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testGetFieldsThrowsUnsupportedOperationException(final JsonNode jsonNode) {
        assertThrows(UnsupportedOperationException.class, jsonNode::getFields);
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testGetFieldListThrowsUnsupportedOperationException(final JsonNode jsonNode) {
        assertThrows(UnsupportedOperationException.class, jsonNode::getFieldList);
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void hasElementsReturnsFalse(final JsonNode jsonNode) {
        assertThat(jsonNode.hasElements(), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonConstantsArgumentsProvider.class)
    void testGetElementsThrowsUnsupportedOperationException(final JsonNode jsonNode) {
        assertThrows(UnsupportedOperationException.class, jsonNode::getElements);
    }

    static final class JsonConstantsArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    nullNode(),
                    trueNode(),
                    falseNode()
            ).map(Arguments::arguments);
        }
    }

}