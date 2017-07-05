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

final class JsonEscapedString {

    private JsonEscapedString() {
    }

    static String escapeString(final String unescapedString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < unescapedString.length(); i++) {
            switch (unescapedString.charAt(i)) {
                case '\u0000':
                    stringBuilder.append("\\u0000");
                    break;
                case '\u0001':
                    stringBuilder.append("\\u0001");
                    break;
                case '\u0002':
                    stringBuilder.append("\\u0002");
                    break;
                case '\u0003':
                    stringBuilder.append("\\u0003");
                    break;
                case '\u0004':
                    stringBuilder.append("\\u0004");
                    break;
                case '\u0005':
                    stringBuilder.append("\\u0005");
                    break;
                case '\u0006':
                    stringBuilder.append("\\u0006");
                    break;
                case '\u0007':
                    stringBuilder.append("\\u0007");
                    break;
                case '\u0008':
                    stringBuilder.append("\\b");
                    break;
                case '\u0009':
                    stringBuilder.append("\\t");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\u000b':
                    stringBuilder.append("\\u000b");
                    break;
                case '\u000c':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\u000e':
                    stringBuilder.append("\\u000e");
                    break;
                case '\u000f':
                    stringBuilder.append("\\u000f");
                    break;
                case '\u0010':
                    stringBuilder.append("\\u0010");
                    break;
                case '\u0011':
                    stringBuilder.append("\\u0011");
                    break;
                case '\u0012':
                    stringBuilder.append("\\u0012");
                    break;
                case '\u0013':
                    stringBuilder.append("\\u0013");
                    break;
                case '\u0014':
                    stringBuilder.append("\\u0014");
                    break;
                case '\u0015':
                    stringBuilder.append("\\u0015");
                    break;
                case '\u0016':
                    stringBuilder.append("\\u0016");
                    break;
                case '\u0017':
                    stringBuilder.append("\\u0017");
                    break;
                case '\u0018':
                    stringBuilder.append("\\u0018");
                    break;
                case '\u0019':
                    stringBuilder.append("\\u0019");
                    break;
                case '\u001a':
                    stringBuilder.append("\\u001a");
                    break;
                case '\u001b':
                    stringBuilder.append("\\u001b");
                    break;
                case '\u001c':
                    stringBuilder.append("\\u001c");
                    break;
                case '\u001d':
                    stringBuilder.append("\\u001d");
                    break;
                case '\u001e':
                    stringBuilder.append("\\u001e");
                    break;
                case '\u001f':
                    stringBuilder.append("\\u001f");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '\"':
                    stringBuilder.append("\\\"");
                    break;
                default:
                    stringBuilder.append(unescapedString.charAt(i));
            }
        }

        return stringBuilder.toString();
    }

}
