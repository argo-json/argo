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

import argo.format.ArrayWriter;
import argo.format.WriteableJsonArray;
import argo.format.WriteableJsonObject;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.logging.Logger;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.jdom.JsonNodeFactories.number;
import static argo.jdom.JsonNodeFactories.string;

class LimitationsTest {

    private static final Logger LOGGER = Logger.getLogger(LimitationsTest.class.getName());
    private static final BlackHoleJsonListener BLACK_HOLE_JSON_LISTENER = new BlackHoleJsonListener(reader -> {
    });
    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(COMPACT);

    private static void executeTest(final Generator generator, final Parser parser) throws IOException, InterruptedException, InvalidSyntaxException {
        try (PipedReader pipedReader = new PipedReader(); Reader reader = new BufferedReader(pipedReader); Writer pipedWriter = new PipedWriter(pipedReader)) {
            final Thread thread = new Thread(() -> {
                try (Writer writer = new BufferedWriter(pipedWriter)) {
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

    @FunctionalInterface
    private interface Generator {
        void generate(Writer writer) throws IOException;
    }

    private interface Parser {
        void parse(Reader reader) throws IOException, InvalidSyntaxException;
    }

    @Test
    @Disabled
    void parseStreamingArray() throws IOException, InterruptedException, InvalidSyntaxException {
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
    @Disabled
    void parseStreamingObject() throws IOException, InterruptedException, InvalidSyntaxException {
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
    @Disabled
    void parseStreamingNested() throws IOException, InterruptedException, InvalidSyntaxException {
        executeTest(writer -> {
            final int max = Integer.MAX_VALUE - 9;
            final int percentile = Integer.MAX_VALUE / 100;
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
    @Disabled
    void generateStreamingNested() throws IOException, InterruptedException, InvalidSyntaxException {
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
}
