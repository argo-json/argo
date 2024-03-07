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

import argo.jdom.JsonNode;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;

@SuppressWarnings("deprecation")
final class WriterJsonGeneratorJsonWriterTestCase implements JsonGeneratorJsonWriterTestCase {
    private final JsonWriter jsonWriter;

    WriterJsonGeneratorJsonWriterTestCase(final JsonWriter jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    @Override
    public String write(final WriteableJsonArray writeableJsonArray) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            jsonWriter.write(stringBuilderWriter, writeableJsonArray);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    @Override
    public String write(final WriteableJsonObject writeableJsonObject) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            jsonWriter.write(stringBuilderWriter, writeableJsonObject);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    @Override
    public String write(final WriteableJsonString writeableJsonString) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            jsonWriter.write(stringBuilderWriter, writeableJsonString);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    @Override
    public String write(final WriteableJsonNumber writeableJsonNumber) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            jsonWriter.write(stringBuilderWriter, writeableJsonNumber);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    @Override
    public String write(final JsonNode jsonNode) throws IOException {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            jsonWriter.write(stringBuilderWriter, jsonNode);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }

    @Override
    public String toString() {
        return "WriterTestCase{" +
                "jsonWriter=" + jsonWriter +
                '}';
    }
}
