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

import java.util.HashMap;
import java.util.LinkedList;

import static argo.jdom.JsonNodeBuilders.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class JsonNodeBuildersTest {

    @Test
    void nullBuilderBuildsNull() {
        assertThat(aNullBuilder().build(), equalTo(JsonNodeFactories.nullNode()));
    }

    @Test
    void trueBuilderBuildsTrue() {
        assertThat(aTrueBuilder().build(), equalTo(JsonNodeFactories.trueNode()));
    }

    @Test
    void falseBuilderBuildsFalse() {
        assertThat(aFalseBuilder().build(), equalTo(JsonNodeFactories.falseNode()));
    }

    @Test
    void numberBuilderBuildsANumber() {
        assertThat(aNumberBuilder("10.1").build(), equalTo(JsonNodeFactories.number("10.1")));
    }

    @Test
    void numberBuilderFailsEarlyOnAnInvalidNumber() {
        assertThrows(IllegalArgumentException.class, () -> aNumberBuilder("-010.1e12").build());
    }

    @Test
    void stringBuilderBuildsAString() {
        assertThat(aStringBuilder("Hello").build(), equalTo(JsonNodeFactories.string("Hello")));
    }

    @Test
    void arrayBuilderBuildsAnArrayWithNoElements() {
        assertThat(anArrayBuilder().build(), equalTo(JsonNodeFactories.array(new LinkedList<>())));
    }

    @Test
    void arrayBuilderBuildsAnArrayWithElements() {
        assertThat(
                anArrayBuilder()
                        .withElement(aStringBuilder("Bob"))
                        .build()
                , equalTo(
                JsonNodeFactories.array(new LinkedList<JsonNode>(
                        singletonList(JsonNodeFactories.string("Bob"))
                ))
        ));
    }

    @Test
    void objectBuilderBuildsAnObjectWithNoFields() {
        assertThat(anObjectBuilder().build(), equalTo(JsonNodeFactories.object(new HashMap<>())));
    }

    @Test
    void objectBuilderBuildsAnObjectWithFields() {
        assertThat(
                anObjectBuilder()
                        .withField("Hunky", aStringBuilder("dory"))
                        .build()
                , equalTo(
                JsonNodeFactories.object(new HashMap<JsonStringNode, JsonNode>() {{
                    put(JsonNodeFactories.string("Hunky"), JsonNodeFactories.string("dory"));
                }})
        ));
    }
}
