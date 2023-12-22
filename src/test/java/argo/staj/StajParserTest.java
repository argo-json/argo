/*
 *  Copyright 2023 Mark Slater
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
import org.apache.commons.io.input.BrokenReader;
import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
        assertThat(exception.getMessage(), equalTo("StajParser cannot remove elements from JSON it has parsed."));
    }
}
