/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package samples;

import argo.format.*;
import argo.jdom.*;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.JsonStreamElement;
import argo.staj.JsonStreamElementType;
import argo.staj.StajParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static argo.jdom.JsonNodeBuilders.*;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeSelectors.aStringNode;
import static argo.jdom.JsonNodeSelectors.anArrayNode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newInputStream;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

final class MainDocumentationExamplesTest {

    private static final JsonNode SAMPLE_JSON = object(
            field("name", string("Black Lace")),
            field("sales", number("110921")),
            field("totalRoyalties", number("10223.82")),
            field("singles", array(
                    string("Superman"),
                    string("Agadoo")
            ))
    );

    private static final JdomParser JDOM_PARSER = new JdomParser();

    private static final SajParser SAJ_PARSER
            = new SajParser();

    @Test
    void producesJsonFromFactory() {
        // tag::jsonNodeFactory[]
        JsonNode json = object(
                field("name", string("Black Lace")),
                field("sales", number("110921")),
                field("totalRoyalties", number("10223.82")),
                field("singles", array(
                        string("Superman"),
                        string("Agadoo")
                ))
        );
        // end::jsonNodeFactory[]
        assertThat(json, equalTo(SAMPLE_JSON));
    }

    @Test
    void producesJsonFromBuilder() {
        // tag::jsonNodeBuilder[]
        JsonObjectNodeBuilder builder = anObjectBuilder()
                .withField("name", aStringBuilder("Black Lace"))
                .withField("sales", aNumberBuilder("110921"))
                .withField("totalRoyalties", aNumberBuilder("10223.82"))
                .withField("singles", anArrayBuilder()
                        .withElement(aStringBuilder("Superman"))
                        .withElement(aStringBuilder("Agadoo"))
                );
        JsonNode json = builder.build();
        // end::jsonNodeBuilder[]
        assertThat(json, equalTo(SAMPLE_JSON));
    }

    @SuppressWarnings("deprecation")
    @Test
    void producesInfiniteSequenceOfJson() throws Exception {
        // tag::unboundedArrayWriting[]
        StringWriter stringWriter = new StringWriter();
        new PrettyJsonWriter().write(stringWriter, (WriteableJsonArray) arrayWriter -> {
            for (int i = 0; i < 10_000; i++) {
                arrayWriter.writeElement(string("I'm Spartacus!"));
            }
        });
        String jsonText = stringWriter.toString();
        // end::unboundedArrayWriting[]
        assertThat(jsonText, Matchers.startsWith("[\n\t\"I'm Spartacus!"));
    }

    @SuppressWarnings("deprecation")
    @Test
    void producesInfiniteStringOfJson() throws Exception {
        // tag::unboundedStringWriting[]
        StringWriter stringWriter = new StringWriter();
        new PrettyJsonWriter().write(stringWriter, (WriteableJsonString) writer -> {
            writer.write("On");
            for (int i = 0; i < 10_000; i++) {
                writer.write(" and on");
            }
        });
        String jsonText = stringWriter.toString();
        // end::unboundedStringWriting[]
        assertThat(jsonText, Matchers.startsWith("\"On and on and on"));
    }

    @SuppressWarnings("deprecation")
    @Test
    void formatsJson() throws Exception {
        @SuppressWarnings("UnnecessaryLocalVariable") JsonNode json = SAMPLE_JSON;
        // tag::jsonFormatter[]
        String jsonText = new PrettyJsonFormatter().format(json);
        // end::jsonFormatter[]
        assertThat(JDOM_PARSER.parse(jsonText), equalTo(SAMPLE_JSON));
    }

    @Test
    void parsesJsonAndGetsElementsWithCallToJsonNodeInline() throws Exception {
        final String jsonText = readFileToString(Utilities.getExampleJsonFile(), UTF_8);
        // tag::jdomPath[]
        String secondSingle = new JdomParser().parse(jsonText).getStringValue("singles", 1);
        // end::jdomPath[]
        assertThat(secondSingle, equalTo("Agadoo"));
    }

