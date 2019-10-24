/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class JsonEscapedStringTest {

    @Test
    public void formatsReverseSolidusAsEscapedReverseSolidus() throws Exception {
        assertThat(escapeString("\\"), equalTo("\\\\"));
    }

    @Test
    public void formatsDoubleQuoteAsEscapedDoubleQuote() throws Exception {
        assertThat(escapeString("\""), equalTo("\\\""));
    }

    @Test
    public void formatsControlCharactersAsEscapedUnicodeCharacters() throws Exception {
        assertThat(escapeString("\u0000"), equalTo("\\u0000"));
        assertThat(escapeString("\u0001"), equalTo("\\u0001"));
        assertThat(escapeString("\u0002"), equalTo("\\u0002"));
        assertThat(escapeString("\u0003"), equalTo("\\u0003"));
        assertThat(escapeString("\u0004"), equalTo("\\u0004"));
        assertThat(escapeString("\u0005"), equalTo("\\u0005"));
        assertThat(escapeString("\u0006"), equalTo("\\u0006"));
        assertThat(escapeString("\u0007"), equalTo("\\u0007"));
        assertThat(escapeString("\u0008"), equalTo("\\b"));
        assertThat(escapeString("\u0009"), equalTo("\\t"));
        assertThat(escapeString("\n"), equalTo("\\n"));
        assertThat(escapeString("\u000b"), equalTo("\\u000b"));
        assertThat(escapeString("\u000c"), equalTo("\\f"));
        assertThat(escapeString("\r"), equalTo("\\r"));
        assertThat(escapeString("\u000e"), equalTo("\\u000e"));
        assertThat(escapeString("\u000f"), equalTo("\\u000f"));
        assertThat(escapeString("\u0010"), equalTo("\\u0010"));
        assertThat(escapeString("\u0011"), equalTo("\\u0011"));
        assertThat(escapeString("\u0012"), equalTo("\\u0012"));
        assertThat(escapeString("\u0013"), equalTo("\\u0013"));
        assertThat(escapeString("\u0014"), equalTo("\\u0014"));
        assertThat(escapeString("\u0015"), equalTo("\\u0015"));
        assertThat(escapeString("\u0016"), equalTo("\\u0016"));
        assertThat(escapeString("\u0017"), equalTo("\\u0017"));
        assertThat(escapeString("\u0018"), equalTo("\\u0018"));
        assertThat(escapeString("\u0019"), equalTo("\\u0019"));
        assertThat(escapeString("\u001a"), equalTo("\\u001a"));
        assertThat(escapeString("\u001b"), equalTo("\\u001b"));
        assertThat(escapeString("\u001c"), equalTo("\\u001c"));
        assertThat(escapeString("\u001d"), equalTo("\\u001d"));
        assertThat(escapeString("\u001e"), equalTo("\\u001e"));
        assertThat(escapeString("\u001f"), equalTo("\\u001f"));
    }

    private static String escapeString(final String unescapedString) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        JsonEscapedString.escapeStringTo(stringBuilderWriter, unescapedString);
        return stringBuilderWriter.toString();
    }
}
