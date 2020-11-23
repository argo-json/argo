/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNumberTest {

    @Test
    void testConstructorRejectsNullValue() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> JsonNodeFactories.number((String) null));
        assertThat(nullPointerException.getMessage(), equalTo("Attempt to construct a JsonNumber with a null value."));
    }

    @Test
    void testEquals() {
        assertEquals(JsonNodeFactories.number("0"), JsonNodeFactories.number("0"));
        assertNotEquals(JsonNodeFactories.number("0"), JsonNodeFactories.number("1"));
    }

    @Test
    void testHashCode() {
        assertEquals(JsonNodeFactories.number("0").hashCode(), JsonNodeFactories.number("0").hashCode());
    }

    @Test
    void testToString() {
        assertThat(JsonNodeFactories.number("0").toString(), is(not(nullValue())));
    }
}
