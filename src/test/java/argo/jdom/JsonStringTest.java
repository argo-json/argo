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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonStringTest {

    @Test
    void testConstructorRejectsNullValue() {
        final NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> JsonNodeFactories.string(null));
        assertThat(nullPointerException.getMessage(), equalTo("Value is null"));
    }

    @Test
    void testEquals() {
        assertEquals(JsonNodeFactories.string("co"), JsonNodeFactories.string("co"));
        assertNotEquals(JsonNodeFactories.string("ho"), JsonNodeFactories.string("bo"));
    }

    @Test
    void testHashCode() {
        assertEquals(JsonNodeFactories.string("po").hashCode(), JsonNodeFactories.string("po").hashCode());
    }

    @Test
    void testToString() {
        JsonNodeFactories.string("lo");
    }
}
