/*
 * Copyright 2015 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class JsonEscapedStringTest {

    @Test
    public void formatsReverseSolidusAsEscapedReverseSolidus() throws Exception {
        assertThat(JsonEscapedString.escapeString("\\"), equalTo("\\\\"));
    }

    @Test
    public void formatsDoubleQuoteAsEscapedDoubleQuote() throws Exception {
        assertThat(JsonEscapedString.escapeString("\""), equalTo("\\\""));
    }

    @Test
    public void formatsControlCharactersAsEscapedUnicodeCharacters() throws Exception {
        assertThat(JsonEscapedString.escapeString("\u0000"), equalTo("\\u0000"));
        assertThat(JsonEscapedString.escapeString("\u0001"), equalTo("\\u0001"));
        assertThat(JsonEscapedString.escapeString("\u0002"), equalTo("\\u0002"));
        assertThat(JsonEscapedString.escapeString("\u0003"), equalTo("\\u0003"));
        assertThat(JsonEscapedString.escapeString("\u0004"), equalTo("\\u0004"));
        assertThat(JsonEscapedString.escapeString("\u0005"), equalTo("\\u0005"));
        assertThat(JsonEscapedString.escapeString("\u0006"), equalTo("\\u0006"));
        assertThat(JsonEscapedString.escapeString("\u0007"), equalTo("\\u0007"));
        assertThat(JsonEscapedString.escapeString("\u0008"), equalTo("\\b"));
        assertThat(JsonEscapedString.escapeString("\u0009"), equalTo("\\t"));
        assertThat(JsonEscapedString.escapeString("\n"), equalTo("\\n"));
        assertThat(JsonEscapedString.escapeString("\u000b"), equalTo("\\u000b"));
        assertThat(JsonEscapedString.escapeString("\u000c"), equalTo("\\f"));
        assertThat(JsonEscapedString.escapeString("\r"), equalTo("\\r"));
        assertThat(JsonEscapedString.escapeString("\u000e"), equalTo("\\u000e"));
        assertThat(JsonEscapedString.escapeString("\u000f"), equalTo("\\u000f"));
        assertThat(JsonEscapedString.escapeString("\u0010"), equalTo("\\u0010"));
        assertThat(JsonEscapedString.escapeString("\u0011"), equalTo("\\u0011"));
        assertThat(JsonEscapedString.escapeString("\u0012"), equalTo("\\u0012"));
        assertThat(JsonEscapedString.escapeString("\u0013"), equalTo("\\u0013"));
        assertThat(JsonEscapedString.escapeString("\u0014"), equalTo("\\u0014"));
        assertThat(JsonEscapedString.escapeString("\u0015"), equalTo("\\u0015"));
        assertThat(JsonEscapedString.escapeString("\u0016"), equalTo("\\u0016"));
        assertThat(JsonEscapedString.escapeString("\u0017"), equalTo("\\u0017"));
        assertThat(JsonEscapedString.escapeString("\u0018"), equalTo("\\u0018"));
        assertThat(JsonEscapedString.escapeString("\u0019"), equalTo("\\u0019"));
        assertThat(JsonEscapedString.escapeString("\u001a"), equalTo("\\u001a"));
        assertThat(JsonEscapedString.escapeString("\u001b"), equalTo("\\u001b"));
        assertThat(JsonEscapedString.escapeString("\u001c"), equalTo("\\u001c"));
        assertThat(JsonEscapedString.escapeString("\u001d"), equalTo("\\u001d"));
        assertThat(JsonEscapedString.escapeString("\u001e"), equalTo("\\u001e"));
        assertThat(JsonEscapedString.escapeString("\u001f"), equalTo("\\u001f"));
    }
}
