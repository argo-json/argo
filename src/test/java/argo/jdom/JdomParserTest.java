/*
 * Copyright 2019 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.saj.InvalidSyntaxException;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public final class JdomParserTest {

    @Test
    public void parsesNumberBetweenZeroAndOne() throws Exception {
        new JdomParser().parse("{\"value\": 0.6}");
    }

    @Test
    public void parsesNumberWithALowerCaseExponent() throws Exception {
        assertThat(new JdomParser().parse("{ \"PI\":3.141e-10}").getNumberValue("PI"), equalTo("3.141e-10"));
    }

    @Test
    public void parsesAnUnescapedForwardsSlash() throws Exception {
        assertThat(new JdomParser().parse("{ \"a\":\"hp://foo\"}").getStringValue("a"), equalTo("hp://foo"));
    }

    @Test
    public void parsesSomeUnicodeStuff() throws Exception {
        assertThat(new JdomParser().parse("{ \"v\":\"\\u2000\\u20ff\"}").getStringValue("v"), equalTo("\u2000\u20ff"));
    }

    @Test
    public void parsesEscapedStuff() throws Exception {
        assertThat(new JdomParser().parse("{ \"v\":\"\\\" \\\\ \\b \\t \\n \\r \\f\"}").getStringValue("v"), equalTo("\" \\ \b \t \n \r \f"));
    }

    @Test
    public void parsesEcmaForwardSlashExamples() throws Exception {
        assertThat(new JdomParser().parse("[\"\\u002F\"]").getStringValue(0), equalTo("/"));
        assertThat(new JdomParser().parse("[\"\\u002f\"]").getStringValue(0), equalTo("/"));
        assertThat(new JdomParser().parse("[\"\\/\"]").getStringValue(0), equalTo("/"));
        assertThat(new JdomParser().parse("[\"/\"]").getStringValue(0), equalTo("/"));
    }

    @Test
    public void parsesEcmaUtf16SurrogatePairExample() throws Exception {
        assertThat(new JdomParser().parse("[\"\\uD834\\uDD1E\"]").getStringValue(0), equalTo("\ud834\udd1e"));
    }

    @Test(expected = InvalidSyntaxException.class)
    public void parsesMismatchedDoubleQuotesInAnArray() throws Exception {
        new JdomParser().parse("{\"}");
    }

    @Test(expected = InvalidSyntaxException.class)
    public void parsesMismatchedDoubleQuotesInAnObject() throws Exception {
        new JdomParser().parse("{\"a\":\"b}");
    }

    @Test
    public void canParseCharacterZero() throws Exception {
        assertThat(new JdomParser().parse("[\"\u0000\"]").getStringValue(0), equalTo("\u0000"));
    }

    @Test
    public void canParseCharacterMinusOne() throws Exception {
        assertThat(new JdomParser().parse("[\"" + ((char) -1) + "\"]").getStringValue(0), equalTo(String.valueOf((char) -1)));
    }

    @Test
    /**
     * This test exposes a bug in PositionTrackingPushbackReader where PTPR doesn't call Reader.read(cbuf, ...) in the intended manner.
     *    (When read's return value != supplied buffer's length, caller should keep calling!)
     * Reader.read(...) may return 'some input', but not enough to fill the supplied buffer.
     * In that case the consumer is expected to call again to fill the supplied buffer.
     * When testing with Readers backed by readily available data (ie a local file), this seems to not happen.
     * However, when reading from Readers backed by a network buffer, we've seen it happen.
     *
     * @author Henrik Sjöstrand
     */
    public void whenReaderReturnsSome_thenReadMore() throws Exception {
        final JsonNode jsonNode = new JdomParser().parse(new ChoppingReader(new StringReader("{\"nullField\":null}")));
        final String result = JsonNodeSelectors.aNullableStringNode("nullField").getValue(jsonNode);
        assertThat(result, equalTo(null));
    }

    /**
     * Implementation of Reader that wraps underlying Reader, but forces Reader.read(...) to read at most 1 character.
     * This makes it useful for exposing the case when a 'some input' is available, but not enough to fill the supplied buffer.
     * It's expected of the Reader's consumer to keep calling read, until the supplied buffer is filled.
     *
     * @author Henrik Sjöstrand
     */
    private static final class ChoppingReader extends Reader {
        private final Reader reader;

        ChoppingReader(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return reader.read(cbuf, off, (len == 0) ? 0 : 1);
        }

        @Override
        public void close() throws IOException {
        }
    }

    @Test
    public void equalKeysInTheSameDocumentReferToTheSameObject() throws Exception {
        final JsonNode jsonNode = new JdomParser().parse("[{\"value\": 0.6}, {\"value\": 0.6}]");
        assertThat(jsonNode.getNode(0).getFieldList().get(0).getName(), sameInstance(jsonNode.getNode(1).getFieldList().get(0).getName()));
    }


    @Test
    public void equalStringsInTheSameDocumentReferToTheSameObject() throws Exception {
        final JsonNode jsonNode = new JdomParser().parse("[\"foo\", \"foo\"]");
        assertThat(jsonNode.getNode(0), sameInstance(jsonNode.getNode(1)));
    }

    @Test
    public void equalNumbersInTheSameDocumentReferToTheSameObject() throws Exception {
        final JsonNode jsonNode = new JdomParser().parse("[123, 123]");
        assertThat(jsonNode.getNode(0), sameInstance(jsonNode.getNode(1)));
    }


}
