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

import org.junit.jupiter.api.Test;

import static argo.jdom.JsonStringNodeTestBuilder.aValidJsonString;
import static argo.staj.JsonStreamElement.startDocument;
import static argo.staj.JsonStreamElement.*;
import static argo.staj.JsonStreamElementMatchers.aNonTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementMatchers.aTextJsonStreamElementWithType;
import static argo.staj.JsonStreamElementType.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonStreamElementTest {
    @Test
    public void startDocumentHasCorrectAttributes() throws Exception {
        assertThat(startDocument(), aNonTextJsonStreamElementWithType(START_DOCUMENT));
    }

    @Test
    public void endDocumentHasCorrectAttributes() throws Exception {
        assertThat(endDocument(), aNonTextJsonStreamElementWithType(END_DOCUMENT));
    }

    @Test
    public void startArrayHasCorrectAttributes() throws Exception {
        assertThat(startArray(), aNonTextJsonStreamElementWithType(START_ARRAY));
    }

    @Test
    public void endArrayHasCorrectAttributes() throws Exception {
        assertThat(endArray(), aNonTextJsonStreamElementWithType(END_ARRAY));
    }

    @Test
    public void startObjectHasCorrectAttributes() throws Exception {
        assertThat(startObject(), aNonTextJsonStreamElementWithType(START_OBJECT));
    }

    @Test
    public void endObjectHasCorrectAttributes() throws Exception {
        assertThat(endObject(), aNonTextJsonStreamElementWithType(END_OBJECT));
    }

    @Test
    public void startFieldHasCorrectAttributes() throws Exception {
        final String text = aValidJsonString();
        assertThat(startField(text), aTextJsonStreamElementWithType(START_FIELD, text));
    }

    @Test
    public void endFieldHasCorrectAttributes() throws Exception {
        assertThat(endField(), aNonTextJsonStreamElementWithType(END_FIELD));
    }

    @Test
    public void stringHasCorrectAttributes() throws Exception {
        final String text = aValidJsonString();
        assertThat(string(text), aTextJsonStreamElementWithType(STRING, text));
    }

    @Test
    public void numberHasCorrectAttributes() throws Exception {
        final String text = aValidJsonString();
        assertThat(number(text), aTextJsonStreamElementWithType(NUMBER, text));
    }

    @Test
    public void trueHasCorrectAttributes() throws Exception {
        assertThat(trueValue(), aNonTextJsonStreamElementWithType(TRUE));
    }

    @Test
    public void falseHasCorrectAttributes() throws Exception {
        assertThat(falseValue(), aNonTextJsonStreamElementWithType(FALSE));
    }

    @Test
    public void nullHasCorrectAttributes() throws Exception {
        assertThat(nullValue(), aNonTextJsonStreamElementWithType(NULL));
    }

}
