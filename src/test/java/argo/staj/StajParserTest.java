/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import argo.format.PrettyJsonBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;
import argo.jdom.JsonStringNodeTestBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.aJsonNode;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static argo.staj.ElementTrackingStajParserMatcher.generatesElements;
import static argo.staj.JsonStreamElement.number;
import static argo.staj.JsonStreamElement.string;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.RoundTrippingStajParserMatcher.parsesTo;
import static argo.staj.StajParserBuilder.stajParser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StajParserTest {

    @Test
    public void arrayOnlyDocumentHasCorrectElements() throws Exception {
        assertThat(stajParser(array()), generatesElements(startDocument(), startArray(), endArray(), endDocument()));
    }

    @Test
    public void objectOnlyDocumentHasCorrectElements() throws Exception {
        assertThat(stajParser(object()), generatesElements(startDocument(), startObject(), endObject(), endDocument()));
    }

    @Test
    public void stringOnlyDocumentHasCorrectElements() throws Exception {
        final JsonNode stringNode = aStringNode();
        assertThat(stajParser(stringNode), generatesElements(startDocument(), string(stringNode.getText()), endDocument()));
    }

    @Test
    public void numberOnlyDocumentHasCorrectElements() throws Exception {
        final JsonNode numberNode = aNumberNode();
        assertThat(stajParser(numberNode), generatesElements(startDocument(), number(numberNode.getText()), endDocument()));
    }

    @Test
    public void nullOnlyDocumentHasCorrectElements() throws Exception {
        assertThat(stajParser(nullNode()), generatesElements(startDocument(), nullValue(), endDocument()));
    }

    @Test
    public void trueOnlyDocumentHasCorrectElements() throws Exception {
        assertThat(stajParser(trueNode()), generatesElements(startDocument(), trueValue(), endDocument()));
    }

    @Test
    public void falseOnlyDocumentHasCorrectElements() throws Exception {
        assertThat(stajParser(falseNode()), generatesElements(startDocument(), falseValue(), endDocument()));
    }

    @Test
    public void arrayWithChildHasCorrectElements() throws Exception {
        assertThat(stajParser(array(array())), generatesElements(startDocument(), startArray(), startArray(), endArray(), endArray(), endDocument()));
    }

    @Test
    public void arrayWithChildrenHasCorrectElements() throws Exception {
        assertThat(stajParser(array(array(), array())), generatesElements(
                startDocument(),
                startArray(),
                startArray(),
                endArray(),
                startArray(),
                endArray(),
                endArray(),
                endDocument()
        ));
    }

    @Test
    public void objectWithFieldHasCorrectElements() throws Exception {
        final JsonStringNode aFieldName = JsonStringNodeTestBuilder.aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()))), generatesElements(
                startDocument(),
                startObject(),
                startField(aFieldName.getText()),
                startArray(),
                endArray(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    public void objectWithFieldsHasCorrectElements() throws Exception {
        final JsonStringNode aFieldName = aStringNode();
        final JsonStringNode anotherFieldName = aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()), field(anotherFieldName, object()))), generatesElements(
                startDocument(),
                startObject(),
                startField(aFieldName.getText()),
                startArray(),
                endArray(),
                endField(),
                startField(anotherFieldName.getText()),
                startObject(),
                endObject(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    public void arrayWithNullHasCorrectElements() throws Exception {
        assertThat(stajParser(array(nullNode())), generatesElements(startDocument(), startArray(), nullValue(), endArray(), endDocument()));
    }

    @Test
    public void arrayWithNullsHasCorrectElements() throws Exception {
        assertThat(stajParser(array(nullNode(), nullNode())), generatesElements(startDocument(), startArray(), nullValue(), nullValue(), endArray(), endDocument()));
    }

    @Test
    public void arrayWithTrueHasCorrectElements() throws Exception {
        assertThat(stajParser(array(trueNode())), generatesElements(startDocument(), startArray(), trueValue(), endArray(), endDocument()));
    }

    @Test
    public void arrayWithFalseHasCorrectElements() throws Exception {
        assertThat(stajParser(array(falseNode())), generatesElements(startDocument(), startArray(), falseValue(), endArray(), endDocument()));
    }

    @Test
    public void arrayWithATextNodeHasCorrectElements() throws Exception {
        final JsonNode aStringNode = aStringNode();
        assertThat(stajParser(array(aStringNode)), generatesElements(startDocument(), startArray(), string(aStringNode.getText()), endArray(), endDocument()));
    }

    @Test
    public void arrayWithANumberNodeHasCorrectElements() throws Exception {
        final JsonNode aNumberNode = aNumberNode();
        assertThat(stajParser(array(aNumberNode)), generatesElements(startDocument(), startArray(), number(aNumberNode.getText()), endArray(), endDocument()));
    }

    @Test
    public void aRandomJsonNodeHasCorrectElements() throws Exception {
        final JsonNode aJsonNode = aJsonNode();
        assertThat(stajParser(aJsonNode), parsesTo(aJsonNode));
    }

    @Test
    public void aRandomJsonNodeFromStringHasCorrectElements() throws Exception {
        final JsonNode jsonNode = aJsonNode();
        assertThat(new StajParser(PrettyJsonBuilder.json(jsonNode)), parsesTo(jsonNode));
    }

    @Test
    public void nextWorksWithoutCallingHasNext() throws Exception {
        assertThat(stajParser(array()).next(), equalTo(startDocument()));
    }

    @Test
    public void callingNextWhenHasNextReturnsFalseThrowsAnException() throws Exception {
        final StajParser stajParser = stajParser(array());
        while (stajParser.hasNext()) {
            stajParser.next();
        }
        assertThrows(NoSuchElementException.class, stajParser::next);
    }

    @Test
    public void handlesIoExceptionDuringParsing() throws Exception {
        final StajParser stajParser = new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                throw new IOException("An IOException");
            }

            public void close() throws IOException {
            }
        });
        stajParser.next();
        assertThrows(JsonStreamException.class, stajParser::next);
    }

    @Test
    public void handlesRuntimeExceptionDuringParsing() throws Exception {
        final StajParser stajParser = new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                throw new MyTestRuntimeException();
            }

            public void close() throws IOException {
            }
        });
        stajParser.next();
        assertThrows(MyTestRuntimeException.class, stajParser::next);
    }

    private static final class MyTestRuntimeException extends RuntimeException {
    }

}
