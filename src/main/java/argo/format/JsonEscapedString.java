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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class JsonEscapedString {

    private static final Pattern ESCAPE_CHARACTERS_PATTERN = Pattern.compile("(\\\\|\"|[\\u0000-\\u001f])");

    private JsonEscapedString() {
    }

    static String escapeString(final String unescapedString) {
        final Matcher matcher = ESCAPE_CHARACTERS_PATTERN.matcher(unescapedString);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String match = matcher.group();
            if ("\\".equals(match)) {
                matcher.appendReplacement(sb, "\\\\\\\\");
            } else if ("\"".equals(match)) {
                matcher.appendReplacement(sb, "\\\\\\\"");
            } else if ("\u0000".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0000");
            } else if ("\u0001".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0001");
            } else if ("\u0002".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0002");
            } else if ("\u0003".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0003");
            } else if ("\u0004".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0004");
            } else if ("\u0005".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0005");
            } else if ("\u0006".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0006");
            } else if ("\u0007".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0007");
            } else if ("\u0008".equals(match)) {
                matcher.appendReplacement(sb, "\\\\b");
            } else if ("\u0009".equals(match)) {
                matcher.appendReplacement(sb, "\\\\t");
            } else if ("\n".equals(match)) {
                matcher.appendReplacement(sb, "\\\\n");
            } else if ("\u000b".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u000b");
            } else if ("\u000c".equals(match)) {
                matcher.appendReplacement(sb, "\\\\f");
            } else if ("\r".equals(match)) {
                matcher.appendReplacement(sb, "\\\\r");
            } else if ("\u000e".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u000e");
            } else if ("\u000f".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u000f");
            } else if ("\u0010".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0010");
            } else if ("\u0011".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0011");
            } else if ("\u0012".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0012");
            } else if ("\u0013".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0013");
            } else if ("\u0014".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0014");
            } else if ("\u0015".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0015");
            } else if ("\u0016".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0016");
            } else if ("\u0017".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0017");
            } else if ("\u0018".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0018");
            } else if ("\u0019".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u0019");
            } else if ("\u001a".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001a");
            } else if ("\u001b".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001b");
            } else if ("\u001c".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001c");
            } else if ("\u001d".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001d");
            } else if ("\u001e".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001e");
            } else if ("\u001f".equals(match)) {
                matcher.appendReplacement(sb, "\\\\u001f");
            } else {
                throw new RuntimeException("Coding failure in Argo: JSON String escaping matched a character [" + match + "] for which no replacement is defined.");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
