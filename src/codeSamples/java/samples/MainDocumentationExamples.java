/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package samples;

import argo.format.*;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeSelector;
import argo.jdom.JsonObjectNodeBuilder;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.JsonStreamElement;
import argo.staj.JsonStreamElementType;
import argo.staj.StajParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static argo.format.JsonNumberUtils.asBigDecimal;
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

final class MainDocumentationExamples {

    private static final JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();

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
    private static final JsonNodeSelector<JsonNode, String> SECOND_SINGLE
            = aStringNode("singles", 1);

    private static final JsonNodeSelector<JsonNode, List<JsonNode>> SINGLES
            = anArrayNode("singles");
    private static final JsonNodeSelector<JsonNode, String> SINGLE_NAME
            = aStringNode();
    private static final SajParser SAJ_PARSER
            = new SajParser();
    private static final JsonWriter JSON_WRITER = new PrettyJsonWriter();

    @Test
    void producesJsonFromFactory() {
        JsonNode json = object(
                field("name", string("Black Lace")),
                field("sales", number("110921")),
                field("totalRoyalties", number("10223.82")),
                field("singles", array(
                        string("Superman"),
                        string("Agadoo")
                ))
        );
        assertThat(json, equalTo(SAMPLE_JSON));
    }

    @Test
    void producesJsonFromBuilder() {
        JsonObjectNodeBuilder builder = anObjectBuilder()
                .withField("name", aStringBuilder("Black Lace"))
                .withField("sales", aNumberBuilder("110921"))
                .withField("totalRoyalties", aNumberBuilder("10223.82"))
                .withField("singles", anArrayBuilder()
                        .withElement(aStringBuilder("Superman"))
                        .withElement(aStringBuilder("Agadoo"))
                );
        JsonNode json = builder.build();
        assertThat(json, equalTo(SAMPLE_JSON));
    }

    @Test
    void producesInfiniteSequenceOfJson() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        JSON_WRITER.write(stringWriter, (WriteableJsonArray) arrayWriter -> {
            for (int i = 0; i < 10_000; i++) {
                arrayWriter.writeElement(string("I'm Spartacus!"));
            }
        });
        String jsonText = stringWriter.toString();
        assertThat(jsonText, Matchers.startsWith("[\n\t\"I'm Spartacus!"));
    }

    @Test
    void producesInfiniteStringOfJson() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        JSON_WRITER.write(stringWriter, (WriteableJsonString) writer -> {
            writer.write("On");
            for (int i = 0; i < 10_000; i++) {
                writer.write(" and on");
            }
        });
        String jsonText = stringWriter.toString();
        assertThat(jsonText, Matchers.startsWith("\"On and on and on"));
    }

    @Test
    void formatsJson() throws Exception {
        String jsonText = JSON_FORMATTER.format(SAMPLE_JSON);
        assertThat(JDOM_PARSER.parse(jsonText), equalTo(SAMPLE_JSON));
    }

    @Test
    void parsesJsonAndGetsElementsWithCallToJsonNode() throws Exception {
        final String jsonText = readFileToString(new File(this.getClass().getResource("SimpleExample.json").getFile()), UTF_8);
        final JsonNode json = JDOM_PARSER.parse(jsonText);
        String secondSingle = json.getStringValue("singles", 1);
        assertThat(secondSingle, equalTo("Agadoo"));
        boolean isString = json.isStringValue("singles", 1);
        assertThat(isString, equalTo(true));
    }

    @Test
    void parsesJsonAndGetsElementsWithJsonNodeSelector() throws Exception {
        final String jsonText = readFileToString(new File(this.getClass().getResource("SimpleExample.json").getFile()), UTF_8);
        final JsonNode json = JDOM_PARSER.parse(jsonText);
        String secondSingle = SECOND_SINGLE.getValue(json);
        List<String> singles = new AbstractList<String>() {
            public String get(int index) {
                return SINGLE_NAME.getValue(SINGLES.getValue(json).get(index));
            }

            public int size() {
                return SINGLES.getValue(json).size();
            }
        };
        BigDecimal totalRoyalties = asBigDecimal(json.getNumberValue("totalRoyalties"));
        assertThat(secondSingle, equalTo("Agadoo"));
        assertThat(singles, equalTo(asList("Superman", "Agadoo")));
        assertThat(totalRoyalties, equalTo(new BigDecimal("10223.82")));
    }

    @Test
    void parsesUsingSaj() throws Exception {
        final Reader jsonReader = newBufferedReader(new File(this.getClass().getResource("SimpleExample.json").getFile()).toPath(), UTF_8);
        try {
            final Set<String> fieldNames = new HashSet<>();
            SAJ_PARSER.parse(jsonReader, new JsonListener() {
                public void startField(Reader name) {
                    fieldNames.add(JsonStreamElement.asString(name));
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

                public void stringValue(final Reader value) {
                }

                public void numberValue(final Reader value) {
                }

                public void trueValue() {
                }

                public void falseValue() {
                }

                public void nullValue() {
                }
            });
            assertThat(fieldNames, equalTo(new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
        } finally {
            jsonReader.close();
        }
    }

    @Test
    void parsesUsingStaj() throws Exception {
        final Reader jsonReader = new InputStreamReader(newInputStream(new File(this.getClass().getResource("SimpleExample.json").getFile()).toPath()), UTF_8);
        try {
            Set<String> fieldNames = new HashSet<>();
            final StajParser stajParser = new StajParser(jsonReader);
            while (stajParser.hasNext()) {
                JsonStreamElement next = stajParser.next();
                if (next.jsonStreamElementType() == JsonStreamElementType.START_FIELD) {
                    fieldNames.add(JsonStreamElement.asString(next.reader()));
                }
            }
            assertThat(fieldNames, equalTo(new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
        } finally {
            jsonReader.close();
        }
    }
}
