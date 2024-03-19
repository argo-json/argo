/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import argo.jdom.JsonStringNodeTestingFactories;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BrokenReader;
import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static argo.ElementTrackingStajParserMatcher.generatesElements;
import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static argo.JsonReaderFactory.readerOf;
import static argo.JsonStreamElement.number;
import static argo.JsonStreamElement.string;
import static argo.JsonStreamElement.*;
import static argo.RoundTrippingStajParserMatcher.parsesTo;
import static argo.StajParserTest.TestCase.testCase;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestingFactories.aJsonNode;
import static argo.jdom.JsonNumberNodeTestingFactories.aNumberNode;
import static argo.jdom.JsonStringNodeTestingFactories.aNonEmptyString;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNode;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class StajParserTest {

    private static Stream<StajParserJsonParserShim> shims() {
        return Stream.of(
                new StajParserJsonParserShim.Staj(),
                new StajParserJsonParserShim.Json(new JsonParser())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesFromReader(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(new StringReader("null")), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesFromString(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse("null"), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void objectOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(object())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_OBJECT, NonTextJsonStreamElement.END_OBJECT, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void numberOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode numberNode = aNumberNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(numberNode)), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, number(new StringReader(numberNode.getText())), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void stringOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode stringNode = aStringNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(stringNode)), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader(stringNode.getText())), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void nullOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(nullNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void trueOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(trueNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.TRUE, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void falseOnlyDocumentHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(falseNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.FALSE, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithChildHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(array()))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithChildrenHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(array(), object()))), generatesElements(
                NonTextJsonStreamElement.START_DOCUMENT,
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.END_ARRAY,
                NonTextJsonStreamElement.START_OBJECT,
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_ARRAY,
                NonTextJsonStreamElement.END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithNullHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(nullNode()))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithNullsHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(nullNode(), nullNode()))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithTrueHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(trueNode()))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.TRUE, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithFalseHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array(falseNode()))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.FALSE, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithATextNodeHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode aStringNode = aStringNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(array(aStringNode))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, string(new StringReader(aStringNode.getText())), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void arrayWithANumberNodeHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode aNumberNode = aNumberNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(array(aNumberNode))), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, number(new StringReader(aNumberNode.getText())), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void objectWithFieldHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonStringNode aFieldName = JsonStringNodeTestingFactories.aStringNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(object(field(aFieldName, array())))), generatesElements(
                NonTextJsonStreamElement.START_DOCUMENT,
                NonTextJsonStreamElement.START_OBJECT,
                startField(new StringReader(aFieldName.getText())),
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.END_ARRAY,
                NonTextJsonStreamElement.END_FIELD,
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void objectWithFieldsHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonStringNode aFieldName = aStringNode();
        final JsonStringNode anotherFieldName = aStringNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(object(field(aFieldName, array()), field(anotherFieldName, object())))), generatesElements(
                NonTextJsonStreamElement.START_DOCUMENT,
                NonTextJsonStreamElement.START_OBJECT,
                startField(new StringReader(aFieldName.getText())),
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.END_ARRAY,
                NonTextJsonStreamElement.END_FIELD,
                startField(new StringReader(anotherFieldName.getText())),
                NonTextJsonStreamElement.START_OBJECT,
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_FIELD,
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void aRandomJsonNodeHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode aJsonNode = aJsonNode();
        assertThat(stajParserJsonParserShim.parse(readerOf(aJsonNode)), parsesTo(aJsonNode));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void aRandomJsonNodeFromStringHasCorrectElements(final StajParserJsonParserShim stajParserJsonParserShim) {
        final JsonNode jsonNode = aJsonNode();
        assertThat(stajParserJsonParserShim.parse(new JsonGenerator().style(PRETTY).generate(jsonNode)), parsesTo(jsonNode));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void nextWorksWithoutCallingHasNext(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse(readerOf(array())).next(), equalTo(NonTextJsonStreamElement.START_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void toleratesFieldNameTextNotBeingRead(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(readerOf(object(field(aNonEmptyString(), nullNode()))));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void toleratesStringTextNotBeingRead(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(readerOf(array(JsonNodeFactories.string(aNonEmptyString()))));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void toleratesNumberTextNotBeingRead(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(readerOf(array(aNumberNode())));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings({"PMD.UseTryWithResources", "PMD.CloseResource"})
    void propagatesIoExceptionReadingNumber(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        try {
            assertThat(reader.read(), not(equalTo(-1)));
            final IOException actualException = assertThrows(IOException.class, reader::read);
            assertThat(actualException, sameInstance(ioException));
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void propagatesIoExceptionSkippingNumberByGettingNextElement(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void propagatesIoExceptionSkippingNumberByQueryingNextElement(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::hasNext);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canCloseANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        stajParser.next().reader().close();
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderCanCallCloseAgain(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        reader.close();
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderMarkThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderMarkSupportedReturnsFalse(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThat(reader.markSupported(), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderReadingACharacterThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::read);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderReadingToABufferThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3]));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderReadingToABufferWithOffsetAndLengthThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3], 0, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderReadingToACharBufferThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(CharBuffer.allocate(3)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderReadyThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::ready);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderResetThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::reset);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingANumberReaderSkipThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToMarkANumberReaderThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, () -> reader.mark(0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void aNumberReaderDoesNotSupportMark(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.markSupported(), equalTo(false));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadNumberCharacterByCharacter(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.read(), equalTo((int) '1'));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadNumberToABuffer(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer), equalTo(2));
            assertThat(buffer, equalTo(new char[]{'1', '2', 0}));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadNumberToABufferWithOffsetAndLength(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer, 1, 1), equalTo(1));
            assertThat(buffer, equalTo(new char[]{0, '1', 0}));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadNumberToACharBuffer(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final CharBuffer charBuffer = CharBuffer.allocate(3);
            assertThat(reader.read(charBuffer), equalTo(2));
            assertThat(charBuffer.array(), equalTo(new char[]{'1', '2', 0}));
            assertThat(reader.read(charBuffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void numberReaderReadyReturnsFalseToBeOnTheSafeSide(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.ready(), equalTo(false));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToResetANumberReaderThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, reader::reset);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipASubsetCharactersInANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(1), equalTo(1L));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipPastAllCharactersInANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(3), equalTo(2L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipZeroCharactersInANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(0), equalTo(0L));
            assertThat(reader.read(), equalTo((int) '1'));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsNegativeSkipValueInANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipAfterEndOfStreamInANumberReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.read(), equalTo(-1));
            assertThat(reader.skip(1), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings({"PMD.UseTryWithResources", "PMD.CloseResource"})
    void propagatesIoExceptionReadingString(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        try {
            assertThat(reader.read(), not(equalTo(-1)));
            final IOException actualException = assertThrows(IOException.class, reader::read);
            assertThat(actualException, sameInstance(ioException));
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void propagatesIoExceptionSkippingStringByGettingNextElement(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void propagatesIoExceptionSkippingStringByQueryingNextElement(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::hasNext);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canCloseAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        stajParser.next().reader().close();
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderCanCallCloseAgain(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        reader.close();
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderMarkThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderMarkSupportedReturnsFalse(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThat(reader.markSupported(), equalTo(false));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderReadingACharacterThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::read);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderReadingToABufferThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3]));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderReadingToABufferWithOffsetAndLengthThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3], 0, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderReadingToACharBufferThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(CharBuffer.allocate(3)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderReadyThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::ready);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderResetThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::reset);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    @SuppressWarnings("PMD.CloseResource")
    void afterClosingAStringReaderSkipThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToMarkAStringReaderThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, () -> reader.mark(0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void aStringReaderDoesNotSupportMark(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.markSupported(), equalTo(false));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadStringCharacterByCharacter(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.read(), equalTo((int) 'F'));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadStringToABuffer(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer), equalTo(2));
            assertThat(buffer, equalTo(new char[]{'F', 'o', 0}));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadStringToABufferWithOffsetAndLength(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer, 1, 1), equalTo(1));
            assertThat(buffer, equalTo(new char[]{0, 'F', 0}));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canReadStringToACharBuffer(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final CharBuffer charBuffer = CharBuffer.allocate(3);
            assertThat(reader.read(charBuffer), equalTo(2));
            assertThat(charBuffer.array(), equalTo(new char[]{'F', 'o', 0}));
            assertThat(reader.read(charBuffer), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void stringReaderReadyReturnsFalseToBeOnTheSafeSide(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.ready(), equalTo(false));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void attemptingToResetAStringReaderThrowsIOException(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, reader::reset);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipASubsetCharactersInAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(1), equalTo(1L));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipPastAllCharactersInAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(3), equalTo(2L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipZeroCharactersInAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(0), equalTo(0L));
            assertThat(reader.read(), equalTo((int) 'F'));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsNegativeSkipValueInAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canSkipAfterEndOfStreamInAStringReader(final StajParserJsonParserShim stajParserJsonParserShim) throws IOException {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.read(), equalTo(-1));
            assertThat(reader.skip(1), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @TestFactory
    Stream<DynamicTest> parsesValidStringWithEscapedChars() {
        return Stream.of(
                testCase("\"", 0x22),
                testCase("\\", 0x5c),
                testCase("/", 0x2f),
                testCase("b", 0x8),
                testCase("f", 0xc),
                testCase("n", 0xa),
                testCase("r", 0xd),
                testCase("t", 0x9)
        ).flatMap(testCase -> shims().map(shim -> dynamicTest("\\" + testCase.input + " should be parsed as character " + testCase.expected + " using " + shim, () ->
                assertThat(shim.parse("\"\\" + testCase.input + "\""), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader(String.valueOf((char) testCase.expected))), NonTextJsonStreamElement.END_DOCUMENT)))));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesUnpairedUtf16Surrogate(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse("\"\\uDEAD\""), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader(String.valueOf('\uDEAD'))), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesValidStringWithEscapedUnicodeChars(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(stajParserJsonParserShim.parse("\"\\uF001\""), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader("\uF001")), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithIncompleteEscapedUnicodeChars(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\uF0\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected 4 hexadecimal digits, but got [F, 0, \"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithPrematureEndOfStreamFollowingBackslash(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithInvalidCharacterFollowingBackslash(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\a\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but got [a]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithInvalidUnprintableCharacterFollowingBackslash(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\\u0000\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but got [\\u0000]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithNonHexadecimalEscapedUnicodeChars(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\uF00L\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Unable to parse escaped character [F, 0, 0, L] as a hexadecimal number"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsStringWithNonHexadecimalNonPrintingEscapedUnicodeChars(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\uF00\u007f\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Unable to parse escaped character [F, 0, 0, \\u007F] as a hexadecimal number"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void callingNextWhenHasNextReturnsFalseThrowsAnException(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(readerOf(array()));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
        assertThrows(NoSuchElementException.class, stajParser::next);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void handlesIoExceptionDuringParsing(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new BrokenReader(ioException));
        stajParser.next();
        final JsonStreamException jsonStreamException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(jsonStreamException.getCause(), equalTo(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void handlesIoExceptionSkippingElement(final StajParserJsonParserShim stajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new SequenceReader(
                new StringReader("["),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException jsonStreamException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(jsonStreamException.getCause(), sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void handlesRuntimeExceptionDuringParsing(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse(new Reader() { // TODO commons-io BrokenReader ought to be made to throw RuntimeExceptions too.
            public int read(char[] chars, int offset, int length) {
                throw new MyTestRuntimeException();
            }

            public void close() {
            }
        });
        stajParser.next();
        assertThrows(MyTestRuntimeException.class, stajParser::next);
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void removeThrowsUnsupportedOperationException(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("null");
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, stajParser::remove);
        assertThat(exception.getMessage(), equalTo("JsonParser cannot remove elements from JSON it has parsed"));
    }

    @TestFactory
    Stream<DynamicTest> parsesValidNumber() {
        return Stream.of(
                "-0",

                "-0.0",
                "-0.1",
                "-0.2",
                "-0.3",
                "-0.4",
                "-0.5",
                "-0.6",
                "-0.7",
                "-0.8",
                "-0.9",

                "-0.00",
                "-0.11",
                "-0.22",
                "-0.33",
                "-0.44",
                "-0.55",
                "-0.66",
                "-0.77",
                "-0.88",
                "-0.99",

                "-0.0e+0",
                "-0.1e+1",
                "-0.2e+2",
                "-0.3e+3",
                "-0.4e+4",
                "-0.5e+5",
                "-0.6e+6",
                "-0.7e+7",
                "-0.8e+8",
                "-0.9e+9",
                "-0.0E+0",
                "-0.1E+1",
                "-0.2E+2",
                "-0.3E+3",
                "-0.4E+4",
                "-0.5E+5",
                "-0.6E+6",
                "-0.7E+7",
                "-0.8E+8",
                "-0.9E+9",
                "-0.0e+00",
                "-0.1e+11",
                "-0.2e+22",
                "-0.3e+33",
                "-0.4e+44",
                "-0.5e+55",
                "-0.6e+66",
                "-0.7e+77",
                "-0.8e+88",
                "-0.9e+99",
                "-0.0E+0",
                "-0.1E+1",
                "-0.2E+2",
                "-0.3E+3",
                "-0.4E+4",
                "-0.5E+5",
                "-0.6E+6",
                "-0.7E+7",
                "-0.8E+8",
                "-0.9E+9",

                "-0.0e-0",
                "-0.1e-1",
                "-0.2e-2",
                "-0.3e-3",
                "-0.4e-4",
                "-0.5e-5",
                "-0.6e-6",
                "-0.7e-7",
                "-0.8e-8",
                "-0.9e-9",
                "-0.0e-00",
                "-0.1e-11",
                "-0.2e-22",
                "-0.3e-33",
                "-0.4e-44",
                "-0.5e-55",
                "-0.6e-66",
                "-0.7e-77",
                "-0.8e-88",
                "-0.9e-99",
                "-0.0E-0",
                "-0.1E-1",
                "-0.2E-2",
                "-0.3E-3",
                "-0.4E-4",
                "-0.5E-5",
                "-0.6E-6",
                "-0.7E-7",
                "-0.8E-8",
                "-0.9E-9",
                "-0.0E-00",
                "-0.1E-11",
                "-0.2E-22",
                "-0.3E-33",
                "-0.4E-44",
                "-0.5E-55",
                "-0.6E-66",
                "-0.7E-77",
                "-0.8E-88",
                "-0.9E-99",

                "-0.0e0",
                "-0.1e1",
                "-0.2e2",
                "-0.3e3",
                "-0.4e4",
                "-0.5e5",
                "-0.6e6",
                "-0.7e7",
                "-0.8e8",
                "-0.9e9",
                "-0.0e00",
                "-0.1e11",
                "-0.2e22",
                "-0.3e33",
                "-0.4e44",
                "-0.5e55",
                "-0.6e66",
                "-0.7e77",
                "-0.8e88",
                "-0.9e99",
                "-0.0E0",
                "-0.1E1",
                "-0.2E2",
                "-0.3E3",
                "-0.4E4",
                "-0.5E5",
                "-0.6E6",
                "-0.7E7",
                "-0.8E8",
                "-0.9E9",
                "-0.0E00",
                "-0.1E11",
                "-0.2E22",
                "-0.3E33",
                "-0.4E44",
                "-0.5E55",
                "-0.6E66",
                "-0.7E77",
                "-0.8E88",
                "-0.9E99",

                "-1.0",
                "-1.1",
                "-1.2",
                "-1.3",
                "-1.4",
                "-1.5",
                "-1.6",
                "-1.7",
                "-1.8",
                "-1.9",

                "-1.00",
                "-1.11",
                "-1.22",
                "-1.33",
                "-1.44",
                "-1.55",
                "-1.66",
                "-1.77",
                "-1.88",
                "-1.99",

                "-1.0e+0",
                "-1.1e+1",
                "-1.2e+2",
                "-1.3e+3",
                "-1.4e+4",
                "-1.5e+5",
                "-1.6e+6",
                "-1.7e+7",
                "-1.8e+8",
                "-1.9e+9",
                "-1.0E+0",
                "-1.1E+1",
                "-1.2E+2",
                "-1.3E+3",
                "-1.4E+4",
                "-1.5E+5",
                "-1.6E+6",
                "-1.7E+7",
                "-1.8E+8",
                "-1.9E+9",
                "-1.0e+00",
                "-1.1e+11",
                "-1.2e+22",
                "-1.3e+33",
                "-1.4e+44",
                "-1.5e+55",
                "-1.6e+66",
                "-1.7e+77",
                "-1.8e+88",
                "-1.9e+99",
                "-1.0E+0",
                "-1.1E+1",
                "-1.2E+2",
                "-1.3E+3",
                "-1.4E+4",
                "-1.5E+5",
                "-1.6E+6",
                "-1.7E+7",
                "-1.8E+8",
                "-1.9E+9",

                "-1.0e-0",
                "-1.1e-1",
                "-1.2e-2",
                "-1.3e-3",
                "-1.4e-4",
                "-1.5e-5",
                "-1.6e-6",
                "-1.7e-7",
                "-1.8e-8",
                "-1.9e-9",
                "-1.0e-00",
                "-1.1e-11",
                "-1.2e-22",
                "-1.3e-33",
                "-1.4e-44",
                "-1.5e-55",
                "-1.6e-66",
                "-1.7e-77",
                "-1.8e-88",
                "-1.9e-99",
                "-1.0E-0",
                "-1.1E-1",
                "-1.2E-2",
                "-1.3E-3",
                "-1.4E-4",
                "-1.5E-5",
                "-1.6E-6",
                "-1.7E-7",
                "-1.8E-8",
                "-1.9E-9",
                "-1.0E-00",
                "-1.1E-11",
                "-1.2E-22",
                "-1.3E-33",
                "-1.4E-44",
                "-1.5E-55",
                "-1.6E-66",
                "-1.7E-77",
                "-1.8E-88",
                "-1.9E-99",

                "-1.0e0",
                "-1.1e1",
                "-1.2e2",
                "-1.3e3",
                "-1.4e4",
                "-1.5e5",
                "-1.6e6",
                "-1.7e7",
                "-1.8e8",
                "-1.9e9",
                "-1.0e00",
                "-1.1e11",
                "-1.2e22",
                "-1.3e33",
                "-1.4e44",
                "-1.5e55",
                "-1.6e66",
                "-1.7e77",
                "-1.8e88",
                "-1.9e99",
                "-1.0E0",
                "-1.1E1",
                "-1.2E2",
                "-1.3E3",
                "-1.4E4",
                "-1.5E5",
                "-1.6E6",
                "-1.7E7",
                "-1.8E8",
                "-1.9E9",
                "-1.0E00",
                "-1.1E11",
                "-1.2E22",
                "-1.3E33",
                "-1.4E44",
                "-1.5E55",
                "-1.6E66",
                "-1.7E77",
                "-1.8E88",
                "-1.9E99",

                "-0",
                "-1",
                "-2",
                "-3",
                "-4",
                "-5",
                "-6",
                "-7",
                "-8",
                "-9",

                "-11",
                "-22",
                "-33",
                "-44",
                "-55",
                "-66",
                "-77",
                "-88",
                "-99",

                "-0e+0",
                "-1e+1",
                "-2e+2",
                "-3e+3",
                "-4e+4",
                "-5e+5",
                "-6e+6",
                "-7e+7",
                "-8e+8",
                "-9e+9",
                "-0E+0",
                "-1E+1",
                "-2E+2",
                "-3E+3",
                "-4E+4",
                "-5E+5",
                "-6E+6",
                "-7E+7",
                "-8E+8",
                "-9E+9",
                "-0e+00",
                "-1e+11",
                "-2e+22",
                "-3e+33",
                "-4e+44",
                "-5e+55",
                "-6e+66",
                "-7e+77",
                "-8e+88",
                "-9e+99",
                "-0E+0",
                "-1E+1",
                "-2E+2",
                "-3E+3",
                "-4E+4",
                "-5E+5",
                "-6E+6",
                "-7E+7",
                "-8E+8",
                "-9E+9",

                "-0e-0",
                "-1e-1",
                "-2e-2",
                "-3e-3",
                "-4e-4",
                "-5e-5",
                "-6e-6",
                "-7e-7",
                "-8e-8",
                "-9e-9",
                "-0e-00",
                "-1e-11",
                "-2e-22",
                "-3e-33",
                "-4e-44",
                "-5e-55",
                "-6e-66",
                "-7e-77",
                "-8e-88",
                "-9e-99",
                "-0E-0",
                "-1E-1",
                "-2E-2",
                "-3E-3",
                "-4E-4",
                "-5E-5",
                "-6E-6",
                "-7E-7",
                "-8E-8",
                "-9E-9",
                "-0E-00",
                "-1E-11",
                "-2E-22",
                "-3E-33",
                "-4E-44",
                "-5E-55",
                "-6E-66",
                "-7E-77",
                "-8E-88",
                "-9E-99",

                "-0e0",
                "-1e1",
                "-2e2",
                "-3e3",
                "-4e4",
                "-5e5",
                "-6e6",
                "-7e7",
                "-8e8",
                "-9e9",
                "-0e00",
                "-1e11",
                "-2e22",
                "-3e33",
                "-4e44",
                "-5e55",
                "-6e66",
                "-7e77",
                "-8e88",
                "-9e99",
                "-0E0",
                "-1E1",
                "-2E2",
                "-3E3",
                "-4E4",
                "-5E5",
                "-6E6",
                "-7E7",
                "-8E8",
                "-9E9",
                "-0E00",
                "-1E11",
                "-2E22",
                "-3E33",
                "-4E44",
                "-5E55",
                "-6E66",
                "-7E77",
                "-8E88",
                "-9E99",

                "0",

                "0.0",
                "0.1",
                "0.2",
                "0.3",
                "0.4",
                "0.5",
                "0.6",
                "0.7",
                "0.8",
                "0.9",

                "0.00",
                "0.11",
                "0.22",
                "0.33",
                "0.44",
                "0.55",
                "0.66",
                "0.77",
                "0.88",
                "0.99",

                "0.0e+0",
                "0.1e+1",
                "0.2e+2",
                "0.3e+3",
                "0.4e+4",
                "0.5e+5",
                "0.6e+6",
                "0.7e+7",
                "0.8e+8",
                "0.9e+9",
                "0.0E+0",
                "0.1E+1",
                "0.2E+2",
                "0.3E+3",
                "0.4E+4",
                "0.5E+5",
                "0.6E+6",
                "0.7E+7",
                "0.8E+8",
                "0.9E+9",
                "0.0e+00",
                "0.1e+11",
                "0.2e+22",
                "0.3e+33",
                "0.4e+44",
                "0.5e+55",
                "0.6e+66",
                "0.7e+77",
                "0.8e+88",
                "0.9e+99",
                "0.0E+0",
                "0.1E+1",
                "0.2E+2",
                "0.3E+3",
                "0.4E+4",
                "0.5E+5",
                "0.6E+6",
                "0.7E+7",
                "0.8E+8",
                "0.9E+9",

                "0.0e-0",
                "0.1e-1",
                "0.2e-2",
                "0.3e-3",
                "0.4e-4",
                "0.5e-5",
                "0.6e-6",
                "0.7e-7",
                "0.8e-8",
                "0.9e-9",
                "0.0e-00",
                "0.1e-11",
                "0.2e-22",
                "0.3e-33",
                "0.4e-44",
                "0.5e-55",
                "0.6e-66",
                "0.7e-77",
                "0.8e-88",
                "0.9e-99",
                "0.0E-0",
                "0.1E-1",
                "0.2E-2",
                "0.3E-3",
                "0.4E-4",
                "0.5E-5",
                "0.6E-6",
                "0.7E-7",
                "0.8E-8",
                "0.9E-9",
                "0.0E-00",
                "0.1E-11",
                "0.2E-22",
                "0.3E-33",
                "0.4E-44",
                "0.5E-55",
                "0.6E-66",
                "0.7E-77",
                "0.8E-88",
                "0.9E-99",

                "0.0e0",
                "0.1e1",
                "0.2e2",
                "0.3e3",
                "0.4e4",
                "0.5e5",
                "0.6e6",
                "0.7e7",
                "0.8e8",
                "0.9e9",
                "0.0e00",
                "0.1e11",
                "0.2e22",
                "0.3e33",
                "0.4e44",
                "0.5e55",
                "0.6e66",
                "0.7e77",
                "0.8e88",
                "0.9e99",
                "0.0E0",
                "0.1E1",
                "0.2E2",
                "0.3E3",
                "0.4E4",
                "0.5E5",
                "0.6E6",
                "0.7E7",
                "0.8E8",
                "0.9E9",
                "0.0E00",
                "0.1E11",
                "0.2E22",
                "0.3E33",
                "0.4E44",
                "0.5E55",
                "0.6E66",
                "0.7E77",
                "0.8E88",
                "0.9E99",

                "1.0",
                "1.1",
                "1.2",
                "1.3",
                "1.4",
                "1.5",
                "1.6",
                "1.7",
                "1.8",
                "1.9",

                "1.00",
                "1.11",
                "1.22",
                "1.33",
                "1.44",
                "1.55",
                "1.66",
                "1.77",
                "1.88",
                "1.99",

                "1.0e+0",
                "1.1e+1",
                "1.2e+2",
                "1.3e+3",
                "1.4e+4",
                "1.5e+5",
                "1.6e+6",
                "1.7e+7",
                "1.8e+8",
                "1.9e+9",
                "1.0E+0",
                "1.1E+1",
                "1.2E+2",
                "1.3E+3",
                "1.4E+4",
                "1.5E+5",
                "1.6E+6",
                "1.7E+7",
                "1.8E+8",
                "1.9E+9",
                "1.0e+00",
                "1.1e+11",
                "1.2e+22",
                "1.3e+33",
                "1.4e+44",
                "1.5e+55",
                "1.6e+66",
                "1.7e+77",
                "1.8e+88",
                "1.9e+99",
                "1.0E+0",
                "1.1E+1",
                "1.2E+2",
                "1.3E+3",
                "1.4E+4",
                "1.5E+5",
                "1.6E+6",
                "1.7E+7",
                "1.8E+8",
                "1.9E+9",

                "1.0e-0",
                "1.1e-1",
                "1.2e-2",
                "1.3e-3",
                "1.4e-4",
                "1.5e-5",
                "1.6e-6",
                "1.7e-7",
                "1.8e-8",
                "1.9e-9",
                "1.0e-00",
                "1.1e-11",
                "1.2e-22",
                "1.3e-33",
                "1.4e-44",
                "1.5e-55",
                "1.6e-66",
                "1.7e-77",
                "1.8e-88",
                "1.9e-99",
                "1.0E-0",
                "1.1E-1",
                "1.2E-2",
                "1.3E-3",
                "1.4E-4",
                "1.5E-5",
                "1.6E-6",
                "1.7E-7",
                "1.8E-8",
                "1.9E-9",
                "1.0E-00",
                "1.1E-11",
                "1.2E-22",
                "1.3E-33",
                "1.4E-44",
                "1.5E-55",
                "1.6E-66",
                "1.7E-77",
                "1.8E-88",
                "1.9E-99",

                "1.0e0",
                "1.1e1",
                "1.2e2",
                "1.3e3",
                "1.4e4",
                "1.5e5",
                "1.6e6",
                "1.7e7",
                "1.8e8",
                "1.9e9",
                "1.0e00",
                "1.1e11",
                "1.2e22",
                "1.3e33",
                "1.4e44",
                "1.5e55",
                "1.6e66",
                "1.7e77",
                "1.8e88",
                "1.9e99",
                "1.0E0",
                "1.1E1",
                "1.2E2",
                "1.3E3",
                "1.4E4",
                "1.5E5",
                "1.6E6",
                "1.7E7",
                "1.8E8",
                "1.9E9",
                "1.0E00",
                "1.1E11",
                "1.2E22",
                "1.3E33",
                "1.4E44",
                "1.5E55",
                "1.6E66",
                "1.7E77",
                "1.8E88",
                "1.9E99",

                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",

                "11",
                "22",
                "33",
                "44",
                "55",
                "66",
                "77",
                "88",
                "99",

                "0e+0",
                "1e+1",
                "2e+2",
                "3e+3",
                "4e+4",
                "5e+5",
                "6e+6",
                "7e+7",
                "8e+8",
                "9e+9",
                "0E+0",
                "1E+1",
                "2E+2",
                "3E+3",
                "4E+4",
                "5E+5",
                "6E+6",
                "7E+7",
                "8E+8",
                "9E+9",
                "0e+00",
                "1e+11",
                "2e+22",
                "3e+33",
                "4e+44",
                "5e+55",
                "6e+66",
                "7e+77",
                "8e+88",
                "9e+99",
                "0E+0",
                "1E+1",
                "2E+2",
                "3E+3",
                "4E+4",
                "5E+5",
                "6E+6",
                "7E+7",
                "8E+8",
                "9E+9",

                "0e-0",
                "1e-1",
                "2e-2",
                "3e-3",
                "4e-4",
                "5e-5",
                "6e-6",
                "7e-7",
                "8e-8",
                "9e-9",
                "0e-00",
                "1e-11",
                "2e-22",
                "3e-33",
                "4e-44",
                "5e-55",
                "6e-66",
                "7e-77",
                "8e-88",
                "9e-99",
                "0E-0",
                "1E-1",
                "2E-2",
                "3E-3",
                "4E-4",
                "5E-5",
                "6E-6",
                "7E-7",
                "8E-8",
                "9E-9",
                "0E-00",
                "1E-11",
                "2E-22",
                "3E-33",
                "4E-44",
                "5E-55",
                "6E-66",
                "7E-77",
                "8E-88",
                "9E-99",

                "0e0",
                "1e1",
                "2e2",
                "3e3",
                "4e4",
                "5e5",
                "6e6",
                "7e7",
                "8e8",
                "9e9",
                "0e00",
                "1e11",
                "2e22",
                "3e33",
                "4e44",
                "5e55",
                "6e66",
                "7e77",
                "8e88",
                "9e99",
                "0E0",
                "1E1",
                "2E2",
                "3E3",
                "4E4",
                "5E5",
                "6E6",
                "7E7",
                "8E8",
                "9E9",
                "0E00",
                "1E11",
                "2E22",
                "3E33",
                "4E44",
                "5E55",
                "6E66",
                "7E77",
                "8E88",
                "9E99"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            assertThat(stajParser, generatesElements(NonTextJsonStreamElement.START_DOCUMENT, number(new StringReader(numberString)), NonTextJsonStreamElement.END_DOCUMENT));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsIncompleteNumberWhenDigitIsExpected() {
        return Stream.of(
                "-0.0e+",
                "-0.1e+",
                "-0.2e+",
                "-0.3e+",
                "-0.4e+",
                "-0.5e+",
                "-0.6e+",
                "-0.7e+",
                "-0.8e+",
                "-0.9e+",
                "-0.0E+",
                "-0.1E+",
                "-0.2E+",
                "-0.3E+",
                "-0.4E+",
                "-0.5E+",
                "-0.6E+",
                "-0.7E+",
                "-0.8E+",
                "-0.9E+",

                "-0.0e-",
                "-0.1e-",
                "-0.2e-",
                "-0.3e-",
                "-0.4e-",
                "-0.5e-",
                "-0.6e-",
                "-0.7e-",
                "-0.8e-",
                "-0.9e-",
                "-0.0E-",
                "-0.1E-",
                "-0.2E-",
                "-0.3E-",
                "-0.4E-",
                "-0.5E-",
                "-0.6E-",
                "-0.7E-",
                "-0.8E-",
                "-0.9E-",

                "-0.",

                "-1.0e+",
                "-1.1e+",
                "-1.2e+",
                "-1.3e+",
                "-1.4e+",
                "-1.5e+",
                "-1.6e+",
                "-1.7e+",
                "-1.8e+",
                "-1.9e+",
                "-1.0E+",
                "-1.1E+",
                "-1.2E+",
                "-1.3E+",
                "-1.4E+",
                "-1.5E+",
                "-1.6E+",
                "-1.7E+",
                "-1.8E+",
                "-1.9E+",

                "-1.0e-",
                "-1.1e-",
                "-1.2e-",
                "-1.3e-",
                "-1.4e-",
                "-1.5e-",
                "-1.6e-",
                "-1.7e-",
                "-1.8e-",
                "-1.9e-",
                "-1.0E-",
                "-1.1E-",
                "-1.2E-",
                "-1.3E-",
                "-1.4E-",
                "-1.5E-",
                "-1.6E-",
                "-1.7E-",
                "-1.8E-",
                "-1.9E-",

                "-1.",

                "-0e+",
                "-1e+",
                "-2e+",
                "-3e+",
                "-4e+",
                "-5e+",
                "-6e+",
                "-7e+",
                "-8e+",
                "-9e+",
                "-0E+",
                "-1E+",
                "-2E+",
                "-3E+",
                "-4E+",
                "-5E+",
                "-6E+",
                "-7E+",
                "-8E+",
                "-9E+",

                "-0e-",
                "-1e-",
                "-2e-",
                "-3e-",
                "-4e-",
                "-5e-",
                "-6e-",
                "-7e-",
                "-8e-",
                "-9e-",
                "-0E-",
                "-1E-",
                "-2E-",
                "-3E-",
                "-4E-",
                "-5E-",
                "-6E-",
                "-7E-",
                "-8E-",
                "-9E-",

                "-",

                "0.0e+",
                "0.1e+",
                "0.2e+",
                "0.3e+",
                "0.4e+",
                "0.5e+",
                "0.6e+",
                "0.7e+",
                "0.8e+",
                "0.9e+",
                "0.0E+",
                "0.1E+",
                "0.2E+",
                "0.3E+",
                "0.4E+",
                "0.5E+",
                "0.6E+",
                "0.7E+",
                "0.8E+",
                "0.9E+",

                "0.0e-",
                "0.1e-",
                "0.2e-",
                "0.3e-",
                "0.4e-",
                "0.5e-",
                "0.6e-",
                "0.7e-",
                "0.8e-",
                "0.9e-",
                "0.0E-",
                "0.1E-",
                "0.2E-",
                "0.3E-",
                "0.4E-",
                "0.5E-",
                "0.6E-",
                "0.7E-",
                "0.8E-",
                "0.9E-",

                "0.",

                "1.0e+",
                "1.1e+",
                "1.2e+",
                "1.3e+",
                "1.4e+",
                "1.5e+",
                "1.6e+",
                "1.7e+",
                "1.8e+",
                "1.9e+",
                "1.0E+",
                "1.1E+",
                "1.2E+",
                "1.3E+",
                "1.4E+",
                "1.5E+",
                "1.6E+",
                "1.7E+",
                "1.8E+",
                "1.9E+",

                "1.0e-",
                "1.1e-",
                "1.2e-",
                "1.3e-",
                "1.4e-",
                "1.5e-",
                "1.6e-",
                "1.7e-",
                "1.8e-",
                "1.9e-",
                "1.0E-",
                "1.1E-",
                "1.2E-",
                "1.3E-",
                "1.4E-",
                "1.5E-",
                "1.6E-",
                "1.7E-",
                "1.8E-",
                "1.9E-",

                "1.",

                "0e+",
                "1e+",
                "2e+",
                "3e+",
                "4e+",
                "5e+",
                "6e+",
                "7e+",
                "8e+",
                "9e+",
                "0E+",
                "1E+",
                "2E+",
                "3E+",
                "4E+",
                "5E+",
                "6E+",
                "7E+",
                "8E+",
                "9E+",

                "0e-",
                "1e-",
                "2e-",
                "3e-",
                "4e-",
                "5e-",
                "6e-",
                "7e-",
                "8e-",
                "9e-",
                "0E-",
                "1E-",
                "2E-",
                "3E-",
                "4E-",
                "5E-",
                "6E-",
                "7E-",
                "8E-",
                "9E-"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length() + 1) + ":  Expected a digit 0 - 9 but reached end of input"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length() + 1));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsIncompleteNumberWhenDigitOrSignIsExpected() {
        return Stream.of(
                "-0.0e",
                "-0.1e",
                "-0.2e",
                "-0.3e",
                "-0.4e",
                "-0.5e",
                "-0.6e",
                "-0.7e",
                "-0.8e",
                "-0.9e",
                "-0.0E",
                "-0.1E",
                "-0.2E",
                "-0.3E",
                "-0.4E",
                "-0.5E",
                "-0.6E",
                "-0.7E",
                "-0.8E",
                "-0.9E",

                "-1.0e",
                "-1.1e",
                "-1.2e",
                "-1.3e",
                "-1.4e",
                "-1.5e",
                "-1.6e",
                "-1.7e",
                "-1.8e",
                "-1.9e",
                "-1.0E",
                "-1.1E",
                "-1.2E",
                "-1.3E",
                "-1.4E",
                "-1.5E",
                "-1.6E",
                "-1.7E",
                "-1.8E",
                "-1.9E",

                "-0e",
                "-1e",
                "-2e",
                "-3e",
                "-4e",
                "-5e",
                "-6e",
                "-7e",
                "-8e",
                "-9e",
                "-0E",
                "-1E",
                "-2E",
                "-3E",
                "-4E",
                "-5E",
                "-6E",
                "-7E",
                "-8E",
                "-9E",

                "0.0e",
                "0.1e",
                "0.2e",
                "0.3e",
                "0.4e",
                "0.5e",
                "0.6e",
                "0.7e",
                "0.8e",
                "0.9e",
                "0.0E",
                "0.1E",
                "0.2E",
                "0.3E",
                "0.4E",
                "0.5E",
                "0.6E",
                "0.7E",
                "0.8E",
                "0.9E",

                "1.0e",
                "1.1e",
                "1.2e",
                "1.3e",
                "1.4e",
                "1.5e",
                "1.6e",
                "1.7e",
                "1.8e",
                "1.9e",
                "1.0E",
                "1.1E",
                "1.2E",
                "1.3E",
                "1.4E",
                "1.5E",
                "1.6E",
                "1.7E",
                "1.8E",
                "1.9E",

                "0e",
                "1e",
                "2e",
                "3e",
                "4e",
                "5e",
                "6e",
                "7e",
                "8e",
                "9e",
                "0E",
                "1E",
                "2E",
                "3E",
                "4E",
                "5E",
                "6E",
                "7E",
                "8E",
                "9E"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length() + 1) + ":  Expected '+' or '-' or a digit 0 - 9 but reached end of input"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length() + 1));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidCharacterInNumberWhenDigitIsExpected() {
        return Stream.of(
                "-0.0e+.",
                "-0.0E+.",

                "-0.0e-.",
                "-0.0E-.",

                "-0..",
                "-0.e",

                "-1.0e+.",
                "-1.0E+.",

                "-1.0e-.",
                "-1.0E-.",

                "-0e+.",
                "-0E+.",

                "-0e-.",
                "-0E-.",

                "-1..",
                "-1.e",

                "-.",
                "-e",

                "0.0e+.",
                "0.0E+.",

                "0.0e-.",
                "0.0E-.",

                "0..",
                "0.e",

                "1.0e+.",
                "1.0E+.",

                "1.0e-.",
                "1.0E-.",

                "1..",
                "1.e",

                "0e+.",
                "0E+.",

                "0e-.",
                "0E-."
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected a digit 0 - 9 but got [" + numberString.charAt(numberString.length() - 1) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidUnprintableCharacterInNumberWhenDigitIsExpected() {
        return Stream.of(
                "0.\u0000",
                "0.\u007f",
                "0.\ud800"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            final char expectedInvalidCharacter = numberString.charAt(numberString.length() - 1);
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected a digit 0 - 9 but got [" + String.format("\\u%04X", (int) expectedInvalidCharacter) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidCharacterInNumberWhenDigitOrSignIsExpected() {
        return Stream.of(
                "-0.0e.",
                "-0.0E.",

                "-1.0e.",
                "-1.0E.",

                "-0e.",
                "-0E.",

                "0.0e.",
                "0.0E.",

                "1.0e.",
                "1.0E.",

                "0e.",
                "0E."
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected '+' or '-' or a digit 0 - 9 but got [" + numberString.charAt(numberString.length() - 1) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidUnprintableCharacterInNumberWhenDigitOrSignIsExpected() {
        return Stream.of(
                "0e\u0000",
                "0e\u007f",
                "0e\ud800"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            final char expectedInvalidCharacter = numberString.charAt(numberString.length() - 1);
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected '+' or '-' or a digit 0 - 9 but got [" + String.format("\\u%04X", (int) expectedInvalidCharacter) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsSuperfluousCharactersAfterNumber() {
        return Stream.of(
                "-00",
                "00"
        ).flatMap(numberString -> shims().map(shim -> dynamicTest(numberString + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(numberString);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected end of stream or whitespace but got [" + numberString.charAt(numberString.length() - 1) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringArray(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("[");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringArrayValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("[1");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringObject(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Expected object identifier to begin with [\"] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringFieldName(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{\"");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Got opening [\"] without matching closing [\"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamAwaitingFieldSeparator(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{\"a\"");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 5:  Expected object identifier to be followed by : but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(5));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamAwaitingFieldValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{\"a\":");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsMissingFieldValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{\"a\":}");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Invalid character [}] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamAwaitingObjectClose(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("{\"a\":1");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected either , or ] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringString(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 1:  Got opening [\"] without matching closing [\"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringHexCharacter(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("\"\\uab");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Expected 4 hexadecimal digits, but got [a, b]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringTrueValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("tru");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 't' to be followed by [r, u, e], but got [r, u]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsInvalidCharacterInTrueValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("trug");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 't' to be followed by [r, u, e], but got [r, u, g]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringFalseValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("fal");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 'f' to be followed by [a, l, s, e], but got [a, l]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsInvalidCharacterInFalseValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("farce");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected 'f' to be followed by [a, l, s, e], but got [a, r]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsPrematureEndOfStreamDuringNullValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("nul");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 'n' to be followed by [u, l, l], but got [u, l]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsInvalidCharacterInNullValue(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("numb");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected 'n' to be followed by [u, l, l], but got [u, m]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @TestFactory
    Stream<DynamicTest> permitsWhitespaceAtStartOfDocument() {
        return Stream.of(' ', '\n', '\r', '\t').flatMap(whitespaceCharacter -> shims().map(shim -> dynamicTest(whitespaceCharacter + " using " + shim, () ->
                assertThat(shim.parse(whitespaceCharacter + "null"), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT)))));
    }

    @TestFactory
    Stream<DynamicTest> permitsWhitespaceAtEndOfDocument() {
        return Stream.of(' ', '\n', '\r', '\t').flatMap(whitespaceCharacter -> shims().map(shim -> dynamicTest(whitespaceCharacter + " using " + shim, () ->
                assertThat(shim.parse("null" + whitespaceCharacter), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT)))));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesJsonObjectWithWhitespace(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(
                stajParserJsonParserShim.parse("{\"hello\": \"world\"}"),
                generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_OBJECT, startField(new StringReader("hello")), string(new StringReader("world")), NonTextJsonStreamElement.END_FIELD, NonTextJsonStreamElement.END_OBJECT, NonTextJsonStreamElement.END_DOCUMENT)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesMultiElementArrayWithWhitespace(final StajParserJsonParserShim stajParserJsonParserShim) {
        assertThat(
                stajParserJsonParserShim.parse("[ 1, 2 ]"),
                generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, number(new StringReader("1")), number(new StringReader("2")), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rejectsEmptyDocument(final StajParserJsonParserShim stajParserJsonParserShim) {
        final Iterator<JsonStreamElement> stajParser = stajParserJsonParserShim.parse("");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 1:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidCharacterAtStartOfDocument() {
        return Stream.of(
                "a",
                ".",
                "e"
        ).flatMap(text -> shims().map(shim -> dynamicTest(text + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(text);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (text.length()) + ":  Invalid character [" + text.charAt(0) + "] at start of value"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(text.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidUnprintableCharacterAtStartOfDocument() {
        return Stream.of(
                "\u0000",
                "\u007f",
                "\ud800"
        ).flatMap(text -> shims().map(shim -> dynamicTest(text + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse(text);
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
                IOUtils.consume(stajParser.next().reader());
                stajParser.next();
            });
            final char expectedInvalidCharacter = text.charAt(0);
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (text.length()) + ":  Invalid character [" + String.format("\\u%04X", (int) expectedInvalidCharacter) + "] at start of value"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(text.length()));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidCharacterAtEndOfDocument() {
        return Stream.of(
                "a",
                ".",
                "e"
        ).flatMap(superfluousCharacter -> shims().map(shim -> dynamicTest(superfluousCharacter + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse("\"foo\" " + superfluousCharacter);
            stajParser.next();
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected end of stream or whitespace but got [" + superfluousCharacter + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidUnprintableCharacterAtEndOfDocument() {
        return Stream.of(
                '\u0000',
                '\u007f',
                '\ud800'
        ).flatMap(superfluousCharacter -> shims().map(shim -> dynamicTest(superfluousCharacter + " using " + shim, () -> {
            final Iterator<JsonStreamElement> stajParser = shim.parse("\"foo\" " + superfluousCharacter);
            stajParser.next();
            stajParser.next();
            final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
            assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected end of stream or whitespace but got [" + String.format("\\u%04X", (int) superfluousCharacter) + "]"));
            assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
            assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
        })));
    }

    static final class ParserArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return shims().map(Arguments::arguments);
        }
    }

    static final class TestCase {
        final String input;
        final int expected;

        TestCase(String input, int expected) {
            this.input = input;
            this.expected = expected;
        }

        static TestCase testCase(final String input, final int expected) {
            return new TestCase(input, expected);
        }
    }

    private static final class MyTestRuntimeException extends RuntimeException {
    }
}
