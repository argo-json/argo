/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;import argo.format.*;
import argo.jdom.JdomParser;
import argo.staj.JsonStreamElement;
import argo.staj.StajParser;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static argo.jdom.JsonNodeFactories.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExamplesTest {

    @Test
    void generateSomeJson() throws Exception { // TODO should be JsonWriter?  Maybe the name should be JsonGenerator??
        // tag::generateSomeJson[]
        String blogEntry = new PrettyJsonFormatter().format( // <1>
            object( // <2>
                field("title", string("How to use Argo")),
                field("version", number(1))
            )
        );
        // end::generateSomeJson[]
        assertEquals(trim("""
// tag::generateSomeJsonResult[]
{
	"title": "How to use Argo",
	"version": 1
}
// end::generateSomeJsonResult[]
""")
                , blogEntry);

        // tag::parseSomeJson[]
        String title = new JdomParser().parse(blogEntry).getStringValue("title");
        // end::parseSomeJson[]

        assertEquals("How to use Argo", title);
    }

    @Test
    void streamsSomeJson() throws Exception {
        // tag::streamOutSomeJson[]
        Iterable<String> comments = queryComments(); // <1>
        StringWriter stringWriter = new StringWriter(); // <2>
        new PrettyJsonWriter().write(stringWriter, (WriteableJsonObject) objectWriter -> {
            objectWriter.writeField("title", string("How to use Argo")); // <3>
            // tag::streamOutSomeJsonIntermediateResultWrapper[]
            assertEquals(trim("""
// tag::streamOutSomeJsonIntermediateResult[]
{
	"title": "How to use Argo"
// end::streamOutSomeJsonIntermediateResult[]
                    	"""), stringWriter.toString());
            // end::streamOutSomeJsonIntermediateResultWrapper[]
            objectWriter.writeField("version", number(2));
            objectWriter.writeField("comments", (WriteableJsonArray) arrayWriter -> {
                for (String comment : comments) { // <4>
                    arrayWriter.writeElement(string(comment));
                }
            });
        });
        // end::streamOutSomeJson[]
        assertEquals("""
{
	"title": "How to use Argo",
	"version": 2,
	"comments": [
		"Thanks for the post!",
		"👍"
	]
}""", stringWriter.toString());

        // tag::streamInSomeJson[]
        int commentCount = 0;
        StringReader stringReader = new StringReader(stringWriter.toString()); // <1>
        StajParser stajParser = new StajParser(stringReader);
        Deque<String> stack = new ArrayDeque<>(); // <2>
        while (stajParser.hasNext()) {
            JsonStreamElement next = stajParser.next();
            switch (next.jsonStreamElementType()) {
                case START_FIELD:
                    StringWriter fieldNameWriter = new StringWriter();
                    try (Reader fieldNameReader = next.reader()) { // <3>
                        fieldNameReader.transferTo(fieldNameWriter);
                    }
                    stack.push(fieldNameWriter.toString());
                    break;
                case END_FIELD:
                    stack.pop();
                    break;
                case STRING:
                    if (List.of("comments").equals(List.copyOf(stack))) {
                        commentCount++;
                    }
                    break;
                default:
                    // ignore other element types
            }
        }
        // end::streamInSomeJson[]

        assertEquals(2, commentCount);
    }

    static Iterable<String> queryComments() {
        return Arrays.asList("Thanks for the post!", "\uD83D\uDC4D");
    }

    static String trim(final String taggedString) {
        return taggedString.replaceFirst("^[^\n]*// tag::[^\n]*\n", "").replaceFirst("\n[^\n]*// end::[^\n]*\n", "");
    }
}