    @Test
    void parsesJsonAndGetsElementsWithCallToJsonNode() throws Exception {
        final String jsonText = readFileToString(Utilities.getExampleJsonFile(), UTF_8);
        // tag::jdomParser[]
        JsonNode json = new JdomParser().parse(jsonText);
        // end::jdomParser[]
        // tag::jdomPathExpanded[]
        String secondSingle = json.getStringValue("singles", 1);
        // end::jdomPathExpanded[]
        assertThat(secondSingle, equalTo("Agadoo"));
        // tag::jdomPathTypeCheck[]
        boolean isString = json.isStringValue("singles", 1);
        // end::jdomPathTypeCheck[]
        assertThat(isString, equalTo(true));
    }

    @Test
    void parsesJsonAndGetsElementsWithJsonNodeSelector() throws Exception {
        final String jsonText = readFileToString(Utilities.getExampleJsonFile(), UTF_8);
        final JsonNode json = JDOM_PARSER.parse(jsonText);
        // tag::jsonNodeSelectorPath[]
        String secondSingle = JsonNodeSelectors.aStringNode("singles", 1).getValue(json);
        // end::jsonNodeSelectorPath[]
        // tag::jsonNodeSelectorListString[]
        JsonNodeSelector<JsonNode, List<JsonNode>> singlesSelector = anArrayNode("singles");
        JsonNodeSelector<JsonNode, String> singleNameSelector = aStringNode();
        List<String> singles = new AbstractList<String>() {
            public String get(int index) {
                return singleNameSelector.getValue(singlesSelector.getValue(json).get(index));
            }

            public int size() {
                return singlesSelector.getValue(json).size();
            }
        };
        // end::jsonNodeSelectorListString[]
        // tag::jsonNumberParsing[]
        BigDecimal totalRoyalties = JsonNumberUtils.asBigDecimal(json.getNumberValue("totalRoyalties"));
        // end::jsonNumberParsing[]
        assertThat(secondSingle, equalTo("Agadoo"));
        assertThat(singles, equalTo(asList("Superman", "Agadoo")));
        assertThat(totalRoyalties, equalTo(new BigDecimal("10223.82")));
    }

    @Test
    void parsesUsingSaj() throws Exception {
        try (Reader jsonReader = newBufferedReader(Utilities.getExampleJsonFile().toPath(), UTF_8)) {
            // tag::jsonThroughEvents[]
            Set<String> fieldNames = new HashSet<>();
            SAJ_PARSER.parse(jsonReader, new JsonListener() {
                public void startField(Reader name) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int next;
                    try {
                        while ((next = name.read()) != -1) {
                            stringBuilder.append((char) next);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    fieldNames.add(stringBuilder.toString());
                }

                public void startDocument() {
                }

                public void endDocument() {
                }

                public void startArray() {
                }

                public void endArray() {
                }

                public void startObject() {
                }

                public void endObject() {
                }

                public void endField() {
                }

                public void stringValue(Reader value) {
                }

                public void numberValue(Reader value) {
                }

                public void trueValue() {
                }

                public void falseValue() {
                }

                public void nullValue() {
                }
            });
            // end::jsonThroughEvents[]
            assertThat(fieldNames, equalTo(new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
        }
    }

    @Test
    void parsesUsingStaj() throws Exception {
        try (Reader jsonReader = new InputStreamReader(newInputStream(Utilities.getExampleJsonFile().toPath()), UTF_8)) {
            // tag::jsonThroughIteration[]
            Set<String> fieldNames = new HashSet<>();
            StajParser stajParser = new StajParser(jsonReader);
            while (stajParser.hasNext()) {
                JsonStreamElement next = stajParser.next();
                if (next.jsonStreamElementType() == JsonStreamElementType.START_FIELD) {
                    StringBuilder stringBuilder = new StringBuilder();
                    try (Reader fieldNameReader = next.reader()) {
                        int nextChar;
                        while ((nextChar = fieldNameReader.read()) != -1) {
                            stringBuilder.append((char) nextChar);
                        }
                    }
                    fieldNames.add(stringBuilder.toString());
                }
            }
            // end::jsonThroughIteration[]
            assertThat(fieldNames, equalTo(new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
        }
    }

}
