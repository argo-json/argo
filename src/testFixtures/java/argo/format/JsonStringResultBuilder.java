/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import org.apache.commons.io.output.StringBuilderWriter;

import java.io.PrintWriter;

final class JsonStringResultBuilder {

    private final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
    private final PrintWriter printWriter = new PrintWriter(stringBuilderWriter);

    private JsonStringResultBuilder() {
    }

    static JsonStringResultBuilder aJsonStringResultBuilder() {
        return new JsonStringResultBuilder();
    }

    JsonStringResultBuilder printLine(final String text) {
        printWriter.println(text);
        return this;
    }

    JsonStringResultBuilder print(final String text) {
        printWriter.print(text);
        return this;
    }

    String build() {
        printWriter.close();
        return stringBuilderWriter.toString();
    }
}
