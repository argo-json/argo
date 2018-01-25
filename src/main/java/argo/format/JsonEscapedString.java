/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import java.io.IOException;
import java.io.Writer;

final class JsonEscapedString {

    private JsonEscapedString() {
    }

    static void escapeStringTo(final Writer writer, final String unescapedString) throws IOException {
        final char[] chars = unescapedString.toCharArray();
        escapeStringTo(writer, chars, 0, chars.length);
    }

    static void escapeStringTo(final Writer writer, final char[] cbuf, final int offset, final int length) throws IOException {
        if (offset < 0 || offset > cbuf.length || length < 0 || offset + length > cbuf.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        } else if (length != 0) {
            for (int i = offset; i < length; i++) {
                switch (cbuf[i]) {
                    case '\u0000':
                        writer.append("\\u0000");
                        break;
                    case '\u0001':
                        writer.append("\\u0001");
                        break;
                    case '\u0002':
                        writer.append("\\u0002");
                        break;
                    case '\u0003':
                        writer.append("\\u0003");
                        break;
                    case '\u0004':
                        writer.append("\\u0004");
                        break;
                    case '\u0005':
                        writer.append("\\u0005");
                        break;
                    case '\u0006':
                        writer.append("\\u0006");
                        break;
                    case '\u0007':
                        writer.append("\\u0007");
                        break;
                    case '\u0008':
                        writer.append("\\b");
                        break;
                    case '\u0009':
                        writer.append("\\t");
                        break;
                    case '\n':
                        writer.append("\\n");
                        break;
                    case '\u000b':
                        writer.append("\\u000b");
                        break;
                    case '\u000c':
                        writer.append("\\f");
                        break;
                    case '\r':
                        writer.append("\\r");
                        break;
                    case '\u000e':
                        writer.append("\\u000e");
                        break;
                    case '\u000f':
                        writer.append("\\u000f");
                        break;
                    case '\u0010':
                        writer.append("\\u0010");
                        break;
                    case '\u0011':
                        writer.append("\\u0011");
                        break;
                    case '\u0012':
                        writer.append("\\u0012");
                        break;
                    case '\u0013':
                        writer.append("\\u0013");
                        break;
                    case '\u0014':
                        writer.append("\\u0014");
                        break;
                    case '\u0015':
                        writer.append("\\u0015");
                        break;
                    case '\u0016':
                        writer.append("\\u0016");
                        break;
                    case '\u0017':
                        writer.append("\\u0017");
                        break;
                    case '\u0018':
                        writer.append("\\u0018");
                        break;
                    case '\u0019':
                        writer.append("\\u0019");
                        break;
                    case '\u001a':
                        writer.append("\\u001a");
                        break;
                    case '\u001b':
                        writer.append("\\u001b");
                        break;
                    case '\u001c':
                        writer.append("\\u001c");
                        break;
                    case '\u001d':
                        writer.append("\\u001d");
                        break;
                    case '\u001e':
                        writer.append("\\u001e");
                        break;
                    case '\u001f':
                        writer.append("\\u001f");
                        break;
                    case '\\':
                        writer.append("\\\\");
                        break;
                    case '\"':
                        writer.append("\\\"");
                        break;
                    default:
                        writer.append(cbuf[i]);
                }
            }
        }
    }

}
