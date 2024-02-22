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

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import org.apache.commons.io.output.BrokenWriter;
import org.apache.commons.io.output.NullWriter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

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

    static final class JsonWriterArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new CompactJsonWriter(),
                    new PrettyJsonWriter()
            ).map(Arguments::arguments);
        }
    }

    static final class JsonNodeJsonWriterCartesianProductArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new CompactJsonWriter(),
                    new PrettyJsonWriter()
            ).flatMap(jsonWriter ->
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
    @ArgumentsSource(JsonWriterTest.JsonNodeJsonWriterCartesianProductArgumentsProvider.class)
    void propagatesIOExceptionFromTargetWriter(final JsonWriter jsonWriter, final JsonNode jsonNode) {
        final IOException ioException = new IOException();
        final IOException actualIoException = assertThrows(IOException.class, () -> jsonWriter.write(new BrokenWriter(() -> ioException), jsonNode));
        assertThat(actualIoException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void writesTwoStringsToReuseWriteBuffer(final JsonWriter jsonWriter) throws Exception {
        final StringWriter stringWriter = new StringWriter();
        final JsonNode jsonNode = array(aStringNode(), aStringNode());
        jsonWriter.write(stringWriter, jsonNode);
        assertThat(new JdomParser().parse(stringWriter.toString()), equalTo(jsonNode));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void writesTwoWriteableJsonStringsToReuseWriteBuffer(final JsonWriter jsonWriter) throws Exception {
        final StringWriter stringWriter = new StringWriter();
        final String firstString = aString();
        final String secondString = aString();
        jsonWriter.write(stringWriter, (WriteableJsonArray) arrayWriter -> {
            arrayWriter.writeElement((WriteableJsonString) writer -> writer.write(firstString));
            arrayWriter.writeElement((WriteableJsonString) writer -> writer.write(secondString));
        });
        assertThat(new JdomParser().parse(stringWriter.toString()), equalTo(array(string(firstString), string(secondString))));
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
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void rejectsIncompleteNumber(final JsonWriter jsonWriter) {
        assertThrows(IllegalStateException.class, () -> jsonWriter.write(NullWriter.INSTANCE, (WriteableJsonNumber) numberWriter -> numberWriter.write("1.")));
    }

    @ParameterizedTest
    @ArgumentsSource(JsonWriterTest.JsonWriterArgumentsProvider.class)
    void correctExceptionIsPropagatedThroughJsonWriter(final JsonWriter jsonWriter) {
        assertThrows(IndexOutOfBoundsException.class, () -> jsonWriter.write(NullWriter.INSTANCE, (WriteableJsonNumber) numberWriter -> numberWriter.write(new char[] {'1', '.'}, 1, 3)));
    }

}
