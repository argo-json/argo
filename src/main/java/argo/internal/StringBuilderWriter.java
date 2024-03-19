/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.internal;

import java.io.Writer;

public final class StringBuilderWriter extends Writer {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void write(@SuppressWarnings("NullableProblems") final char[] cbuf, final int off, final int len) {
        stringBuilder.append(cbuf, off, len);
    }

    @Override
    public void write(@SuppressWarnings("NullableProblems") final String str) {
        if (str == null) {
            throw new NullPointerException();
        } else {
            stringBuilder.append(str);
        }
    }

    @Override
    public void write(final int c) {
        stringBuilder.append((char) c);
    }

    @Override
    public void write(@SuppressWarnings("NullableProblems") final char[] cbuf) {
        stringBuilder.append(cbuf);
    }

    @Override
    public void write(@SuppressWarnings("NullableProblems") final String str, final int off, final int len) {
        if (str == null) {
            throw new NullPointerException();
        } else {
            stringBuilder.append(str, off, off + len);
        }

    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
