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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("PMD.CloseResource")
final class StajParserTest {

    @Test
    void arrayOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(array()), generatesElements(startDocument(), startArray(), endArray(), endDocument()));
    }

    @Test
    void objectOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(object()), generatesElements(startDocument(), startObject(), endObject(), endDocument()));
    }

    @Test
    void stringOnlyDocumentHasCorrectElements() {
        final JsonNode stringNode = aStringNode();
        assertThat(stajParser(stringNode), generatesElements(startDocument(), string(new StringReader(stringNode.getText())), endDocument()));
    }

    @Test
    void numberOnlyDocumentHasCorrectElements() {
        final JsonNode numberNode = aNumberNode();
        assertThat(stajParser(numberNode), generatesElements(startDocument(), number(new StringReader(numberNode.getText())), endDocument()));
    }

    @Test
    void nullOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(nullNode()), generatesElements(startDocument(), nullValue(), endDocument()));
    }

    @Test
    void trueOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(trueNode()), generatesElements(startDocument(), trueValue(), endDocument()));
    }

    @Test
    void falseOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(falseNode()), generatesElements(startDocument(), falseValue(), endDocument()));
    }

    @Test
    void arrayWithChildHasCorrectElements() {
        assertThat(stajParser(array(array())), generatesElements(startDocument(), startArray(), startArray(), endArray(), endArray(), endDocument()));
    }

    @Test
    void arrayWithChildrenHasCorrectElements() {
        assertThat(stajParser(array(array(), array())), generatesElements(
                startDocument(),
                startArray(),
                startArray(),
                endArray(),
                startArray(),
                endArray(),
                endArray(),
                endDocument()
        ));
    }

    @Test
    void objectWithFieldHasCorrectElements() {
        final JsonStringNode aFieldName = JsonStringNodeTestBuilder.aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()))), generatesElements(
                startDocument(),
                startObject(),
                startField(new StringReader(aFieldName.getText())),
                startArray(),
                endArray(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    void objectWithFieldsHasCorrectElements() {
        final JsonStringNode aFieldName = aStringNode();
        final JsonStringNode anotherFieldName = aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()), field(anotherFieldName, object()))), generatesElements(
                startDocument(),
                startObject(),
                startField(new StringReader(aFieldName.getText())),
                startArray(),
                endArray(),
                endField(),
                startField(new StringReader(anotherFieldName.getText())),
                startObject(),
                endObject(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    void arrayWithNullHasCorrectElements() {
        assertThat(stajParser(array(nullNode())), generatesElements(startDocument(), startArray(), nullValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithNullsHasCorrectElements() {
        assertThat(stajParser(array(nullNode(), nullNode())), generatesElements(startDocument(), startArray(), nullValue(), nullValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithTrueHasCorrectElements() {
        assertThat(stajParser(array(trueNode())), generatesElements(startDocument(), startArray(), trueValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithFalseHasCorrectElements() {
        assertThat(stajParser(array(falseNode())), generatesElements(startDocument(), startArray(), falseValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithATextNodeHasCorrectElements() {
        final JsonNode aStringNode = aStringNode();
        assertThat(stajParser(array(aStringNode)), generatesElements(startDocument(), startArray(), string(new StringReader(aStringNode.getText())), endArray(), endDocument()));
    }

    @Test
    void arrayWithANumberNodeHasCorrectElements() {
        final JsonNode aNumberNode = aNumberNode();
        assertThat(stajParser(array(aNumberNode)), generatesElements(startDocument(), startArray(), number(new StringReader(aNumberNode.getText())), endArray(), endDocument()));
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
        assertThat(stajParser(array()).next(), equalTo(startDocument()));
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
    void propagatesIoExceptionReadingNumber() throws IOException {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("1"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.read();
        final IOException actualException = assertThrows(IOException.class, reader::read);
        assertThat(actualException, sameInstance(ioException));
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
        assertThrows(IOException.class, () -> reader.read());
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
    @Disabled("It's not totally clear whether always returning false is OK")
    void afterClosingANumberReaderReadyThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.ready());
    }

    @Test
    void afterClosingANumberReaderResetThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.reset());
    }

    @Test
    @Disabled("It's not totally clear whether skip(0) should fail")
    void afterClosingANumberReaderSkipThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @Test
    void attemptingToMarkANumberReaderThrowsIOException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @Test
    void aNumberReaderDoesNotSupportMark() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        assertThat(stajParser.next().reader().markSupported(), equalTo(false));
    }

    @Test
    void canReadNumberCharacterByCharacter() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.read(), equalTo((int)'1'));
        assertThat(reader.read(), equalTo((int)'2'));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canReadNumberToABuffer() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final char[] buffer = new char[3];
        assertThat(reader.read(buffer), equalTo(2));
        assertThat(buffer, equalTo(new char[]{'1', '2', 0}));
        assertThat(reader.read(buffer), equalTo(-1));
    }

    @Test
    void canReadNumberToABufferWithOffsetAndLength() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final char[] buffer = new char[3];
        assertThat(reader.read(buffer, 1, 1), equalTo(1));
        assertThat(buffer, equalTo(new char[]{0, '1', 0}));
        assertThat(reader.read(), equalTo((int)'2'));
        assertThat(reader.read(buffer), equalTo(-1));
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
    }

    @Test
    void attemptingToReadNumberToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
    }

    @Test
    void canReadNumberToACharBuffer() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final CharBuffer charBuffer = CharBuffer.allocate(3);
        assertThat(reader.read(charBuffer), equalTo(2));
        assertThat(charBuffer.array(), equalTo(new char[]{'1', '2', 0}));
        assertThat(reader.read(charBuffer), equalTo(-1));
    }

    @Test
    void numberReaderReadyReturnsFalseToBeOnTheSafeSide() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        assertThat(stajParser.next().reader().ready(), equalTo(false));
    }

    @Test
    void attemptingToResetANumberReaderThrowsIOException() {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IOException.class, () -> reader.reset());
    }

    @Test
    void canSkipASubsetCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(1), equalTo(1L));
        assertThat(reader.read(), equalTo((int)'2'));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canSkipPastAllCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(3), equalTo(2L));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canSkipZeroCharactersInANumberReader() throws IOException {
        final StajParser stajParser = new StajParser("12");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(0), equalTo(0L));
        assertThat(reader.read(), equalTo((int)'1'));
        assertThat(reader.read(), equalTo((int)'2'));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void propagatesIoExceptionReadingString() throws IOException {
        final IOException ioException = new IOException("An IOException");
        final StajParser stajParser = new StajParser(new SequenceReader(
                new StringReader("\"F"),
                new BrokenReader(ioException)
        ));
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.read();
        final IOException actualException = assertThrows(IOException.class, reader::read);
        assertThat(actualException, sameInstance(ioException));
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
        assertThrows(IOException.class, () -> reader.read());
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
    @Disabled("It's not totally clear whether always returning false is OK")
    void afterClosingAStringReaderReadyThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.ready());
    }

    @Test
    void afterClosingAStringReaderResetThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.reset());
    }

    @Test
    @Disabled("It's not totally clear whether skip(0) should fail")
    void afterClosingAStringReaderSkipThrowsIOException() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(0));
    }

    @Test
    void attemptingToMarkAStringReaderThrowsIOException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IOException.class, () -> reader.mark(0));
    }

    @Test
    void aStringReaderDoesNotSupportMark() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        assertThat(stajParser.next().reader().markSupported(), equalTo(false));
    }

    @Test
    void canReadStringCharacterByCharacter() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.read(), equalTo((int)'F'));
        assertThat(reader.read(), equalTo((int)'o'));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canReadStringToABuffer() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final char[] buffer = new char[3];
        assertThat(reader.read(buffer), equalTo(2));
        assertThat(buffer, equalTo(new char[]{'F', 'o', 0}));
        assertThat(reader.read(buffer), equalTo(-1));
    }

    @Test
    void canReadStringToABufferWithOffsetAndLength() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final char[] buffer = new char[3];
        assertThat(reader.read(buffer, 1, 1), equalTo(1));
        assertThat(buffer, equalTo(new char[]{0, 'F', 0}));
        assertThat(reader.read(), equalTo((int)'o'));
        assertThat(reader.read(buffer), equalTo(-1));
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsNegativeThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], -1, 0));
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 4, 0));
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereLengthIsLessThanZeroThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 0, -1));
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthIsGreaterThanBufferLengthThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, 3));
    }

    @Test
    void attemptingToReadStringToABufferWithOffsetAndLengthWhereOffsetPlusLengthOverflowsThrowsIndexOutOfBoundsException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IndexOutOfBoundsException.class, () -> reader.read(new char[3], 1, Integer.MAX_VALUE));
    }

    @Test
    void canReadStringToACharBuffer() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        final CharBuffer charBuffer = CharBuffer.allocate(3);
        assertThat(reader.read(charBuffer), equalTo(2));
        assertThat(charBuffer.array(), equalTo(new char[]{'F', 'o', 0}));
        assertThat(reader.read(charBuffer), equalTo(-1));
    }

    @Test
    void stringReaderReadyReturnsFalseToBeOnTheSafeSide() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        assertThat(stajParser.next().reader().ready(), equalTo(false));
    }

    @Test
    void attemptingToResetAStringReaderThrowsIOException() {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThrows(IOException.class, () -> reader.reset());
    }

    @Test
    void canSkipASubsetCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(1), equalTo(1L));
        assertThat(reader.read(), equalTo((int)'o'));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canSkipPastAllCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(3), equalTo(2L));
        assertThat(reader.read(), equalTo(-1));
    }

    @Test
    void canSkipZeroCharactersInAStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"Fo\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        assertThat(reader.skip(0), equalTo(0L));
        assertThat(reader.read(), equalTo((int)'F'));
        assertThat(reader.read(), equalTo((int)'o'));
        assertThat(reader.read(), equalTo(-1));
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
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> stajParser.remove());
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
    void tokenizesValidNumber(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        assertThat(stajParser, generatesElements(startDocument(), number(new StringReader(numberString)), endDocument()));
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
    void handlesIncompleteNumberWhenDigitIsExpected(final String numberString) {
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
    void handlesIncompleteNumberWhenDigitOrSignIsExpected(final String numberString) {
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
            "",
    })
    void handlesMissingValue(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length() + 1) + ":  Expected a value but reached end of input"));
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
    void handlesInvalidCharacterInNumberWhenDigitIsExpected(final String numberString) {
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
    void handlesInvalidUnprintableCharacterInNumberWhenDigitIsExpected(final String numberString) {
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
    void handlesInvalidCharacterInNumberWhenDigitOrSignIsExpected(final String numberString) {
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
    void handlesInvalidUnprintableCharacterInNumberWhenDigitOrSignIsExpected(final String numberString) {
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
            ".",
            "e",
    })
    void handlesInvalidCharacterAtStartOfValue(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Invalid character [" + numberString.charAt(0) + "] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\u0000",
            "\u007f",
            "\ud800",
    })
    void handlesInvalidUnprintableCharacterAtStartOfValue(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        final char expectedInvalidCharacter = numberString.charAt(0);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Invalid character [" + String.format("\\u%04X", (int)expectedInvalidCharacter) + "] at start of value"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-00",
            "00",
    })
    void handlesSuperfluousCharactersAfterNumber(final String numberString) {
        final StajParser stajParser = new StajParser(numberString);
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, () -> {
            IOUtils.consume(stajParser.next().reader());
            stajParser.next();
        });
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column " + (numberString.length()) + ":  Expected only whitespace but got [" + numberString.charAt(numberString.length() - 1) + "]")); // TODO this message is misleading.
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(numberString.length()));
        assertThat(invalidSyntaxRuntimeException.getLine(), equalTo(1));
    }

    @Test
    void skipsCharactersInANumber() throws IOException {
        final StajParser stajParser = new StajParser("123456");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(5), equalTo(5L));
            assertThat(reader.read(), equalTo((int)'6'));
        }
    }

    @Test
    void skipsPastEndOfANumber() throws IOException {
        final StajParser stajParser = new StajParser("123456");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(10), equalTo(6L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void skipsAfterEndOfANumber() throws IOException {
        final StajParser stajParser = new StajParser("123456");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.skip(10), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void handlesSkippingOnAClosedNumberReader() throws IOException {
        final StajParser stajParser = new StajParser("123456");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(1L));
    }

    @Test
    void rejectsNegateArgumentToSkipOnNumberReader() throws IOException {
        final StajParser stajParser = new StajParser("123456");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
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
        final StajParser stajParser = new StajParser("falte");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 4:  Expected 'f' to be followed by [a, l, s, e], but got [a, l, t]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(4));
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
        final StajParser stajParser = new StajParser("nurl");
        stajParser.next();
        final InvalidSyntaxRuntimeException invalidSyntaxRuntimeException = assertThrows(InvalidSyntaxRuntimeException.class, stajParser::next);
        assertThat(invalidSyntaxRuntimeException.getMessage(), equalTo("At line 1, column 3:  Expected 'n' to be followed by [u, l, l], but got [u, r]"));
        assertThat(invalidSyntaxRuntimeException.getColumn(), equalTo(3));
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
    void tokenizesValidStringWithEscapedChars() {
        assertThat(new StajParser("\"\\\"hello world\\\"\""), generatesElements(startDocument(), string(new StringReader("\"hello world\"")), endDocument()));
    }

    @Test
    void tokenizesValidStringWithEscapedUnicodeChars() {
        assertThat(new StajParser("\"\\uF001\""), generatesElements(startDocument(), string(new StringReader("\uF001")), endDocument()));
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
    void skipsCharactersInAString() throws IOException {
        final StajParser stajParser = new StajParser("\"abcdef\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(5), equalTo(5L));
            assertThat(reader.read(), equalTo((int)'f'));
        }
    }

    @Test
    void skipsPastEndOfAString() throws IOException {
        final StajParser stajParser = new StajParser("\"abcdef\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThat(reader.skip(10), equalTo(6L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void skipsAfterEndOfAString() throws IOException {
        final StajParser stajParser = new StajParser("\"abcdef\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            IOUtils.consume(reader);
            assertThat(reader.skip(10), equalTo(0L));
            assertThat(reader.read(), equalTo(-1));
        }
    }

    @Test
    void handlesSkippingOnAClosedStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"abcdef\"");
        stajParser.next();
        final Reader reader = stajParser.next().reader();
        reader.close();
        assertThrows(IOException.class, () -> reader.skip(1L));
    }

    @Test
    void rejectsNegateArgumentToSkipOnStringReader() throws IOException {
        final StajParser stajParser = new StajParser("\"abcdef\"");
        stajParser.next();
        try (Reader reader = stajParser.next().reader()) {
            assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
        }
    }
    // TODO test failures where . or e would have been valid
}
