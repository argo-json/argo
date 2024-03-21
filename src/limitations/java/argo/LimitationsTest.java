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

import argo.format.*;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import org.apache.commons.io.output.NullWriter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.jdom.JsonNodeFactories.*;

class LimitationsTest {

    private static final Logger LOGGER = Logger.getLogger(LimitationsTest.class.getName());
    private static final BlackHoleJsonListener BLACK_HOLE_JSON_LISTENER = new BlackHoleJsonListener(reader -> {
    });
    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(COMPACT);

    private static void executeTest(final Generator generator, final Parser parser) throws IOException, InterruptedException, InvalidSyntaxException {
        try (PipedReader pipedReader = new PipedReader(); Reader reader = new BufferedReader(pipedReader, 1024 * 256); Writer pipedWriter = new PipedWriter(pipedReader)) {
            final Thread thread = new Thread(() -> {
                try (Writer writer = new BufferedWriter(pipedWriter, 1024 * 256)) {
                    generator.generate(writer);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            parser.parse(reader);
            thread.join();
        }
    }

    @Test
    @Disabled // checked
    void generateAndParseString() throws IOException, InterruptedException, InvalidSyntaxException {
        final StringBuilder stringBuilder = new StringBuilder(Integer.MAX_VALUE - 8);
        for (int i = 0; i < Integer.MAX_VALUE - 8; i++) {
            stringBuilder.append('a');
        }
        final JsonNode node = string(stringBuilder.toString());
        executeTest(writer -> JSON_GENERATOR.generate(writer, node), reader -> new JsonParser().parse(reader));
    }

    @Test
    @Disabled // checked
    void streamingGenerateAndStreamingParseString() throws IOException, InterruptedException, InvalidSyntaxException {
        final int max = Integer.MAX_VALUE;
        final int percentile = max / 100;
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonString) stringWriter -> {
            for (int i = -5; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                stringWriter.write('a');
            }
        }), reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER));
    }

    @Test
    @Disabled // checked
    void generateAndParseNumber() throws IOException, InterruptedException, InvalidSyntaxException {
        final StringBuilder stringBuilder = new StringBuilder(Integer.MAX_VALUE - 8);
        for (int i = 0; i < Integer.MAX_VALUE - 8; i++) {
            stringBuilder.append('1');
        }
        final JsonNode node = string(stringBuilder.toString());
        executeTest(writer -> JSON_GENERATOR.generate(writer, node), reader -> new JsonParser().parse(reader));
    }

    @Test
    @Disabled // checked
    void streamingGenerateAndStreamingParseNumber() throws IOException, InterruptedException, InvalidSyntaxException {
        final int max = Integer.MAX_VALUE;
        final int percentile = max / 100;
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonNumber) numberWriter -> {
            for (int i = -5; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                numberWriter.write('1');
            }
        }), reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER));
    }

    @Test
    @Disabled // checked
    void generateArray() throws IOException {
        final JsonNode number = number(0);
        final JsonNode jsonNode = array(Stream.generate(() -> number).limit(Integer.MAX_VALUE - 8).iterator());
        LOGGER.info("Made json node");
        JSON_GENERATOR.generate(NullWriter.INSTANCE, jsonNode);
    }

    @Test
    @Disabled // checked
    void parseArray() throws IOException, InterruptedException, InvalidSyntaxException {
        final int max = Integer.MAX_VALUE - 8;
        final int percentile = max / 100;
        final JsonNode number = number(0);
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonArray) arrayWriter -> {
            for (int i = 0; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                arrayWriter.writeElement(number);
            }
        }), reader -> new JsonParser().parse(reader));
    }

    @Test
    @Disabled // checked
    void streamingGenerateAndStreamingParseArray() throws IOException, InterruptedException, InvalidSyntaxException {
        final int max = Integer.MAX_VALUE;
        final int percentile = max / 100;
        final JsonNode number = number(0);
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonArray) arrayWriter -> {
            for (int i = -5; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                arrayWriter.writeElement(number);
            }
        }), reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER));
    }

    @Test
    @Disabled // checked
    void generateObject() throws IOException {
        final JsonField field = field(string("a"), number(0));
        final JsonNode jsonNode = object(Stream.generate(() -> field).limit(Integer.MAX_VALUE - 8).iterator());
        LOGGER.info("Made json node");
        JSON_GENERATOR.generate(NullWriter.INSTANCE, jsonNode);
    }

    @Test
    @Disabled // TODO test with big heap
    void parseObject() throws IOException, InterruptedException, InvalidSyntaxException {
        final int max = Integer.MAX_VALUE / 8;
        final int percentile = max / 100;
        final JsonField field = field(string("a"), number(0));
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonObject) objectWriter -> {
            for (int i = 0; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                objectWriter.writeField(field);
            }
        }), reader -> new JsonParser().parse(reader));
    }

    @Test
    @Disabled // checked
    void streamingGenerateAndStreamingParseObject() throws IOException, InterruptedException, InvalidSyntaxException {
        final JsonStringNode name = string("");
        final JsonNode number = number(0);
        final int max = Integer.MAX_VALUE;
        final int percentile = max / 100;
        executeTest(writer -> JSON_GENERATOR.generate(writer, (WriteableJsonObject) objectWriter -> {
            for (int i = -5; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Wrote " + (i / percentile) + "%");
                }
                objectWriter.writeField(name, number);
            }
        }), reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER));
    }

    @Test
    @Disabled // checked
    void generateNested() throws IOException {
        JsonNode jsonNode = array();
        for (int i = 1; i < 2900; i++) {
            jsonNode = array(jsonNode);
        }
        JSON_GENERATOR.generate(NullWriter.INSTANCE, jsonNode);
    }

    @Test
    @Disabled // checked
    void parseNested() throws IOException, InterruptedException, InvalidSyntaxException {
        executeTest(writer -> {
            final int max = 3000;
            for (int i = 0; i < max; i++) {
                writer.write('[');
            }
            for (int i = 0; i < max; i++) {
                writer.write(']');
            }
        }, reader -> new JsonParser().parse(reader));
    }

    @Test
    @Disabled  // checked
    void streamingParseNested() throws IOException, InterruptedException, InvalidSyntaxException {
        executeTest(writer -> {
            final int max = Integer.MAX_VALUE - 9; // outermost layer is always implicitly START_DOCUMENT
            final int percentile = max / 100;
            for (int i = 0; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Opened " + (i / percentile) + "%");
                }
                writer.write('[');
            }
            for (int i = 0; i < max; i++) {
                if (i % percentile == 0) {
                    LOGGER.info("Closed " + (i / percentile) + "%");
                }
                writer.write(']');
            }
        }, reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER));
    }

    @Test
    @Disabled  // checked
    void streamingGenerateNested() throws IOException, InterruptedException, InvalidSyntaxException {
        final WriteableJsonArray writeableJsonArray = new WriteableJsonArray() {
            private int callCount = 0;

            @Override
            public void writeTo(final ArrayWriter arrayWriter) throws IOException {
                callCount++;
                if (callCount < 3000) {
                    arrayWriter.writeElement(this);
                }
            }
        };
        executeTest(
                writer -> JSON_GENERATOR.generate(writer, writeableJsonArray),
                reader -> new JsonParser().parseStreaming(reader, BLACK_HOLE_JSON_LISTENER)
        );
    }

    @FunctionalInterface
    private interface Generator {
        void generate(Writer writer) throws IOException;
    }

    private interface Parser {
        void parse(Reader reader) throws IOException, InvalidSyntaxException;
    }
}
