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

import static org.junit.Assert.*;

public final class JsonStringTest {

    @Test
    public void testConstructorRejectsNullValue() {
        try {
            JsonNodeFactories.string(null);
            fail("Constructing a JsonTextNode with a null argument should throw an IllegalArgumentException.");
        } catch (final NullPointerException e) {
            // expect to end up here
        }
    }

    @Test
    public void testEquals() {
        assertEquals(JsonNodeFactories.string("co"), JsonNodeFactories.string("co"));
        assertFalse(JsonNodeFactories.string("ho").equals(JsonNodeFactories.string("bo")));
    }

    @Test
    public void testHashCode() {
        assertEquals(JsonNodeFactories.string("po").hashCode(), JsonNodeFactories.string("po").hashCode());
    }

    @Test
    public void testToString() {
        JsonNodeFactories.string("lo");
    }
}
