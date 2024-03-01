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
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import org.apache.commons.io.output.BrokenWriter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.TestingFactories.*;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestingFactories.*;
import static argo.jdom.JsonNumberNodeTestingFactories.aNumberNode;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonWriterTest {

    private static Stream<JsonWriter> jsonWritersAndShimmedJsonGenerators() {
        return Stream.of(
                new CompactJsonWriter(),
                new PrettyJsonWriter(),
                new JsonGeneratorJsonWriterAdapter(new JsonGenerator().style(COMPACT)),
                new JsonGeneratorJsonWriterAdapter(new JsonGenerator()),
                new JsonGeneratorJsonWriterAdapter(new JsonGenerator().style(PRETTY))
        );
    }

    static final class JsonGeneratorJsonWriterShimArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.concat(
                    jsonWritersAndShimmedJsonGenerators().map(WriterJsonGeneratorJsonWriterTestCase::new),
                    Stream.of(
                            new StringJsonGeneratorJsonWriterTestCase(new JsonGenerator()),
                            new StringJsonGeneratorJsonWriterTestCase(new JsonGenerator().style(PRETTY))
                    )).map(Arguments::arguments);
        }

    }

    static final class JsonWriterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return jsonWritersAndShimmedJsonGenerators().map(Arguments::arguments);
        }
    }

    static final class JsonNodeJsonWriterCartesianProductArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return jsonWritersAndShimmedJsonGenerators().flatMap(jsonWriter ->
                    Stream.of(
                            nullNode(),
                            trueNode(),
                            falseNode(),
                            aNumberNode(),
                            string(aStringOfLength(aSmallNonNegativeInt())),
                            string(aStringOfLength(2048)),
                            anObjectNode(),
                            anArrayNode()
                    ).map(jsonNode -> Arguments.arguments(jsonWriter, jsonNode))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(JsonNodeJsonWriterCartesianProductArgumentsProvider.class)
    void propagatesIOExceptionFromTargetWriter(final JsonWriter jsonWriter, final JsonNode jsonNode) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new BrokenWriter(() -> ioException), jsonNode));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void writesTwoStringsToReuseWriteBuffer(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        final JsonNode jsonNode = array(aStringNode(), aStringNode());
        final String jsonText = jsonGeneratorJsonWriterTestCase.write(jsonNode);
        assertThat(new JdomParser().parse(jsonText), equalTo(jsonNode));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void writesTwoWriteableJsonStringsToReuseWriteBuffer(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) throws Exception {
        final String firstString = aString();
        final String secondString = aString();
        final String jsonText = jsonGeneratorJsonWriterTestCase.write((WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement((WriteableJsonString) writer -> writer.write(firstString));
            arrayWriter.writeElement((WriteableJsonString) writer -> writer.write(secondString));
        });
        assertThat(new JdomParser().parse(jsonText), equalTo(array(string(firstString), string(secondString))));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void propagatesIOExceptionWritingWriteableJsonArrayFromTargetWriter(final JsonWriter jsonWriter) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new EventuallyBrokenWriter(ioException, 10), (WriteableJsonArray) arrayWriter -> arrayWriter.writeElement(string("a string with more than 10 characters"))));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void propagatesIOExceptionWritingWriteableJsonObjectFromTargetWriter(final JsonWriter jsonWriter) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new EventuallyBrokenWriter(ioException, 10), (WriteableJsonObject) objectWriter -> objectWriter.writeField(string("a string with more than 10 characters"), aJsonNode())));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void propagatesIOExceptionWritingWriteableJsonStringFromTargetWriter(final JsonWriter jsonWriter) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new EventuallyBrokenWriter(ioException, 10), (WriteableJsonString) writer -> writer.write("a string with more than 10 characters")));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void propagatesIOExceptionWritingWriteableJsonNumberFromTargetWriter(final JsonWriter jsonWriter) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new EventuallyBrokenWriter(ioException, 10), (WriteableJsonNumber) writer -> writer.write("12345678901234567890")));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void rejectsIncompleteNumber(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) {
        assertThrows(IllegalStateException.class, () -> jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) numberWriter -> numberWriter.write("1.")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonGeneratorJsonWriterShimArgumentsProvider.class)
    void correctExceptionIsPropagatedThroughJsonWriter(final JsonGeneratorJsonWriterTestCase jsonGeneratorJsonWriterTestCase) {
        assertThrows(IndexOutOfBoundsException.class, () -> jsonGeneratorJsonWriterTestCase.write((WriteableJsonNumber) numberWriter -> numberWriter.write(new char[] {'1', '.'}, 1, 3)));
    }

}
