/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Arrays;

import static argo.jdom.JsonNodeFactories.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

final class JsonListenerToJdomAdapterTest {

    @Test
    void simpleStringObjectHappyCase() {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField(new StringReader("Hello"));
        jsonListenerToJdomAdapter.stringValue(new StringReader("World"));
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = object(field("Hello", string("World")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    void simpleNumberObjectHappyCase() {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField(new StringReader("Gigawatts"));
        jsonListenerToJdomAdapter.numberValue(new StringReader("1.21"));
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = object(field("Gigawatts", number("1.21")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    void simpleStringArrayHappyCase() {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.stringValue(new StringReader("Hello"));
        jsonListenerToJdomAdapter.stringValue(new StringReader("World"));
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = array(Arrays.asList((JsonNode) string("Hello"), string("World")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    void simpleNumberArrayHappyCase() {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.numberValue(new StringReader("1.21"));
        jsonListenerToJdomAdapter.numberValue(new StringReader("42"));
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = array(Arrays.asList(number("1.21"), number("42")));
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }

    @Test
    void objectArrayHappyCase() {
        final JsonListenerToJdomAdapter jsonListenerToJdomAdapter = new JsonListenerToJdomAdapter();
        jsonListenerToJdomAdapter.startDocument();
        jsonListenerToJdomAdapter.startArray();
        jsonListenerToJdomAdapter.startObject();
        jsonListenerToJdomAdapter.startField(new StringReader("anObject"));
        jsonListenerToJdomAdapter.stringValue(new StringReader("objectValue"));
        jsonListenerToJdomAdapter.endField();
        jsonListenerToJdomAdapter.endObject();
        jsonListenerToJdomAdapter.nullValue();
        jsonListenerToJdomAdapter.trueValue();
        jsonListenerToJdomAdapter.falseValue();
        jsonListenerToJdomAdapter.endArray();
        jsonListenerToJdomAdapter.endDocument();
        final JsonNode expected = array(
                object(field("anObject", string("objectValue"))),
                nullNode(),
                trueNode(),
                falseNode()
        );
        assertThat(jsonListenerToJdomAdapter.getDocument(), equalTo(expected));
    }
}
