/*
 *  Copyright 2023 Mark Slater
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
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import argo.jdom.JsonStringNodeTestBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNodeTestBuilder.aJsonNode;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.*;
import static argo.staj.ElementTrackingStajParserMatcher.generatesElements;
import static argo.staj.JsonStreamElement.number;
import static argo.staj.JsonStreamElement.string;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.RoundTrippingStajParserMatcher.parsesTo;
import static argo.staj.StajParserBuilder.stajParser;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class StajParserTest {

    @Test
    void arrayOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(array()), generatesElements(startDocument(), startArray(), endArray(), endDocument()));
    }

    @Test
    void objectOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(object()), generatesElements(startDocument(), startObject(), endObject(), endDocument()));
    }

    @Test
    void stringOnlyDocumentHasCorrectElements() {
        final JsonNode stringNode = aStringNode();
        assertThat(stajParser(stringNode), generatesElements(startDocument(), string(new StringReader(stringNode.getText())), endDocument()));
    }

    @Test
    void numberOnlyDocumentHasCorrectElements() {
        final JsonNode numberNode = aNumberNode();
        assertThat(stajParser(numberNode), generatesElements(startDocument(), number(new StringReader(numberNode.getText())), endDocument()));
    }

    @Test
    void nullOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(nullNode()), generatesElements(startDocument(), nullValue(), endDocument()));
    }

    @Test
    void trueOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(trueNode()), generatesElements(startDocument(), trueValue(), endDocument()));
    }

    @Test
    void falseOnlyDocumentHasCorrectElements() {
        assertThat(stajParser(falseNode()), generatesElements(startDocument(), falseValue(), endDocument()));
    }

    @Test
    void arrayWithChildHasCorrectElements() {
        assertThat(stajParser(array(array())), generatesElements(startDocument(), startArray(), startArray(), endArray(), endArray(), endDocument()));
    }

    @Test
    void arrayWithChildrenHasCorrectElements() {
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
    void objectWithFieldHasCorrectElements() {
        final JsonStringNode aFieldName = JsonStringNodeTestBuilder.aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()))), generatesElements(
                startDocument(),
                startObject(),
                startField(new StringReader(aFieldName.getText())),
                startArray(),
                endArray(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    void objectWithFieldsHasCorrectElements() {
        final JsonStringNode aFieldName = aStringNode();
        final JsonStringNode anotherFieldName = aStringNode();
        assertThat(stajParser(object(field(aFieldName, array()), field(anotherFieldName, object()))), generatesElements(
                startDocument(),
                startObject(),
                startField(new StringReader(aFieldName.getText())),
                startArray(),
                endArray(),
                endField(),
                startField(new StringReader(anotherFieldName.getText())),
                startObject(),
                endObject(),
                endField(),
                endObject(),
                endDocument()
        ));
    }

    @Test
    void arrayWithNullHasCorrectElements() {
        assertThat(stajParser(array(nullNode())), generatesElements(startDocument(), startArray(), nullValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithNullsHasCorrectElements() {
        assertThat(stajParser(array(nullNode(), nullNode())), generatesElements(startDocument(), startArray(), nullValue(), nullValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithTrueHasCorrectElements() {
        assertThat(stajParser(array(trueNode())), generatesElements(startDocument(), startArray(), trueValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithFalseHasCorrectElements() {
        assertThat(stajParser(array(falseNode())), generatesElements(startDocument(), startArray(), falseValue(), endArray(), endDocument()));
    }

    @Test
    void arrayWithATextNodeHasCorrectElements() {
        final JsonNode aStringNode = aStringNode();
        assertThat(stajParser(array(aStringNode)), generatesElements(startDocument(), startArray(), string(new StringReader(aStringNode.getText())), endArray(), endDocument()));
    }

    @Test
    void arrayWithANumberNodeHasCorrectElements() {
        final JsonNode aNumberNode = aNumberNode();
        assertThat(stajParser(array(aNumberNode)), generatesElements(startDocument(), startArray(), number(new StringReader(aNumberNode.getText())), endArray(), endDocument()));
    }

    @Test
    void aRandomJsonNodeHasCorrectElements() {
        final JsonNode aJsonNode = aJsonNode();
        assertThat(stajParser(aJsonNode), parsesTo(aJsonNode));
    }

    @Test
    void aRandomJsonNodeFromStringHasCorrectElements() {
        final JsonNode jsonNode = aJsonNode();
        assertThat(new StajParser(PrettyJsonBuilder.json(jsonNode)), parsesTo(jsonNode));
    }

    @Test
    void nextWorksWithoutCallingHasNext() {
        assertThat(stajParser(array()).next(), equalTo(startDocument()));
    }

    @Test
    void toleratesFieldNameTextNotBeingRead() {
        final StajParser stajParser = stajParser(object(field(aNonEmptyString(), nullNode())));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    void toleratesStringTextNotBeingRead() {
        final StajParser stajParser = stajParser(array(JsonNodeFactories.string(aNonEmptyString())));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    void toleratesNumberTextNotBeingRead() {
        final StajParser stajParser = stajParser(array(aNumberNode()));
        while (stajParser.hasNext()) {
            stajParser.next();
        }
    }

    @Test
    void callingNextWhenHasNextReturnsFalseThrowsAnException() {
        final StajParser stajParser = stajParser(array());
        while (stajParser.hasNext()) {
            stajParser.next();
        }
        assertThrows(NoSuchElementException.class, stajParser::next);
    }

    @Test
    void handlesIoExceptionDuringParsing() {
        final StajParser stajParser = new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                throw new IOException("An IOException");
            }

            public void close() {
            }
        });
        stajParser.next();
        assertThrows(JsonStreamException.class, stajParser::next);
    }

    @Test
    void handlesIoExceptionSkippingElement() {
        final AtomicInteger callCount = new AtomicInteger(0);
        final StajParser stajParser = new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) throws IOException {
                if (callCount.get() == 0) {
                    chars[offset] = '[';
                    callCount.incrementAndGet();
                    return 1;
                } else {
                    throw new IOException("An IOException");
                }
            }

            public void close() {
            }
        });
        stajParser.next();
        stajParser.next();
        assertThrows(JsonStreamException.class, stajParser::next);
    }

    @Test
    void handlesRuntimeExceptionDuringParsing() {
        final StajParser stajParser = new StajParser(new Reader() {
            public int read(char[] chars, int offset, int length) {
                throw new MyTestRuntimeException();
            }

            public void close() {
            }
        });
        stajParser.next();
        assertThrows(MyTestRuntimeException.class, stajParser::next);
    }

    private static final class MyTestRuntimeException extends RuntimeException {
    }

}
