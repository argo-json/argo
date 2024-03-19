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

import argo.JsonParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static argo.MapBuilder.mapBuilder;
import static argo.TestingFactories.aSmallNonNegativeInt;
import static argo.TestingFactories.aString;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeSelectors.*;
import static argo.jdom.JsonNodeSelectorsTest.TreeTestCase.TestCase.expectCandidateTestCase;
import static argo.jdom.JsonNodeSelectorsTest.TreeTestCase.TestCase.mismatchTestCase;
import static argo.jdom.JsonNodeSelectorsTest.TreeTestCase.TestCase.testCase;
import static argo.jdom.JsonNodeTestingFactories.aJsonNode;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNodeDifferentTo;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNodeSelectorsTest {

    @Test
    void javadocExampleWorks() throws Exception {
        final String json = "{\"Fee\":{\"fi\":\"fo\"}}";
        final JsonNode jsonNode = new JsonParser().parse(new StringReader(json));
        final String result = JsonNodeSelectors.anObjectNodeWithField("Fee")
                .with(JsonNodeSelectors.anObjectNodeWithField("fi"))
                .with(JsonNodeSelectors.aStringNode())
                .getValue(jsonNode);
        assertThat(result, equalTo("fo"));
    }

    static class TreeTestCase {

        private static Stream<? extends Arguments> nestedPermutations(final TestCase... testCases) {
            return Arrays.stream(testCases).flatMap(testCase -> Stream.of(
                    Arguments.arguments(testCase.candidate, new Object[]{}, testCase.expectedMatch, testCase.expectedValue),
                    Arguments.arguments(array(testCase.candidate), new Object[]{0}, testCase.expectedMatch, testCase.expectedValue),
                    Arguments.arguments(object(field("foo", testCase.candidate)), new Object[]{"foo"}, testCase.expectedMatch, testCase.expectedValue)
            ));
        }

        static final class TestCase {
            final JsonNode candidate;
            final boolean expectedMatch;
            final Object expectedValue;

            private TestCase(final JsonNode candidate, boolean expectedMatch, final Object expectedValue) {
                this.candidate = candidate;
                this.expectedMatch = expectedMatch;
                this.expectedValue = expectedValue;
            }

            static TestCase testCase(final JsonNode candidate, final Object expected) {
                return new TestCase(candidate, true, expected);
            }

            static TestCase mismatchTestCase(final JsonNode candidate) {
                return new TestCase(candidate, false, null);
            }

            static TestCase expectCandidateTestCase(final JsonNode candidate) {
                return new TestCase(candidate, true, candidate);
            }
        }

        static final class JsonNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return nestedPermutations(
                        expectCandidateTestCase(nullNode()),
                        expectCandidateTestCase(trueNode()),
                        expectCandidateTestCase(falseNode()),
                        expectCandidateTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        expectCandidateTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        expectCandidateTestCase(JsonNodeTestingFactories.anObjectNode()),
                        expectCandidateTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class JsonStringNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonStringNode jsonStringNode = JsonStringNodeTestingFactories.aStringNode();
                return nestedPermutations(
                        mismatchTestCase(nullNode()),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        testCase(jsonStringNode, jsonStringNode.getText()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class NullableJsonStringNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonStringNode jsonStringNode = JsonStringNodeTestingFactories.aStringNode();
                return nestedPermutations(
                        testCase(nullNode(), null),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        testCase(jsonStringNode, jsonStringNode.getText()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class JsonNumberNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNumberNodeTestingFactories.aNumberNode();
                return nestedPermutations(
                        mismatchTestCase(nullNode()),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        testCase(jsonNode, jsonNode.getText()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class NullableJsonNumberNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNumberNodeTestingFactories.aNumberNode();
                return nestedPermutations(
                        testCase(nullNode(), null),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        testCase(jsonNode, jsonNode.getText()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class JsonBooleanNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return nestedPermutations(
                        mismatchTestCase(nullNode()),
                        testCase(trueNode(), true),
                        testCase(falseNode(), false),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class NullableJsonBooleanNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return nestedPermutations(
                        testCase(nullNode(), null),
                        testCase(trueNode(), true),
                        testCase(falseNode(), false),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class JsonNullNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                return nestedPermutations(
                        expectCandidateTestCase(nullNode()),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class JsonArrayNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNodeTestingFactories.anArrayNode();
                return nestedPermutations(
                        mismatchTestCase(nullNode()),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        testCase(jsonNode, jsonNode.getElements())
                );
            }
        }

        static final class NullableJsonArrayNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNodeTestingFactories.anArrayNode();
                return nestedPermutations(
                        testCase(nullNode(), null),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        mismatchTestCase(JsonNodeTestingFactories.anObjectNode()),
                        testCase(jsonNode, jsonNode.getElements())
                );
            }
        }

        static final class JsonObjectNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNodeTestingFactories.anObjectNode();
                return nestedPermutations(
                        mismatchTestCase(nullNode()),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        testCase(jsonNode, jsonNode.getFields()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }

        static final class NullableJsonObjectNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonNode jsonNode = JsonNodeTestingFactories.anObjectNode();
                return nestedPermutations(
                        testCase(nullNode(), null),
                        mismatchTestCase(trueNode()),
                        mismatchTestCase(falseNode()),
                        mismatchTestCase(JsonNumberNodeTestingFactories.aNumberNode()),
                        mismatchTestCase(JsonStringNodeTestingFactories.aStringNode()),
                        testCase(jsonNode, jsonNode.getFields()),
                        mismatchTestCase(JsonNodeTestingFactories.anArrayNode())
                );
            }
        }
    }

    static class NodeTestCase {

        static final class FieldByStringArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final String key = aString();
                final JsonNode value = aJsonNode();
                final JsonStringNode mismatchKey = aStringNodeDifferentTo(string(key));
                final JsonNode candidateJsonObject = object(mapBuilder(string(key), value)
                        .put(mismatchKey, aJsonNode())
                        .build());
                return Stream.of(
                        Arguments.arguments(nullNode(), aString(), false, null),
                        Arguments.arguments(trueNode(), aString(), false, null),
                        Arguments.arguments(falseNode(), aString(), false, null),
                        Arguments.arguments(JsonNumberNodeTestingFactories.aNumberNode(), aString(), false, null),
                        Arguments.arguments(JsonStringNodeTestingFactories.aStringNode(), aString(), false, null),
                        Arguments.arguments(candidateJsonObject, key, true, value),
                        Arguments.arguments(JsonNodeTestingFactories.anArrayNode(), aString(), false, null),
                        Arguments.arguments(candidateJsonObject, aStringNodeDifferentTo(string(key), mismatchKey).getText(), false, null)
                );
            }
        }

        static final class FieldByJsonStringNodeArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final JsonStringNode key = JsonStringNodeTestingFactories.aStringNode();
                final JsonNode value = aJsonNode();
                final JsonStringNode mismatchKey = aStringNodeDifferentTo(key);
                final JsonNode candidateJsonObject = object(mapBuilder(key, value)
                        .put(mismatchKey, aJsonNode())
                        .build());
                return Stream.of(
                        Arguments.arguments(nullNode(), aString(), false, null),
                        Arguments.arguments(trueNode(), aString(), false, null),
                        Arguments.arguments(falseNode(), aString(), false, null),
                        Arguments.arguments(JsonNumberNodeTestingFactories.aNumberNode(), aString(), false, null),
                        Arguments.arguments(JsonStringNodeTestingFactories.aStringNode(), aString(), false, null),
                        Arguments.arguments(candidateJsonObject, key, true, value),
                        Arguments.arguments(JsonNodeTestingFactories.anArrayNode(), aString(), false, null),
                        Arguments.arguments(candidateJsonObject, aStringNodeDifferentTo(key, mismatchKey), false, null)
                );
            }
        }

        static final class ArrayElementByIndexArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
                final int index = aSmallNonNegativeInt();
                final JsonNode value = aJsonNode();
                final List<JsonNode> candidate = Stream.concat(
                                Stream.concat(
                                        Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(index),
                                        Stream.of(value)
                                ),
                                Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(aSmallNonNegativeInt()))
                        .collect(toList());
                return Stream.of(
                        Arguments.arguments(nullNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(trueNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(falseNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(JsonNumberNodeTestingFactories.aNumberNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(JsonStringNodeTestingFactories.aStringNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(JsonNodeTestingFactories.anObjectNode(), aSmallNonNegativeInt(), false, null),
                        Arguments.arguments(array(candidate), index, true, value),
                        Arguments.arguments(array(candidate), candidate.size(), false, null)
                );
            }
        }
    }

    @Nested
    final class AnyNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.anyNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean ignored, final JsonNode expected) {
            assertThat(JsonNodeSelectors.anyNode(path).getValue(candidateMatch), equalTo(expected));
        }

        @Test
        void matchesRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anyNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anyNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(anyNode().toString(), equalTo("any value"));
        }
    }

    @Nested
    final class AStringNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonStringNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aStringNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonStringNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final String expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aStringNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aStringNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a string] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aStringNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aStringNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aStringNode(new Object[]{null}));
        }

        @Test
        void rejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aStringNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aStringNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aStringNode().toString(), equalTo("a value that is a string"));
        }
    }

    @Nested
    final class ANullableStringNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonStringNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullableStringNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonStringNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final String expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullableStringNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableStringNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a string or null] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableStringNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableStringNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableStringNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableStringNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableStringNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullableStringNode().toString(), equalTo("a value that is a string or null"));
        }
    }

    @Nested
    final class ANumberNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNumberNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNumberNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNumberNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final String expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNumberNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNumberNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a number] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNumberNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNumberNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNumberNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNumberNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNumberNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNumberNode().toString(), equalTo("a value that is a number"));
        }
    }

    @Nested
    final class ANullableNumberNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonNumberNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullableNumberNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonNumberNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final String expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullableNumberNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableNumberNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a number or null] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableNumberNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableNumberNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableNumberNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableNumberNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableNumberNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullableNumberNode().toString(), equalTo("a value that is a number or null"));
        }
    }

    @Nested
    final class ABooleanNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonBooleanNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aBooleanNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonBooleanNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final Boolean expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aBooleanNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aBooleanNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a true or false] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aBooleanNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aBooleanNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aBooleanNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aBooleanNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aBooleanNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aBooleanNode().toString(), equalTo("a value that is true or false"));
        }
    }

    @Nested
    final class ANullableBooleanNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonBooleanNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullableBooleanNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonBooleanNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final Boolean expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullableBooleanNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableBooleanNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [a true or false or null] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableBooleanNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableBooleanNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableBooleanNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableBooleanNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableBooleanNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullableBooleanNode().toString(), equalTo("a value that is true or false or null"));
        }
    }

    @Nested
    final class ANullNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNullNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonNullNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final JsonNode expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [null value] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullNode().toString(), equalTo("null value"));
        }
    }

    @Nested
    final class AnArrayNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonArrayNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.anArrayNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonArrayNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final List<JsonNode> expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.anArrayNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anArrayNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [an array] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anArrayNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(anArrayNode().toString(), equalTo("an array"));
        }
    }

    @Nested
    final class ANullableArrayNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonArrayNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullableArrayNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonArrayNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final List<JsonNode> expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullableArrayNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableArrayNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [an array or null] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableArrayNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableArrayNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableArrayNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableArrayNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableArrayNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullableArrayNode().toString(), equalTo("an array or null"));
        }
    }

    @Nested
    final class AnObjectNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonObjectNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.anObjectNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.JsonObjectNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final Map<JsonStringNode, JsonNode> expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.anObjectNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anObjectNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [an object] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anObjectNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(anObjectNode().toString(), equalTo("an object"));
        }
    }

    @Nested
    final class ANullableObjectNode {
        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonObjectNodeArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.aNullableObjectNode(path).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(TreeTestCase.NullableJsonObjectNodeArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final Object[] path, final boolean expectedMatch, final Map<JsonStringNode, JsonNode> expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.aNullableObjectNode(path).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableObjectNode(path).getValue(candidateMatch));
                assertThat(illegalArgumentException.getMessage(), startsWith("Failed to match [an object or null] at ["));
            }
        }

        @Test
        void rejectsNullPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableObjectNode((Object[]) null));
        }

        @Test
        void rejectsInvalidTypeInPath() {
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aNullableObjectNode(new Object()));
        }

        @Test
        void rejectsNullInPath() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableObjectNode(new Object[]{null}));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableObjectNode().matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aNullableObjectNode().getValue(null));
        }

        @Test
        void toStringWorks() {
            assertThat(aNullableObjectNode().toString(), equalTo("an object or null"));
        }
    }

    @Nested
    final class AField {
        @Nested
        final class ByString {
            @Test
            void matches() {
                final String key = aString();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(string(key), aJsonNode())
                        .put(aStringNodeDifferentTo(string(key)), aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(key).matches(candidate), equalTo(true));
            }

            @Test
            void matchesMismatch() {
                final String key = aString();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(string(key), aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(aStringNodeDifferentTo(string(key)).getText()).matches(candidate), equalTo(false));
            }

            @Test
            void getValue() {
                final String key = aString();
                final JsonNode value = aJsonNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(string(key), value)
                        .put(aStringNodeDifferentTo(string(key)), aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(key).getValue(candidate), equalTo(value));
            }

            @Test
            void getValueOfMismatch() {
                final String key = aString();
                final JsonNode value = aJsonNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(string(key), value)
                        .build();
                assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aField(aStringNodeDifferentTo(string(key)).getText()).getValue(candidate));
            }

            @Test
            void rejectsNullFieldName() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField((String) null));
            }

            @Test
            void matchRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField(aString()).matches(null));
            }

            @Test
            void getValueRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField(aString()).getValue(null));
            }

            @Test
            void toStringWorks() {
                final String fieldName = aString();
                assertThat(aField(fieldName).toString(), equalTo("a field called [\"" + fieldName + "\"]"));
            }
        }

        @Nested
        final class ByJsonStringNode {
            @Test
            void matches() {
                final JsonStringNode key = JsonStringNodeTestingFactories.aStringNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(key, aJsonNode())
                        .put(aStringNodeDifferentTo(key), aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(key).matches(candidate), equalTo(true));
            }

            @Test
            void matchesMismatch() {
                final JsonStringNode key = JsonStringNodeTestingFactories.aStringNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(key, aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(aStringNodeDifferentTo(key).getText()).matches(candidate), equalTo(false));
            }

            @Test
            void getValue() {
                final JsonStringNode key = JsonStringNodeTestingFactories.aStringNode();
                final JsonNode value = aJsonNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(key, value)
                        .put(aStringNodeDifferentTo(key), aJsonNode())
                        .build();
                assertThat(JsonNodeSelectors.aField(key).getValue(candidate), equalTo(value));
            }

            @Test
            void getValueOfMismatch() {
                final JsonStringNode key = JsonStringNodeTestingFactories.aStringNode();
                final JsonNode value = aJsonNode();
                final Map<JsonStringNode, JsonNode> candidate = mapBuilder(key, value)
                        .build();
                assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.aField(aStringNodeDifferentTo(key).getText()).getValue(candidate));
            }

            @Test
            void rejectsNullFieldName() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField((JsonStringNode) null));
            }

            @Test
            void matchRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField(JsonStringNodeTestingFactories.aStringNode()).matches(null));
            }

            @Test
            void getValueRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.aField(JsonStringNodeTestingFactories.aStringNode()).getValue(null));
            }

            @Test
            void toStringWorks() {
                final JsonStringNode fieldName = JsonStringNodeTestingFactories.aStringNode();
                assertThat(aField(fieldName).toString(), equalTo("a field called [\"" + fieldName.getText() + "\"]"));
            }
        }
    }

    @Nested
    final class AnObjectNodeWithField {
        @Nested
        final class ByString {
            @ParameterizedTest
            @ArgumentsSource(NodeTestCase.FieldByStringArgumentsProvider.class)
            void matches(final JsonNode candidateMatch, final String fieldName, final boolean expectedMatch) {
                assertThat(JsonNodeSelectors.anObjectNodeWithField(fieldName).matches(candidateMatch), equalTo(expectedMatch));
            }

            @ParameterizedTest
            @ArgumentsSource(NodeTestCase.FieldByStringArgumentsProvider.class)
            void getValue(final JsonNode candidateMatch, final String fieldName, final boolean expectedMatch, final JsonNode expectedValue) {
                if (expectedMatch) {
                    assertThat(JsonNodeSelectors.anObjectNodeWithField(fieldName).getValue(candidateMatch), equalTo(expectedValue));
                } else {
                    assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anObjectNodeWithField(fieldName).getValue(candidateMatch));
                }
            }

            @Test
            void rejectsNullFieldName() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField((String) null));
            }

            @Test
            void matchRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField(aString()).matches(null));
            }

            @Test
            void getValueRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField(aString()).getValue(null));
            }

            @Test
            void toStringWorks() {
                final String fieldName = aString();
                assertThat(anObjectNodeWithField(fieldName).toString(), equalTo("an object, with a field called [\"" + fieldName + "\"]"));
            }
        }

        @Nested
        final class ByJsonStringNode {
            @ParameterizedTest
            @ArgumentsSource(NodeTestCase.FieldByJsonStringNodeArgumentsProvider.class)
            void matches(final JsonNode candidateMatch, final JsonStringNode fieldName, final boolean expectedMatch) {
                assertThat(JsonNodeSelectors.anObjectNodeWithField(fieldName).matches(candidateMatch), equalTo(expectedMatch));
            }

            @ParameterizedTest
            @ArgumentsSource(NodeTestCase.FieldByJsonStringNodeArgumentsProvider.class)
            void getValue(final JsonNode candidateMatch, final JsonStringNode fieldName, final boolean expectedMatch, final JsonNode expectedValue) {
                if (expectedMatch) {
                    assertThat(JsonNodeSelectors.anObjectNodeWithField(fieldName).getValue(candidateMatch), equalTo(expectedValue));
                } else {
                    assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anObjectNodeWithField(fieldName).getValue(candidateMatch));
                }
            }

            @Test
            void rejectsNullFieldName() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField((JsonStringNode) null));
            }

            @Test
            void matchRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField(JsonStringNodeTestingFactories.aStringNode()).matches(null));
            }

            @Test
            void getValueRejectsNullCandidate() {
                assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anObjectNodeWithField(JsonStringNodeTestingFactories.aStringNode()).getValue(null));
            }

            @Test
            void toStringWorks() {
                final JsonStringNode fieldName = JsonStringNodeTestingFactories.aStringNode();
                assertThat(anObjectNodeWithField(fieldName).toString(), equalTo("an object, with a field called [\"" + fieldName.getText() + "\"]"));
            }
        }
    }

    @Nested
    final class AnElement {
        @Test
        void matches() {
            final int index = aSmallNonNegativeInt();
            final List<JsonNode> candidate = Stream.concat(
                            Stream.concat(
                                    Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(index),
                                    Stream.of(aJsonNode())
                            ),
                            Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(aSmallNonNegativeInt()))
                    .collect(toList());
            assertThat(JsonNodeSelectors.anElement(index).matches(candidate), equalTo(true));
        }

        @Test
        void matchesMismatch() {
            final int size = aSmallNonNegativeInt();
            final List<JsonNode> candidate = Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(size).collect(toList());
            assertThat(JsonNodeSelectors.anElement(size).matches(candidate), equalTo(false));
        }

        @Test
        void getValue() {
            final int index = aSmallNonNegativeInt();
            final JsonNode value = aJsonNode();
            final List<JsonNode> candidate = Stream.concat(
                            Stream.concat(
                                    Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(index),
                                    Stream.of(value)
                            ),
                            Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(aSmallNonNegativeInt()))
                    .collect(toList());
            assertThat(JsonNodeSelectors.anElement(index).getValue(candidate), equalTo(value));
        }

        @Test
        void getValueOfMismatch() {
            final int size = aSmallNonNegativeInt();
            final List<JsonNode> candidate = Stream.generate(JsonNodeTestingFactories::aJsonNode).limit(size).collect(toList());
            assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anElement(size).getValue(candidate));
        }

        @Test
        void rejectsNegativeIndex() {
            final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> anElement(-1));
            assertThat(illegalArgumentException.getMessage(), equalTo("-1"));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anElement(aSmallNonNegativeInt()).matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anElement(aSmallNonNegativeInt()).getValue(null));
        }

        @Test
        void toStringWorks() {
            final int index = aSmallNonNegativeInt();
            assertThat(anElement(index).toString(), equalTo("an element at index [" + index + "]"));
        }
    }

    @Nested
    final class AnArrayNodeWithElement {
        @ParameterizedTest
        @ArgumentsSource(NodeTestCase.ArrayElementByIndexArgumentsProvider.class)
        void matches(final JsonNode candidateMatch, final int index, final boolean expectedMatch) {
            assertThat(JsonNodeSelectors.anArrayNodeWithElement(index).matches(candidateMatch), equalTo(expectedMatch));
        }

        @ParameterizedTest
        @ArgumentsSource(NodeTestCase.ArrayElementByIndexArgumentsProvider.class)
        void getValue(final JsonNode candidateMatch, final int index, final boolean expectedMatch, final JsonNode expectedValue) {
            if (expectedMatch) {
                assertThat(JsonNodeSelectors.anArrayNodeWithElement(index).getValue(candidateMatch), equalTo(expectedValue));
            } else {
                assertThrows(IllegalArgumentException.class, () -> JsonNodeSelectors.anArrayNodeWithElement(index).getValue(candidateMatch));
            }
        }

        @Test
        void rejectsNegativeIndex() {
            final IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> anArrayNodeWithElement(-1));
            assertThat(illegalArgumentException.getMessage(), equalTo("-1"));
        }

        @Test
        void matchRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNodeWithElement(aSmallNonNegativeInt()).matches(null));
        }

        @Test
        void getValueRejectsNullCandidate() {
            assertThrows(NullPointerException.class, () -> JsonNodeSelectors.anArrayNodeWithElement(aSmallNonNegativeInt()).getValue(null));
        }

        @Test
        void toStringWorks() {
            final int index = aSmallNonNegativeInt();
            assertThat(anArrayNodeWithElement(index).toString(), equalTo("an array, with an element at index [" + index + "]"));
        }
    }

}
