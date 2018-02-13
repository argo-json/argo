/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import argo.format.*;
import argo.jdom.*;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.JsonStreamElement;
import argo.staj.JsonStreamElementType;
import argo.staj.StajParser;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.*;
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
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class MainDocumentationExamples {

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
            = JsonNodeSelectors.aStringNode("singles", 1);

    private static final JsonNodeSelector<JsonNode, List<JsonNode>> SINGLES
            = anArrayNode("singles");
    private static final JsonNodeSelector<JsonNode, String> SINGLE_NAME
            = aStringNode();
    private static final SajParser SAJ_PARSER
            = new SajParser();
    private static final JsonWriter JSON_WRITER = new PrettyJsonWriter();

    @Test
    public void producesJsonFromFactory() throws Exception {
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
    public void producesJsonFromBuilder() throws Exception {
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
    public void producesInfiniteSequenceOfJson() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        JSON_WRITER.write(stringWriter, new WriteableJsonArray() {
            @Override
            public void writeTo(ArrayWriter arrayWriter) throws IOException {
                for (int i = 0; i < 10000; i++) {
                    arrayWriter.writeElement(string("I'm Spartacus!"));
                }
            }
        });
        String jsonText = stringWriter.toString();
        assertThat(jsonText, Matchers.startsWith("[\n\t\"I'm Spartacus!"));
    }

    @Test
    public void producesInfiniteStringOfJson() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        JSON_WRITER.write(stringWriter, new WriteableJsonString() {
            @Override
            public void writeTo(Writer stringWriter) throws IOException {
                stringWriter.write("On");
                for (int i = 0; i < 10000; i++) {
                    stringWriter.write(" and on");
                }
            }
        });
        String jsonText = stringWriter.toString();
        assertThat(jsonText, Matchers.startsWith("\"On and on and on"));
    }

    @Test
    public void formatsJson() throws Exception {
        final JsonNode json = SAMPLE_JSON;
        String jsonText = JSON_FORMATTER.format(json);
        assertThat(JDOM_PARSER.parse(jsonText), equalTo(SAMPLE_JSON));
    }

    @Test
    public void parsesJsonAndGetsElementsWithCallToJsonNode() throws Exception {
        final String jsonText = readFileToString(new File(this.getClass().getResource("SimpleExample.json").getFile()), UTF_8);
        final JsonNode json = JDOM_PARSER.parse(jsonText);
        String secondSingle = json.getStringValue("singles", 1);
        assertThat(secondSingle, equalTo("Agadoo"));
        boolean isString = json.isStringValue("singles", 1);
        assertThat(isString, equalTo(true));
    }

    @Test
    public void parsesJsonAndGetsElementsWithJsonNodeSelector() throws Exception {
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
    public void parsesUsingSaj() throws Exception {
        final Reader jsonReader = new InputStreamReader(new FileInputStream(new File(this.getClass().getResource("SimpleExample.json").getFile())), UTF_8);
        final Set<String> fieldNames = new HashSet<>();
        SAJ_PARSER.parse(jsonReader, new JsonListener() {
            public void startField(String name) {
                fieldNames.add(name);
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

            public void stringValue(final String value) {
            }

            public void numberValue(final String value) {
            }

            public void trueValue() {
            }

            public void falseValue() {
            }

            public void nullValue() {
            }
        });
        assertThat(fieldNames, equalTo((Set<String>) new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
    }

    @Test
    public void parsesUsingStaj() throws Exception {
        final Reader jsonReader = new InputStreamReader(new FileInputStream(new File(this.getClass().getResource("SimpleExample.json").getFile())), UTF_8);
        Set<String> fieldNames = new HashSet<>();
        final StajParser stajParser = new StajParser(jsonReader);
        while (stajParser.hasNext()) {
            JsonStreamElement next = stajParser.next();
            if (next.jsonStreamElementType() == JsonStreamElementType.START_FIELD) {
                fieldNames.add(next.text());
            }
        }
        assertThat(fieldNames, equalTo((Set<String>) new HashSet<>(asList("name", "sales", "totalRoyalties", "singles"))));
    }
}
