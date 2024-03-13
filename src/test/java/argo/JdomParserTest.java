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
import argo.jdom.JsonNodeSelectors;
import org.apache.commons.io.input.BrokenReader;
import org.apache.commons.io.input.SequenceReader;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JdomParserTest {

    static final class ParserArgumentsProvider implements ArgumentsProvider {
        @Override
        @SuppressWarnings("deprecation")
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new JdomParserJsonParserShim.Jdom(new argo.jdom.JdomParser()),
                    new JdomParserJsonParserShim.Json(new JsonParser())
            ).map(Arguments::arguments);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesANumber(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("42"), equalTo(number(42)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesAString(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("\"Foo\""), equalTo(string("Foo")));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesASingletonNumberZero(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("0"), sameInstance(number(0)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesASingletonNumberOne(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("1"), sameInstance(number(1)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesASingletonEmptyString(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("\"\""), sameInstance(string("")));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesANull(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("null"), equalTo(nullNode()));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesATrue(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("true"), equalTo(trueNode()));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesAFalse(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("false"), equalTo(falseNode()));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesAnArray(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("[]"), equalTo(array()));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesAnObject(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("{\"foo\": \"bar\"}"), equalTo(object(field("foo", string("bar")))));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesNumberBetweenZeroAndOne(final JdomParserJsonParserShim parser) throws Exception {
        parser.parse("{\"value\": 0.6}");
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesNumberWithALowerCaseExponent(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("{ \"PI\":3.141e-10}").getNumberValue("PI"), equalTo("3.141e-10"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesAnUnescapedForwardsSlash(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("{ \"a\":\"hp://foo\"}").getStringValue("a"), equalTo("hp://foo"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesSomeUnicodeStuff(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("{ \"v\":\"\\u2000\\u20ff\"}").getStringValue("v"), equalTo("\u2000\u20ff"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesEscapedStuff(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("{ \"v\":\"\\\" \\\\ \\b \\t \\n \\r \\f\"}").getStringValue("v"), equalTo("\" \\ \b \t \n \r \f"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesEcmaForwardSlashExamples(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("[\"\\u002F\"]").getStringValue(0), equalTo("/"));
        assertThat(parser.parse("[\"\\u002f\"]").getStringValue(0), equalTo("/"));
        assertThat(parser.parse("[\"\\/\"]").getStringValue(0), equalTo("/"));
        assertThat(parser.parse("[\"/\"]").getStringValue(0), equalTo("/"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesEcmaUtf16SurrogatePairExample(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("[\"\\uD834\\uDD1E\"]").getStringValue(0), equalTo("\ud834\udd1e"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesMismatchedDoubleQuotesInAnArray(final JdomParserJsonParserShim parser) {
        assertThrows(InvalidSyntaxException.class, () -> parser.parse("{\"}"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsesMismatchedDoubleQuotesInAnObject(final JdomParserJsonParserShim parser) {
        assertThrows(InvalidSyntaxException.class, () -> parser.parse("{\"a\":\"b}"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rethrowsIOExceptionFromReader(final JdomParserJsonParserShim parser) {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> parser.parse(new BrokenReader(ioException)));
        assertThat(actualException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rethrowsIOExceptionFromPartWayThroughFieldName(final JdomParserJsonParserShim parser) {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> parser.parse(new SequenceReader(
                new StringReader("{\"He"),
                new BrokenReader(ioException)
        )));
        assertThat(actualException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rethrowsIOExceptionFromPartWayThroughString(final JdomParserJsonParserShim parser) {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> parser.parse(new SequenceReader(
                new StringReader("\"He"),
                new BrokenReader(ioException)
        )));
        assertThat(actualException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rethrowsIOExceptionFromPartWayThroughNumber(final JdomParserJsonParserShim parser) {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> parser.parse(new SequenceReader(
                new StringReader("1."),
                new BrokenReader(ioException)
        )));
        assertThat(actualException, sameInstance(ioException));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsingInvalidJsonThrowsInvalidSyntaxExceptionFromReader(final JdomParserJsonParserShim parser) {
        assertThrows(InvalidSyntaxException.class, () -> parser.parse(new StringReader("not json")));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canParseCharacterZero(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("[\"\u0000\"]").getStringValue(0), equalTo("\u0000"));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void canParseCharacterMinusOne(final JdomParserJsonParserShim parser) throws Exception {
        assertThat(parser.parse("[\"" + ((char) -1) + "\"]").getStringValue(0), equalTo(String.valueOf((char) -1)));
    }

    /**
     * This test exposes a bug in PositionTrackingPushbackReader where it doesn't call Reader.read(chars, ...) in the intended manner.
     *    (When read's return value != supplied buffer's length, caller should keep calling!)
     * Reader.read(...) may return 'some input', but not enough to fill the supplied buffer.
     * In that case the consumer is expected to call again to fill the supplied buffer.
     * When testing with Readers backed by readily available data (ie a local file), this seems to not happen.
     * However, when reading from Readers backed by a network buffer, we've seen it happen.
     *
     * @author Henrik Sjöstrand
     */
    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void whenReaderReturnsSomeThenReadMore(final JdomParserJsonParserShim parser) throws Exception {
        final JsonNode jsonNode = parser.parse(new ChoppingReader(new StringReader("{\"nullField\":null}")));
        final String result = JsonNodeSelectors.aNullableStringNode("nullField").getValue(jsonNode);
        assertThat(result, equalTo(null));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void equalKeysInTheSameDocumentReferToTheSameObject(final JdomParserJsonParserShim parser) throws Exception {
        final JsonNode jsonNode = parser.parse("[{\"value\": 0.6}, {\"value\": 0.6}]");
        assertThat(jsonNode.getNode(0).getFieldList().get(0).getName(), sameInstance(jsonNode.getNode(1).getFieldList().get(0).getName()));
    }


    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void equalStringsInTheSameDocumentReferToTheSameObject(final JdomParserJsonParserShim parser) throws Exception {
        final JsonNode jsonNode = parser.parse("[\"foo\", \"foo\"]");
        assertThat(jsonNode.getNode(0), sameInstance(jsonNode.getNode(1)));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void equalNumbersInTheSameDocumentReferToTheSameObject(final JdomParserJsonParserShim parser) throws Exception {
        final JsonNode jsonNode = parser.parse("[123, 123]");
        assertThat(jsonNode.getNode(0), sameInstance(jsonNode.getNode(1)));
    }


}
