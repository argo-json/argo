/*
 *  Copyright  2019 Mark Slater
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

final class JsonNumberTest {

    @Test
    void testConstructorRejectsNullValue() {
        try {
            JsonNodeFactories.number((String) null);
            fail("Constructing a JsonNumber with a null argument should throw a NullPointerException.");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), equalTo("Attempt to construct a JsonNumber with a null value."));
            // expect to end up here
        }
    }

    @Test
    void testEquals() {
        assertEquals(JsonNodeFactories.number("0"), JsonNodeFactories.number("0"));
        assertFalse(JsonNodeFactories.number("0").equals(JsonNodeFactories.number("1")));
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
