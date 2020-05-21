/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import org.junit.jupiter.api.Test;

import static argo.jdom.JsonStringNodeTestBuilder.aValidJsonString;
import static argo.staj.JsonStreamElement.startDocument;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.JsonStreamElementMatchers.aNonTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementMatchers.aTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementType.*;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(startField(text), aTextJsonStreamElementWithType(START_FIELD, text));
    }

    @Test
    void endFieldHasCorrectAttributes() {
        assertThat(endField(), aNonTextJsonStreamElementWithType(END_FIELD));
    }

    @Test
    void stringHasCorrectAttributes() {
        final String text = aValidJsonString();
        assertThat(string(text), aTextJsonStreamElementWithType(STRING, text));
    }

    @Test
    void numberHasCorrectAttributes() {
        final String text = aValidJsonString();
        assertThat(number(text), aTextJsonStreamElementWithType(NUMBER, text));
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

}
