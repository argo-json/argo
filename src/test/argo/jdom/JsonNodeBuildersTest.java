/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import static argo.jdom.JsonNodeBuilders.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public final class JsonNodeBuildersTest {

    @Test
    public void nullBuilderBuildsNull() throws Exception {
        assertThat(aNullBuilder().build(), equalTo(JsonNodeFactories.aJsonNull()));
    }

    @Test
    public void trueBuilderBuildsTrue() throws Exception {
        assertThat(aTrueBuilder().build(), equalTo(JsonNodeFactories.aJsonTrue()));
    }

    @Test
    public void falseBuilderBuildsFalse() throws Exception {
        assertThat(aFalseBuilder().build(), equalTo(JsonNodeFactories.aJsonFalse()));
    }

    @Test
    public void numberBuilderBuildsANumber() throws Exception {
        assertThat(aNumberBuilder("10.1").build(), equalTo(JsonNodeFactories.aJsonNumber("10.1")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void numberBuilderFailsEarlyOnAnInvalidNumber() throws Exception {
        assertThat(aNumberBuilder("-010.1e12").build(), equalTo(JsonNodeFactories.aJsonNumber("10.1")));
    }

    @Test
    public void stringBuilderBuildsAString() throws Exception {
        assertThat(aStringBuilder("Hello").build(), equalTo(JsonNodeFactories.aJsonString("Hello")));
    }

    @Test
    public void arrayBuilderBuildsAnArrayWithNoElements() throws Exception {
        assertThat(anArrayBuilder().build(), equalTo(JsonNodeFactories.aJsonArray(new LinkedList<JsonNode>())));
    }

    @Test
    public void arrayBuilderBuildsAnArrayWithElements() throws Exception {
        assertThat(
                anArrayBuilder()
                        .withElement(aStringBuilder("Bob"))
                        .build()
                , equalTo(
                JsonNodeFactories.aJsonArray(
                        new LinkedList<JsonNode>(
                                Arrays.asList(JsonNodeFactories.aJsonString("Bob"))
                        )
                )
        ));
    }

    @Test
    public void objectBuilderBuildsAnObjectWithNoFields() throws Exception {
        assertThat(anObjectBuilder().build(), equalTo(JsonNodeFactories.aJsonObject(new HashMap<JsonStringNode, JsonNode>())));
    }

    @Test
    public void objectBuilderBuildsAnObjectWithFields() throws Exception {
        assertThat(
                anObjectBuilder()
                        .withField("Hunky", aStringBuilder("dory"))
                        .build()
                , equalTo(
                JsonNodeFactories.aJsonObject(
                        new HashMap<JsonStringNode, JsonNode>() {{
                            put(JsonNodeFactories.aJsonString("Hunky"), JsonNodeFactories.aJsonString("dory"));
                        }}
                )
        ));
    }
}
