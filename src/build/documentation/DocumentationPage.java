/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package documentation;

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.*;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.StajParser;
import org.sourceforge.xazzle.xhtml.HtmlTag;
import org.sourceforge.xazzle.xhtml.InlineTag;

import java.io.Reader;
import java.math.BigDecimal;

import static documentation.ArgoPage.*;
import static org.sourceforge.xazzle.xhtml.Href.href;
import static org.sourceforge.xazzle.xhtml.Tags.*;

final class DocumentationPage {

    private static final InlineTag JSON_ROOT_NODE = simpleNameOf(JsonRootNode.class);
    private static final InlineTag JSON_NODE = simpleNameOf(JsonNode.class);
    private static final InlineTag STRING = simpleNameOf(String.class);
    private static final InlineTag JDOM_PARSER = simpleNameOf(JdomParser.class);
    private static final InlineTag JSON_NODE_SELECTOR = simpleNameOf(JsonNodeSelector.class);

    private DocumentationPage() {
    }

    @SuppressWarnings({"StaticMethodOnlyUsedInOneClass"})
    static HtmlTag documentationPage() {
        String jsonText = "jsonText";
        return anArgoPage(
                h2Tag(xhtmlText("Documentation")),
                h3Tag(xhtmlText("Introduction")),
                paragraphTag(
                        xhtmlText("This page provides an example-based guide to Argo. In-depth details of the API are available in the "),
                        anchorTag(xhtmlText("online javadoc")).withHref(href("javadoc/")),
                        xhtmlText(", which can also be found in the "),
                        anchorTag(xhtmlText("standard jar")).withHref(href("https://sourceforge.net/projects/argo/files/latest")),
                        xhtmlText(".")
                ),
                h3Tag(xhtmlText("Model of JSON in Argo")),
                paragraphTag(
                        xhtmlText("There is a good explanation of "),
                        anchorTag(xhtmlText("the structure of JSON")).withHref(href("http://www.json.org/")),
                        xhtmlText(" at http://www.json.org/. Argo models JSON using five types of entity - two that can have children:")
                ),
                unorderedListTag(
                        listItemTag(xhtmlText("Objects - pairs of a string and any entity type.")),
                        listItemTag(xhtmlText("Arrays - sequences of any entity type."))
                ),
                paragraphTag(xhtmlText("and three types of leaf entity:")),
                unorderedListTag(
                        listItemTag(xhtmlText("Strings - essentially the same a Java Strings.")),
                        listItemTag(xhtmlText("Numbers - an unlimited precision number.")),
                        listItemTag(xhtmlText("Constants - true, false, and null."))
                ),
                h3Tag(xhtmlText("Producing JSON")),
                paragraphTag(
                        xhtmlText("There are two steps to producing JSON using Argo: First, an instance of "), JSON_ROOT_NODE,
                        xhtmlText(" representing the JSON is built, and then an instance of "),
                        simpleNameOf(JsonFormatter.class),
                        xhtmlText(" is used to turn that into a "),
                        STRING,
                        xhtmlText(" representation.")
                ),
                paragraphTag(
                        xhtmlText("A "), JSON_ROOT_NODE,
                        xhtmlText(" represents either a JSON object or a JSON array. They can have fields and elements consisting of instances of "),
                        JSON_NODE, xhtmlText(", which is the superclass of "), JSON_ROOT_NODE,
                        xhtmlText(". A number of factory methods are provided for constructing "), JSON_NODE, xhtmlText(" and "),
                        JSON_ROOT_NODE, xhtmlText("s in the "), simpleNameOf(JsonNodeFactories.class), xhtmlText(" class:")
                ),
                codeBlock("import static argo.jdom.JsonNodeFactories.*;\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "JsonRootNode json = aJsonObject(\n" +
                        "        aJsonField(\"name\", aJsonString(\"Black Lace\"))\n" +
                        "        , aJsonField(\"sales\", aJsonNumber(\"110921\"))\n" +
                        "        , aJsonField(\"totalRoyalties\", aJsonNumber(\"10223.82\"))\n" +
                        "        , aJsonField(\"singles\", aJsonArray(\n" +
                        "                aJsonString(\"Superman\")\n" +
                        "                , aJsonString(\"Agadoo\")\n" +
                        "        ))\n" +
                        ");"),
                paragraphTag(
                        xhtmlText("All implementations of "), JSON_NODE, xhtmlText(" and "), JSON_ROOT_NODE,
                        xhtmlText(" are immutable. The same "), JSON_ROOT_NODE,
                        xhtmlText(" could have been built up in a mutable manner using the methods in the "),
                        simpleNameOf(JsonNodeBuilders.class), xhtmlText(" class:")
                ),
                codeBlock("import static argo.jdom.JsonNodeBuilders.*;\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "JsonObjectNodeBuilder builder = anObjectBuilder()\n" +
                        "        .withField(\"name\", aStringBuilder(\"Black Lace\"))\n" +
                        "        .withField(\"sales\", aNumberBuilder(\"110921\"))\n" +
                        "        .withField(\"totalRoyalties\", aNumberBuilder(\"10223.82\"))\n" +
                        "        .withField(\"singles\", anArrayBuilder()\n" +
                        "                .withElement(aStringBuilder(\"Superman\"))\n" +
                        "                .withElement(aStringBuilder(\"Agadoo\"))\n" +
                        "        );\n" +
                        "JsonRootNode json = builder.build();"),
                paragraphTag(
                        xhtmlText("Finally, an instance of "), simpleNameOf(JsonFormatter.class),
                        xhtmlText(" is needed to produce JSON text from the "), JSON_ROOT_NODE,
                        xhtmlText(". The two implementations provided are "), simpleNameOf(PrettyJsonFormatter.class),
                        xhtmlText(", which produces easy to read JSON, and "), simpleNameOf(CompactJsonFormatter.class),
                        xhtmlText(", which produces very brief JSON. Instances of both classes can safely be shared between threads.")
                ),
                codeBlock("private static final JsonFormatter JSON_FORMATTER\n" +
                        "        = new PrettyJsonFormatter();\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "String jsonText = JSON_FORMATTER.format(json);"),
                h3Tag(xhtmlText("Parsing JSON into an Object")),
                paragraphTag(
                        xhtmlText("All the examples in this section are based on the following JSON, which is assumed to be available in a "),
                        STRING, xhtmlText(" variable called "), variableName(jsonText), xhtmlText(".")
                ),
                codeBlock("{\n" +
                        "    \"name\": \"Black Lace\",\n" +
                        "    \"sales\": 110921,\n" +
                        "    \"totalRoyalties\": 10223.82,\n" +
                        "    \"singles\": [\n" +
                        "        \"Superman\", \"Agadoo\"\n" +
                        "    ]\n" +
                        "}"),
                paragraphTag(
                        xhtmlText("The "), JDOM_PARSER, xhtmlText(" class is used to generate a "), JSON_ROOT_NODE,
                        xhtmlText(" from JSON text. Instances of "), JDOM_PARSER, xhtmlText(" can safely be shared between threads.")
                ),
                codeBlock("private static final JdomParser JDOM_PARSER = new JdomParser();\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "JsonRootNode json = JDOM_PARSER.parse(jsonText);"),
                paragraphTag(
                        xhtmlText("Instances of "), JSON_ROOT_NODE, xhtmlText(" are immutable. "), JSON_ROOT_NODE,
                        xhtmlText(" and its parent "), JSON_NODE, xhtmlText(" provide methods for exploring the " +
                        "generated structure, as specified in the javadoc. The simplest of these are the "),
                        codeSnippet("getXXXValue(Object... pathElements)"), xhtmlText(" methods, which are used as follows:")
                ),
                codeBlock("String secondSingle = json.getStringValue(\"singles\", 1);"),
                paragraphTag(
                        xhtmlText("It is also possible to check that the node at a particular path is of the required type as follows:")
                ),
                codeBlock("boolean isString = json.isStringValue(\"singles\", 1);"),
                paragraphTag(
                        xhtmlText("Alternatively, "), JSON_NODE_SELECTOR, xhtmlText("s can be used to extract data from the "),
                        JSON_ROOT_NODE, xhtmlText("in a typesafe manner. "), JSON_NODE_SELECTOR, xhtmlText("s are functions that can be applied to "),
                        JSON_NODE, xhtmlText("s to extract a value or a child "), JSON_NODE, xhtmlText(". The "),
                        simpleNameOf(JsonNodeSelectors.class), xhtmlText(" class provides a number of factory methods for constructing instances of "),
                        JSON_NODE_SELECTOR, xhtmlText(". The following code gets the name of the second single (note that like arrays, the index of the first element is zero):")
                ),
                codeBlock("private static final JsonNodeSelector<JsonNode,String> SECOND_SINGLE\n" +
                        "        = JsonNodeSelectors.aStringNode(\"singles\", 1);\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "String secondSingle = SECOND_SINGLE.getValue(json);"),
                paragraphTag(
                        xhtmlText("The singles can also be converted into a "), codeSnippet("List<String>"), xhtmlText(":")
                ),
                codeBlock("private static final JsonNodeSelector<JsonNode, List<JsonNode>> SINGLES\n" +
                        "        = anArrayNode(\"singles\");\n" +
                        "private static final JsonNodeSelector<JsonNode, String> SINGLE_NAME\n" +
                        "        = aStringNode();\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "List<String> singles = new AbstractList<String>() {\n" +
                        "    public String get(int index) {\n" +
                        "        return SINGLE_NAME.getValue(SINGLES.getValue(json).get(index));\n" +
                        "    }\n" +
                        "\n" +
                        "    public int size() {\n" +
                        "        return SINGLES.getValue(json).size();\n" +
                        "    }\n" +
                        "};"),
                paragraphTag(
                        xhtmlText("JSON number handling deserves a brief explanation. JSON numbers are unlimited precision, " +
                                "and as such, cannot be represented by a Java primitive like "), codeSnippet("double"),
                        xhtmlText(" or "), codeSnippet("int"), xhtmlText(". The closest numeric representation offered by Java is "),
                        simpleNameOf(BigDecimal.class), xhtmlText(", which accurately represents almost all JSON numbers, with the " +
                        "exception of some edge cases (for example, "), codeSnippet("-0"), xhtmlText(" and "), codeSnippet("+0"),
                        xhtmlText(" are valid JSON numbers, but collapse to "), codeSnippet("BigDecimal.ZERO"),
                        xhtmlText("). For this reason, Argo doesn't do any implicit conversion of JSON numbers - they're treated as "),
                        STRING, xhtmlText("s. However, helper methods are provided for converting these "),
                        STRING, xhtmlText("s to other Java numeric types. For example, you could retrieve the total royalties from" +
                        " the example JSON as follows:")
                ),
                codeBlock("import static argo.format.JsonNumberUtils.asBigDecimal;\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "BigDecimal totalRoyalties = asBigDecimal(json.getNumberValue(\"totalRoyalties\"));"),
                h3Tag(xhtmlText("Parsing JSON into events")),
                paragraphTag(
                        xhtmlText("The "), simpleNameOf(SajParser.class), xhtmlText(" class offers a SAX style interface for parsing JSON. Given a "),
                        simpleNameOf(Reader.class), xhtmlText("of a stream of JSON text, and an implementation of "),
                        simpleNameOf(JsonListener.class), xhtmlText(", the "), codeSnippet("parse"),
                        xhtmlText(" method will parse the JSON from the "), simpleNameOf(Reader.class),
                        xhtmlText(", and call methods on the "), simpleNameOf(JsonListener.class),
                        xhtmlText(" as it encounters parse events, such as the start of the document, the start of a field, or a JSON string.")
                ),
                paragraphTag(
                        xhtmlText("The following code extracts the names of all the fields in a piece of JSON. It assumes "),
                        variableName("jsonReader"), xhtmlText(" refers to a "), simpleNameOf(Reader.class),
                        xhtmlText(" of the example JSON in the previous section.")
                ),
                codeBlock("private static final SajParser SAJ_PARSER\n" +
                        "        = new SajParser();\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "final Set<String> fieldNames = new HashSet<String>();\n" +
                        "SAJ_PARSER.parse(jsonReader, new JsonListener() {\n" +
                        "    public void startField(String name) {\n" +
                        "        fieldNames.add(name);\n" +
                        "    }\n" +
                        "    public void startDocument() { }\n" +
                        "    public void endDocument() { }\n" +
                        "\n" +
                        "...\n" +
                        "\n" +
                        "});"),
                h3Tag(xhtmlText("Parsing JSON through iteration")),
                paragraphTag(
                        xhtmlText("The "), simpleNameOf(StajParser.class),
                        xhtmlText(" class allows the calling code to request parsing events from a stream of JSON text, similar to how the StAX parser works for XML.")
                ),
                paragraphTag(
                        xhtmlText("The following code gets the names of all the fields in a piece of JSON using the "),
                        simpleNameOf(StajParser.class), xhtmlText(". Again, it assumes "), variableName("jsonReader"),
                        xhtmlText(" refers to a "), simpleNameOf(Reader.class),
                        xhtmlText(" of the example JSON in the previous section.")
                ),
                codeBlock("Set<String> fieldNames = new HashSet<String>();\n" +
                        "StajParser stajParser = null;\n" +
                        "try {\n" +
                        "    stajParser = new StajParser(jsonReader);\n" +
                        "    while (stajParser.hasNext()) {\n" +
                        "        if (stajParser.next() == JsonStreamElementType.START_FIELD) {\n" +
                        "            fieldNames.add(stajParser.getText());\n" +
                        "        }\n" +
                        "    }\n" +
                        "} finally {\n" +
                        "    if (stajParser != null) {\n" +
                        "        stajParser.close();\n" +
                        "    }\n" +
                        "}"),
                paragraphTag(
                        xhtmlText("Note that it is important to call "), codeSnippet("close()"),
                        xhtmlText(" when finished with an instance of "), simpleNameOf(StajParser.class),
                        xhtmlText(" to free up resource associated with it.")
                )
        );
    }

}
