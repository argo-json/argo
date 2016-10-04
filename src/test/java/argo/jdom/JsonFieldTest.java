/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import static argo.jdom.JsonNodeTestBuilder.aJsonNode;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class JsonFieldTest {
    @Test
    public void rejectsNullStringNameInConstructor() throws Exception {
        final String name = null;
        try {
            new JsonField(name, aJsonNode());
            fail("Expected JsonField constructed with a null name to throw a NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Attempt to construct a JsonField with a null name."));
        }
    }

    @Test
    public void rejectsNullJsonStringNameInConstructor() throws Exception {
        final JsonStringNode name = null;
        try {
            new JsonField(name, aJsonNode());
            fail("Expected JsonField constructed with a null name to throw a NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Attempt to construct a JsonField with a null name."));
        }
    }

    @Test
    public void rejectsNullValueInConstructor() throws Exception {
        try {
            new JsonField(aStringNode(), null);
            fail("Expected JsonField constructed with a null value to throw a NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Attempt to construct a JsonField with a null value."));
        }
    }

    @Test
    public void toStringIsCorrect() throws Exception {
        final JsonStringNode name = aStringNode();
        final JsonNode value = aJsonNode();
        assertThat(new JsonField(name, value).toString(), equalTo("JsonField{name=" + name + ", value=" + value + "}"));
    }

    @Test
    public void twoJsonFieldsWithTheEqualNamesAndValuesAreEqual() throws Exception {
        final JsonStringNode name = aStringNode();
        final JsonNode value = aJsonNode();
        final JsonField aJsonField = new JsonField(name, value);
        final JsonField anEqualJsonField = new JsonField(name, value);
        assertThat(aJsonField, equalTo(anEqualJsonField));
        assertThat(aJsonField.hashCode(), equalTo(anEqualJsonField.hashCode()));
    }

    @Test
    public void twoJsonFieldsWithTheEqualNamesAndDifferentValuesAreNotEqual() throws Exception {
        final JsonStringNode name = aStringNode();
        assertThat(new JsonField(name, aJsonNode()), not(equalTo(new JsonField(name, aJsonNode()))));
    }

    @Test
    public void twoJsonFieldsWithTheDifferentNamesAndEqualValuesAreNotEqual() throws Exception {
        final JsonNode value = aJsonNode();
        assertThat(new JsonField(aStringNode(), value), not(equalTo(new JsonField(aStringNode(), value))));
    }
}
