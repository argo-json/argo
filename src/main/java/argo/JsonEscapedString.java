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

import java.io.IOException;
import java.io.Writer;

final class JsonEscapedString {

    private JsonEscapedString() {
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NcssCount"})
    static void escapeCharBufferTo(final Writer writer, final char[] cbuf, final int offset, final int length) throws IOException {
        if (offset < 0 || offset > cbuf.length || length < 0 || offset + length > cbuf.length || offset + length < 0) {
            throw new IndexOutOfBoundsException();
        } else if (writer == null) {
            throw new NullPointerException("writer");
        } else if (length != 0) {
            final int end = offset + length;
            int writtenOffset = offset;

            for (int i = offset; i < end; i++) {
                final char c = cbuf[i];

                if (c < 32 || c == '\\' || c == '\"') {
                    writer.write(cbuf, writtenOffset, i - writtenOffset);
                    switch (c) {
                        case '\u0000':
                            writer.write("\\u0000");
                            break;
                        case '\u0001':
                            writer.write("\\u0001");
                            break;
                        case '\u0002':
                            writer.write("\\u0002");
                            break;
                        case '\u0003':
                            writer.write("\\u0003");
                            break;
                        case '\u0004':
                            writer.write("\\u0004");
                            break;
                        case '\u0005':
                            writer.write("\\u0005");
                            break;
                        case '\u0006':
                            writer.write("\\u0006");
                            break;
                        case '\u0007':
                            writer.write("\\u0007");
                            break;
                        case '\u0008':
                            writer.write("\\b");
                            break;
                        case '\t':
                            writer.write("\\t");
                            break;
                        case '\n':
                            writer.write("\\n");
                            break;
                        case '\u000b':
                            writer.write("\\u000b");
                            break;
                        case '\u000c':
                            writer.write("\\f");
                            break;
                        case '\r':
                            writer.write("\\r");
                            break;
                        case '\u000e':
                            writer.write("\\u000e");
                            break;
                        case '\u000f':
                            writer.write("\\u000f");
                            break;
                        case '\u0010':
                            writer.write("\\u0010");
                            break;
                        case '\u0011':
                            writer.write("\\u0011");
                            break;
                        case '\u0012':
                            writer.write("\\u0012");
                            break;
                        case '\u0013':
                            writer.write("\\u0013");
                            break;
                        case '\u0014':
                            writer.write("\\u0014");
                            break;
                        case '\u0015':
                            writer.write("\\u0015");
                            break;
                        case '\u0016':
                            writer.write("\\u0016");
                            break;
                        case '\u0017':
                            writer.write("\\u0017");
                            break;
                        case '\u0018':
                            writer.write("\\u0018");
                            break;
                        case '\u0019':
                            writer.write("\\u0019");
                            break;
                        case '\u001a':
                            writer.write("\\u001a");
                            break;
                        case '\u001b':
                            writer.write("\\u001b");
                            break;
                        case '\u001c':
                            writer.write("\\u001c");
                            break;
                        case '\u001d':
                            writer.write("\\u001d");
                            break;
                        case '\u001e':
                            writer.write("\\u001e");
                            break;
                        case '\u001f':
                            writer.write("\\u001f");
                            break;
                        case '\\':
                            writer.write("\\\\");
                            break;
                        case '\"':
                            writer.write("\\\"");
                            break;
                        default:
                            throw new RuntimeException("Coding failure in Argo:  Escaping a character that doesn't need escaping: " + c);
                    }

                    writtenOffset = i + 1;
                }

            }
            writer.write(cbuf, writtenOffset, end - writtenOffset);
        }
    }

}
