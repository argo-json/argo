/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static argo.jdom.JsonStringNodeTestBuilder.aValidJsonString;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.JsonStreamElementMatchers.aNonTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementMatchers.aTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JsonStreamElementTest {
    @Test
    void startDocumentHasCorrectAttributes() {
        assertThat(startDocument(), aNonTextJsonStreamElementWithType(START_DOCUMENT));
    }

    @Test
    void endDocumentHasCorrectAttributes() {
        assertThat(endDocument(), aNonTextJsonStreamElementWithType(END_DOCUMENT));
    }

    @Test
    void startArrayHasCorrectAttributes() {
        assertThat(startArray(), aNonTextJsonStreamElementWithType(START_ARRAY));
    }

    @Test
    void endArrayHasCorrectAttributes() {
        assertThat(endArray(), aNonTextJsonStreamElementWithType(END_ARRAY));
    }

    @Test
    void startObjectHasCorrectAttributes() {
        assertThat(startObject(), aNonTextJsonStreamElementWithType(START_OBJECT));
    }

    @Test
    void endObjectHasCorrectAttributes() {
        assertThat(endObject(), aNonTextJsonStreamElementWithType(END_OBJECT));
    }

    @Test
    void startFieldHasCorrectAttributes() {
        final String text = aValidJsonString();
        assertThat(startField(new StringReader(text)), aTextJsonStreamElementWithType(START_FIELD, text));
    }

    @Test
    void endFieldHasCorrectAttributes() {
        assertThat(endField(), aNonTextJsonStreamElementWithType(END_FIELD));
    }

    @Test
    void stringHasCorrectAttributes() {
        final String text = aValidJsonString();
        assertThat(string(new StringReader(text)), aTextJsonStreamElementWithType(STRING, text));
    }

    @Test
    void numberHasCorrectAttributes() {
        final String text = aValidJsonString();
        assertThat(number(new StringReader(text)), aTextJsonStreamElementWithType(NUMBER, text));
    }

    @Test
    void trueHasCorrectAttributes() {
        assertThat(trueValue(), aNonTextJsonStreamElementWithType(TRUE));
    }

    @Test
    void falseHasCorrectAttributes() {
        assertThat(falseValue(), aNonTextJsonStreamElementWithType(FALSE));
    }

    @Test
    void nullHasCorrectAttributes() {
        assertThat(nullValue(), aNonTextJsonStreamElementWithType(NULL));
    }

    @Test
    void startDocumentHasCorrectToString() {
        assertThat(startDocument().toString(), equalTo("JsonStreamElement jsonStreamElementType: START_DOCUMENT"));
    }

    @Test
    void endDocumentHasCorrectToString() {
        assertThat(endDocument().toString(), equalTo("JsonStreamElement jsonStreamElementType: END_DOCUMENT"));
    }

    @Test
    void startArrayHasCorrectToString() {
        assertThat(startArray().toString(), equalTo("JsonStreamElement jsonStreamElementType: START_ARRAY"));
    }

    @Test
    void endArrayHasCorrectToString() {
        assertThat(endArray().toString(), equalTo("JsonStreamElement jsonStreamElementType: END_ARRAY"));
    }

    @Test
    void startObjectHasCorrectToString() {
        assertThat(startObject().toString(), equalTo("JsonStreamElement jsonStreamElementType: START_OBJECT"));
    }

    @Test
    void endObjectHasCorrectToString() {
        assertThat(endObject().toString(), equalTo("JsonStreamElement jsonStreamElementType: END_OBJECT"));
    }

    @Test
    void startFieldHasCorrectToString() {
        final String text = aValidJsonString();
        assertThat(startField(new StringReader(text)).toString(), equalTo("JsonStreamElement jsonStreamElementType: START_FIELD"));
    }

    @Test
    void endFieldHasCorrectToString() {
        assertThat(endField().toString(), equalTo("JsonStreamElement jsonStreamElementType: END_FIELD"));
    }

    @Test
    void stringHasCorrectToString() {
        final String text = aValidJsonString();
        assertThat(string(new StringReader(text)).toString(), equalTo("JsonStreamElement jsonStreamElementType: STRING"));
    }

    @Test
    void numberHasCorrectToString() {
        final String text = aValidJsonString();
        assertThat(number(new StringReader(text)).toString(), equalTo("JsonStreamElement jsonStreamElementType: NUMBER"));
    }

    @Test
    void trueHasCorrectToString() {
        assertThat(trueValue().toString(), equalTo("JsonStreamElement jsonStreamElementType: TRUE"));
    }

    @Test
    void falseHasCorrectToString() {
        assertThat(falseValue().toString(), equalTo("JsonStreamElement jsonStreamElementType: FALSE"));
    }

    @Test
    void nullHasCorrectToString() {
        assertThat(nullValue().toString(), equalTo("JsonStreamElement jsonStreamElementType: NULL"));
    }

}
