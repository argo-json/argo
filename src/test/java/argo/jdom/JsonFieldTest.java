/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import static argo.TestingFactories.aString;
import static argo.jdom.JsonNodeFactories.string;
import static argo.jdom.JsonNodeTestingFactories.aJsonNode;
import static argo.jdom.JsonNodeTestingFactories.aJsonNodeDifferentTo;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNode;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNodeDifferentTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonFieldTest {
    @Test
    void rejectsNullStringNameInConstructor() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new JsonField((String) null, aJsonNode()));
        assertThat(nullPointerException.getMessage(), equalTo("Name is null"));
    }

    @Test
    void rejectsNullJsonStringNameInConstructor() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new JsonField((JsonStringNode) null, aJsonNode()));
        assertThat(nullPointerException.getMessage(), equalTo("Name is null"));
    }

    @Test
    void rejectsNullValueInStringConstructor() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new JsonField(aString(), null));
        assertThat(nullPointerException.getMessage(), equalTo("Value is null"));
    }

    @Test
    void rejectsNullValueInJsonStringConstructor() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new JsonField(aStringNode(), null));
        assertThat(nullPointerException.getMessage(), equalTo("Value is null"));
    }

    @Test
    void canGetNameWhenConstructedWithAString() {
        final String name = aString();
        assertThat(new JsonField(name, aStringNode()).getName(), equalTo(string(name)));
    }

    @Test
    void canGetNameWhenConstructedWithAJsonString() {
        final JsonStringNode name = aStringNode();
        assertThat(new JsonField(name, aStringNode()).getName(), equalTo(name));
    }

    @Test
    void canGetNameTextWhenConstructedWithAString() {
        final String name = aString();
        assertThat(new JsonField(name, aStringNode()).getNameText(), equalTo(name));
    }

    @Test
    void canGetNameTextWhenConstructedWithAJsonString() {
        final JsonStringNode name = aStringNode();
        assertThat(new JsonField(name, aStringNode()).getNameText(), equalTo(name.getText()));
    }

    @Test
    void canGetValue() {
        final JsonNode value = aJsonNode();
        assertThat(new JsonField(aStringNode(), value).getValue(), equalTo(value));
    }

    @Test
    void toStringIsCorrect() {
        final JsonStringNode name = aStringNode();
        final JsonNode value = aJsonNode();
        assertThat(new JsonField(name, value).toString(), equalTo("JsonField{name=" + name + ", value=" + value + "}"));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void aJsonFieldIsEqualToItself() {
        final JsonField aJsonField = new JsonField(aStringNode(), aJsonNode());
        assertThat(aJsonField.equals(aJsonField), equalTo(true));
    }

    @Test
    @SuppressWarnings({"ConstantValue", "PMD.EqualsNull"})
    void aJsonFieldIsNotEqualToNull() {
        final JsonField aJsonField = new JsonField(aStringNode(), aJsonNode());
        assertThat(aJsonField.equals(null), equalTo(false));
    }

    @Test
    void aJsonFieldIsNotEqualToAnObjectOfDifferentType() {
        final JsonField aJsonField = new JsonField(aStringNode(), aJsonNode());
        assertThat(aJsonField.equals(new Object()), equalTo(false));
    }

    @Test
    void twoJsonFieldsWithTheEqualNamesAndValuesAreEqual() {
        final JsonStringNode name = aStringNode();
        final JsonNode value = aJsonNode();
        final JsonField aJsonField = new JsonField(name, value);
        final JsonField anEqualJsonField = new JsonField(name, value);
        assertThat(aJsonField, equalTo(anEqualJsonField));
        assertThat(aJsonField.hashCode(), equalTo(anEqualJsonField.hashCode()));
    }

    @Test
    void twoJsonFieldsWithTheEqualNamesAndDifferentValuesAreNotEqual() {
        final JsonStringNode name = aStringNode();
        final JsonNode value = aJsonNode();
        assertThat(new JsonField(name, value), not(equalTo(new JsonField(name, aJsonNodeDifferentTo(value)))));
    }

    @Test
    void twoJsonFieldsWithTheDifferentNamesAndEqualValuesAreNotEqual() {
        final JsonNode value = aJsonNode();
        final JsonStringNode name = aStringNode();
        assertThat(new JsonField(name, value), not(equalTo(new JsonField(aStringNodeDifferentTo(name), value))));
    }
}
