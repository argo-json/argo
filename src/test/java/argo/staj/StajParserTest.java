/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import argo.format.PrettyJsonBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import argo.jdom.JsonStringNodeTestBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BrokenReader;
import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.NoSuchElementException;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.aJsonNode;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.*;
import static argo.staj.ElementTrackingStajParserMatcher.generatesElements;
import static argo.staj.JsonStreamElement.number;
import static argo.staj.JsonStreamElement.string;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.RoundTrippingStajParserMatcher.parsesTo;
import static argo.staj.StajParserBuilder.stajParser;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PMD.CloseResource")
final class StajParserTest {

    @Test
    void parsesFromReader() {
        assertThat(new StajParser(new StringReader("null")), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void parsesFromString() {
        assertThat(new StajParser("null"), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(array()), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void objectOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(object()), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_OBJECT, NonTextJsonStreamElement.END_OBJECT, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void numberOnlyDocumentHasCorrectElements() {
        final JsonNode numberNode = aNumberNode();
        assertThat(stajParser(numberNode), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, number(new StringReader(numberNode.getText())), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void stringOnlyDocumentHasCorrectElements() {
        final JsonNode stringNode = aStringNode();
        assertThat(stajParser(stringNode), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader(stringNode.getText())), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void nullOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(nullNode()), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void trueOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(trueNode()), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.TRUE, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void falseOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(falseNode()), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.FALSE, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithChildHasCorrectElements() {
        assertThat(stajParser(array(array())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithChildrenHasCorrectElements() {
        assertThat(stajParser(array(array(), object())), generatesElements(
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

    @Test
    void arrayWithNullHasCorrectElements() {
        assertThat(stajParser(array(nullNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithNullsHasCorrectElements() {
        assertThat(stajParser(array(nullNode(), nullNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithTrueHasCorrectElements() {
        assertThat(stajParser(array(trueNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.TRUE, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithFalseHasCorrectElements() {
        assertThat(stajParser(array(falseNode())), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, NonTextJsonStreamElement.FALSE, NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithATextNodeHasCorrectElements() {
        final JsonNode aStringNode = aStringNode();
        assertThat(stajParser(array(aStringNode)), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, string(new StringReader(aStringNode.getText())), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void arrayWithANumberNodeHasCorrectElements() {
        final JsonNode aNumberNode = aNumberNode();
        assertThat(stajParser(array(aNumberNode)), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, number(new StringReader(aNumberNode.getText())), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void objectWithFieldHasCorrectElements() {
        final JsonStringNode aFieldName = JsonStringNodeTestBuilder.aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()))), generatesElements(
                NonTextJsonStreamElement.START_DOCUMENT,
                NonTextJsonStreamElement.START_OBJECT,
                startField(new StringReader(aFieldName.getText())),
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.END_ARRAY,
                endField(),
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_DOCUMENT
        ));
    }

    @Test
    void objectWithFieldsHasCorrectElements() {
        final JsonStringNode aFieldName = aStringNode();
        final JsonStringNode anotherFieldName = aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()), field(anotherFieldName, object()))), generatesElements(
                NonTextJsonStreamElement.START_DOCUMENT,
                NonTextJsonStreamElement.START_OBJECT,
                startField(new StringReader(aFieldName.getText())),
                NonTextJsonStreamElement.START_ARRAY,
                NonTextJsonStreamElement.END_ARRAY,
                endField(),
                startField(new StringReader(anotherFieldName.getText())),
                NonTextJsonStreamElement.START_OBJECT,
                NonTextJsonStreamElement.END_OBJECT,
                endField(),
                NonTextJsonStreamElement.END_OBJECT,
                NonTextJsonStreamElement.END_DOCUMENT
        ));
    }

    @Test
    void aRandomJsonNodeHasCorrectElements() {
        final JsonNode aJsonNode = aJsonNode();
        assertThat(stajParser(aJsonNode), parsesTo(aJsonNode));
    }

    @Test
    void aRandomJsonNodeFromStringHasCorrectElements() {
        final JsonNode jsonNode = aJsonNode();
        assertThat(new StajParser(PrettyJsonBuilder.json(jsonNode)), parsesTo(jsonNode));
    }

    @Test
    void nextWorksWithoutCallingHasNext() {
        assertThat(stajParser(array()).next(), equalTo(NonTextJsonStreamElement.START_DOCUMENT));
    }

    @Test
    void toleratesFieldNameTextNotBeingRead() {
        final StajParser stajParser = stajParser(object(field(aNonEmptyString(), nullNode())));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    void toleratesStringTextNotBeingRead() {
        final StajParser stajParser = stajParser(array(JsonNodeFactories.string(aNonEmptyString())));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    void toleratesNumberTextNotBeingRead() {
        final StajParser stajParser = stajParser(array(aNumberNode()));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    @SuppressWarnings("PMD.UseTryWithResources")
    void propagatesIoExceptionReadingNumber() throws IOException {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
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

    @Test
    void propagatesIoExceptionSkippingNumberByGettingNextElement() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @Test
    void propagatesIoExceptionSkippingNumberByQueryingNextElement() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::hasNext);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @Test
    void canCloseANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        stajParser.next().reader().close();
    }

    @Test
    void afterClosingANumberReaderCanCallCloseAgain() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        reader.close();
    }

    @Test
    void afterClosingANumberReaderMarkThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @Test
    void afterClosingANumberReaderMarkSupportedReturnsFalse() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThat(reader.markSupported(), equalTo(false));
    }

    @Test
    void afterClosingANumberReaderReadingACharacterThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void afterClosingANumberReaderReadingToABufferThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3]));
    }

    @Test
    void afterClosingANumberReaderReadingToABufferWithOffsetAndLengthThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3], 0, 1));
    }

    @Test
    void afterClosingANumberReaderReadingToACharBufferThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(CharBuffer.allocate(3)));
    }

    @Test
    void afterClosingANumberReaderReadyThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::ready);
    }

    @Test
    void afterClosingANumberReaderResetThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::reset);
    }

    @Test
    void afterClosingANumberReaderSkipThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @Test
    void attemptingToMarkANumberReaderThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, () -> reader.mark(0));
        }
    }

    @Test
    void aNumberReaderDoesNotSupportMark() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.markSupported(), equalTo(false));
        }
    }

    @Test
    void canReadNumberCharacterByCharacter() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.read(), equalTo((int) '1'));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canReadNumberToABuffer() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer), equalTo(2));
            assertThat(buffer, equalTo(new char[]{'1', '2', 0}));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @Test
    void canReadNumberToABufferWithOffsetAndLength() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer, 1, 1), equalTo(1));
            assertThat(buffer, equalTo(new char[]{0, '1', 0}));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
        }
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
        }
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
        }
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
        }
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @Test
    void canReadNumberToACharBuffer() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final CharBuffer charBuffer = CharBuffer.allocate(3);
            assertThat(reader.read(charBuffer), equalTo(2));
            assertThat(charBuffer.array(), equalTo(new char[]{'1', '2', 0}));
            assertThat(reader.read(charBuffer), equalTo(-1));
        }
    }

    @Test
    void numberReaderReadyReturnsFalseToBeOnTheSafeSide() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.ready(), equalTo(false));
        }
    }

    @Test
    void attemptingToResetANumberReaderThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, reader::reset);
        }
    }

    @Test
    void canSkipASubsetCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(1), equalTo(1L));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canSkipPastAllCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(3), equalTo(2L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canSkipZeroCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(0), equalTo(0L));
            assertThat(reader.read(), equalTo((int) '1'));
            assertThat(reader.read(), equalTo((int) '2'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void rejectsNegativeSkipValueInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
    }

    @Test
    void canSkipAfterEndOfStreamInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.read(), equalTo(-1));
            assertThat(reader.skip(1), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    @SuppressWarnings("PMD.UseTryWithResources")
    void propagatesIoExceptionReadingString() throws IOException {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
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

    @Test
    void propagatesIoExceptionSkippingStringByGettingNextElement() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @Test
    void propagatesIoExceptionSkippingStringByQueryingNextElement() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException actualException = assertThrows(JsonStreamException.class, stajParser::hasNext);
        assertThat(actualException.getCause(), sameInstance(ioException));
    }

    @Test
    void canCloseAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        stajParser.next().reader().close();
    }

    @Test
    void afterClosingAStringReaderCanCallCloseAgain() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        reader.close();
    }

    @Test
    void afterClosingAStringReaderMarkThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @Test
    void afterClosingAStringReaderMarkSupportedReturnsFalse() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThat(reader.markSupported(), equalTo(false));
    }

    @Test
    void afterClosingAStringReaderReadingACharacterThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void afterClosingAStringReaderReadingToABufferThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3]));
    }

    @Test
    void afterClosingAStringReaderReadingToABufferWithOffsetAndLengthThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(new char[3], 0, 1));
    }

    @Test
    void afterClosingAStringReaderReadingToACharBufferThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.read(CharBuffer.allocate(3)));
    }

    @Test
    void afterClosingAStringReaderReadyThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::ready);
    }

    @Test
    void afterClosingAStringReaderResetThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, reader::reset);
    }

    @Test
    void afterClosingAStringReaderSkipThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @Test
    void attemptingToMarkAStringReaderThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, () -> reader.mark(0));
        }
    }

    @Test
    void aStringReaderDoesNotSupportMark() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.markSupported(), equalTo(false));
        }
    }

    @Test
    void canReadStringCharacterByCharacter() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.read(), equalTo((int) 'F'));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canReadStringToABuffer() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer), equalTo(2));
            assertThat(buffer, equalTo(new char[]{'F', 'o', 0}));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @Test
    void canReadStringToABufferWithOffsetAndLength() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final char[] buffer = new char[3];
            assertThat(reader.read(buffer, 1, 1), equalTo(1));
            assertThat(buffer, equalTo(new char[]{0, 'F', 0}));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(buffer), equalTo(-1));
        }
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
        }
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
        }
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
        }
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
        }
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
        }
    }

    @Test
    void canReadStringToACharBuffer() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            final CharBuffer charBuffer = CharBuffer.allocate(3);
            assertThat(reader.read(charBuffer), equalTo(2));
            assertThat(charBuffer.array(), equalTo(new char[]{'F', 'o', 0}));
            assertThat(reader.read(charBuffer), equalTo(-1));
        }
    }

    @Test
    void stringReaderReadyReturnsFalseToBeOnTheSafeSide() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.ready(), equalTo(false));
        }
    }

    @Test
    void attemptingToResetAStringReaderThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IOException.class, reader::reset);
        }
    }

    @Test
    void canSkipASubsetCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(1), equalTo(1L));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canSkipPastAllCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(3), equalTo(2L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void canSkipZeroCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(0), equalTo(0L));
            assertThat(reader.read(), equalTo((int) 'F'));
            assertThat(reader.read(), equalTo((int) 'o'));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void rejectsNegativeSkipValueInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
    }

    @Test
    void canSkipAfterEndOfStreamInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.read(), equalTo(-1));
            assertThat(reader.skip(1), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "\", 0x22",
            "\\, 0x5c",
            "/, 0x2f",
            "b, 0x8",
            "f, 0xc",
            "n, 0xa",
            "r, 0xd",
            "t, 0x9",
    })
    void parsesValidStringWithEscapedChars(final String input, final int expected) {
        assertThat(new StajParser("\"\\" + input + "\""), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader(String.valueOf((char)expected))), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void parsesValidStringWithEscapedUnicodeChars() {
        assertThat(new StajParser("\"\\uF001\""), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, string(new StringReader("\uF001")), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void rejectsStringWithIncompleteEscapedUnicodeChars() {
        final StajParser stajParser = new StajParser("\"\\uF0\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected 4 hexadecimal digits, but got [F, 0, \"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsStringWithPrematureEndOfStreamFollowingBackslash() {
        final StajParser stajParser = new StajParser("\"\\");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsStringWithInvalidCharacterFollowingBackslash() {
        final StajParser stajParser = new StajParser("\"\\a\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but got [a]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsStringWithInvalidUnprintableCharacterFollowingBackslash() {
        final StajParser stajParser = new StajParser("\"\\\u0000\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected \\ to be followed by one of \", \\, /, b, f, n, r, t, or u but got [\\u0000]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsStringWithNonHexadecimalEscapedUnicodeChars() {
        final StajParser stajParser = new StajParser("\"\\uF00L\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Unable to parse escaped character [F, 0, 0, L] as a hexadecimal number"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsStringWithNonHexadecimalNonPrintingEscapedUnicodeChars() {
        final StajParser stajParser = new StajParser("\"\\uF00\u007f\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Unable to parse escaped character [F, 0, 0, \\u007F] as a hexadecimal number"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void callingNextWhenHasNextReturnsFalseThrowsAnException() {
        final StajParser stajParser = stajParser(array());
        while (stajParser.hasNext()) {
            stajParser.next();
        }
        assertThrows(NoSuchElementException.class, stajParser::next);
    }

    @Test
    void handlesIoExceptionDuringParsing() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new BrokenReader(ioException));
        stajParser.next();
        final JsonStreamException jsonStreamException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(jsonStreamException.getCause(), equalTo(ioException));
    }

    @Test
    void handlesIoExceptionSkippingElement() {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("["),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        stajParser.next();
        final JsonStreamException jsonStreamException = assertThrows(JsonStreamException.class, stajParser::next);
        assertThat(jsonStreamException.getCause(), sameInstance(ioException));
    }

    @Test
    void handlesRuntimeExceptionDuringParsing() {
        final StajParser stajParser = new StajParser(new Reader() { // TODO commons-io BrokenReader ought to be made to throw RuntimeExceptions too.
            public int read(char[] chars, int offset, int length) {
                throw new MyTestRuntimeException();
            }

            public void close() {
            }
        });
        stajParser.next();
        assertThrows(MyTestRuntimeException.class, stajParser::next);
    }

    private static final class MyTestRuntimeException extends RuntimeException {
    }

    @Test
    void removeThrowsUnsupportedOperationException() {
        final StajParser stajParser = new StajParser(aValidJsonString());
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, stajParser::remove);
        assertThat(exception.getMessage(), equalTo("StajParser cannot remove elements from JSON it has parsed"));
    }


    @ParameterizedTest
    @ValueSource(strings = {
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
            "9E99",
    })
    void parsesValidNumber(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        assertThat(stajParser, generatesElements(NonTextJsonStreamElement.START_DOCUMENT, number(new StringReader(numberString)), NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ValueSource(strings = {
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
            "9E-",
    })
    void rejectsIncompleteNumberWhenDigitIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length() + 1) + ":  Expected a digit 0 - 9 but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length() + 1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
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
            "9E",
    })
    void rejectsIncompleteNumberWhenDigitOrSignIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length() + 1) + ":  Expected '+' or '-' or a digit 0 - 9 but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length() + 1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
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
            "0E-.",
    })
    void rejectsInvalidCharacterInNumberWhenDigitIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected a digit 0 - 9 but got [" + numberString.charAt(numberString.length() - 1) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.\u0000",
            "0.\u007f",
            "0.\ud800",
    })
    void rejectsInvalidUnprintableCharacterInNumberWhenDigitIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        final char expectedInvalidCharacter = numberString.charAt(numberString.length() - 1);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected a digit 0 - 9 but got [" + String.format("\\u%04X", (int)expectedInvalidCharacter) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
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
            "0E.",
    })
    void rejectsInvalidCharacterInNumberWhenDigitOrSignIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected '+' or '-' or a digit 0 - 9 but got [" + numberString.charAt(numberString.length() -1) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0e\u0000",
            "0e\u007f",
            "0e\ud800",
    })
    void rejectsInvalidUnprintableCharacterInNumberWhenDigitOrSignIsExpected(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        final char expectedInvalidCharacter = numberString.charAt(numberString.length() - 1);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected '+' or '-' or a digit 0 - 9 but got [" + String.format("\\u%04X", (int)expectedInvalidCharacter) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-00",
            "00",
    })
    void rejectsSuperfluousCharactersAfterNumber(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected end of stream or whitespace but got [" + numberString.charAt(numberString.length() - 1) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringArray() {
        final StajParser stajParser = new StajParser("[");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringArrayValue() {
        final StajParser stajParser = new StajParser("[1");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected either , or ] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringObject() {
        final StajParser stajParser = new StajParser("{");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Expected object identifier to begin with [\"] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFieldName() {
        final StajParser stajParser = new StajParser("{\"");
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 2:  Got opening [\"] without matching closing [\"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(2));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamAwaitingFieldSeparator() {
        final StajParser stajParser = new StajParser("{\"a\"");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 5:  Expected object identifier to be followed by : but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(5));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamAwaitingFieldValue() {
        final StajParser stajParser = new StajParser("{\"a\":");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsMissingFieldValue() {
        final StajParser stajParser = new StajParser("{\"a\":}");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Invalid character [}] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamAwaitingObjectClose() {
        final StajParser stajParser = new StajParser("{\"a\":1");
        stajParser.next();
        stajParser.next();
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected either , or ] but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringString() {
        final StajParser stajParser = new StajParser("\"");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 1:  Got opening [\"] without matching closing [\"]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringHexCharacter() {
        final StajParser stajParser = new StajParser("\"\\uab");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> IOUtils.consume(stajParser.next().reader()));
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 6:  Expected 4 hexadecimal digits, but got [a, b]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(6));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringTrueValue() {
        final StajParser stajParser = new StajParser("tru");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 't' to be followed by [r, u, e], but got [r, u]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsInvalidCharacterInTrueValue() {
        final StajParser stajParser = new StajParser("trug");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 't' to be followed by [r, u, e], but got [r, u, g]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringFalseValue() {
        final StajParser stajParser = new StajParser("fal");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 'f' to be followed by [a, l, s, e], but got [a, l]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsInvalidCharacterInFalseValue() {
        final StajParser stajParser = new StajParser("farce");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected 'f' to be followed by [a, l, s, e], but got [a, r]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsPrematureEndOfStreamDuringNullValue() {
        final StajParser stajParser = new StajParser("nul");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 'n' to be followed by [u, l, l], but got [u, l]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void rejectsInvalidCharacterInNullValue() {
        final StajParser stajParser = new StajParser("numb");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected 'n' to be followed by [u, l, l], but got [u, m]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(chars = {' ', '\n', '\r', '\t'})
    void permitsWhitespaceAtStartOfDocument(final char whitespaceCharacter) {
        assertThat(new StajParser(whitespaceCharacter + "null"), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @ParameterizedTest
    @ValueSource(chars = {' ', '\n', '\r', '\t'})
    void permitsWhitespaceAtEndOfDocument(final char whitespaceCharacter) {
        assertThat(new StajParser("null" + whitespaceCharacter), generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.NULL, NonTextJsonStreamElement.END_DOCUMENT));
    }

    @Test
    void parsesJsonObjectWithWhitespace() {
        assertThat(
                new StajParser("{\"hello\": \"world\"}"),
                generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_OBJECT, startField(new StringReader("hello")), string(new StringReader("world")), endField(), NonTextJsonStreamElement.END_OBJECT, NonTextJsonStreamElement.END_DOCUMENT)
        );
    }

    @Test
    void parsesMultiElementArrayWithWhitespace() {
        assertThat(
                new StajParser("[ 1, 2 ]"),
                generatesElements(NonTextJsonStreamElement.START_DOCUMENT, NonTextJsonStreamElement.START_ARRAY, number(new StringReader("1")), number(new StringReader("2")), NonTextJsonStreamElement.END_ARRAY, NonTextJsonStreamElement.END_DOCUMENT)
        );
    }

    @Test
    void rejectsEmptyDocument() {
        final StajParser stajParser = new StajParser("");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 1:  Expected a value but reached end of input"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a",
            ".",
            "e",
    })
    void rejectsInvalidCharacterAtStartOfDocument(final String text) {
        final StajParser stajParser = new StajParser(text);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (text.length()) + ":  Invalid character [" + text.charAt(0) + "] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(text.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\u0000",
            "\u007f",
            "\ud800",
    })
    void rejectsInvalidUnprintableCharacterAtStartOfDocument(final String text) {
        final StajParser stajParser = new StajParser(text);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        final char expectedInvalidCharacter = text.charAt(0);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (text.length()) + ":  Invalid character [" + String.format("\\u%04X", (int)expectedInvalidCharacter) + "] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(text.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(chars = {
            'a',
            '.',
            'e',
    })
    void rejectsInvalidCharacterAtEndOfDocument(final char superfluousCharacter) {
        final StajParser stajParser = new StajParser("\"foo\" " + superfluousCharacter);
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected end of stream or whitespace but got [" + superfluousCharacter + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(chars = {
            '\u0000',
            '\u007f',
            '\ud800',
    })
    void rejectsInvalidUnprintableCharacterAtEndOfDocument(final char superfluousCharacter) {
        final StajParser stajParser = new StajParser("\"foo\" " + superfluousCharacter);
        stajParser.next();
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 7:  Expected end of stream or whitespace but got [" + String.format("\\u%04X", (int) superfluousCharacter) + "]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(7));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

}
