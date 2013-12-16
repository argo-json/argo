/*
 * Copyright 2013 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import argo.saj.InvalidSyntaxException;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
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
    @Ignore("Not implemented yet")
    public void canParseCharacterMinusOne() throws Exception {
        assertThat(new JdomParser().parse("[\"" + (char) -1 + "\"]").getStringValue(0), equalTo(String.valueOf((char) -1)));
    }
}
