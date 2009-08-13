/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import static argo.jdom.JsonNodeBuilders.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public final class JsonNodeBuildersTest {

    @Test
    public void nullBuilderBuildsNull() throws Exception {
        assertThat(aJsonNull().build(), equalTo(JsonNodeFactory.aJsonNull()));
    }

    @Test
    public void trueBuilderBuildsTrue() throws Exception {
        assertThat(aJsonTrue().build(), equalTo(JsonNodeFactory.aJsonTrue()));
    }

    @Test
    public void falseBuilderBuildsFalse() throws Exception {
        assertThat(aJsonFalse().build(), equalTo(JsonNodeFactory.aJsonFalse()));
    }

    @Test
    public void numberBuilderBuildsANumber() throws Exception {
        assertThat(aJsonNumber("10.1").build(), equalTo(JsonNodeFactory.aJsonNumber("10.1")));
    }

    @Test
    public void stringBuilderBuildsAString() throws Exception {
        assertThat(aJsonString("Hello").build(), equalTo(JsonNodeFactory.aJsonString("Hello")));
    }

    @Test
    public void arrayBuilderBuildsAnArrayWithNoElements() throws Exception {
        assertThat(aJsonArray().build(), equalTo(JsonNodeFactory.aJsonArray(new LinkedList<JsonNode>())));
    }

    @Test
    public void arrayBuilderBuildsAnArrayWithElements() throws Exception {
        assertThat(
                aJsonArray()
                        .withElement(aJsonString("Bob"))
                        .build()
                , equalTo(
                        JsonNodeFactory.aJsonArray(
                                new LinkedList<JsonNode>(
                                        Arrays.asList(JsonNodeFactory.aJsonString("Bob"))
                                )
                        )
                ));
    }

    @Test
    public void objectBuilderBuildsAnOjectWithNoFields() throws Exception {
        assertThat(aJsonObject().build(), equalTo(JsonNodeFactory.aJsonObject(new HashMap<JsonStringNode, JsonNode>())));
    }

    @Test
    public void objectBuilderBuildsAnObjectWithFields() throws Exception {
        assertThat(
                aJsonObject()
                        .withField("Hunky", aJsonString("dory"))
                        .build()
                , equalTo(
                        JsonNodeFactory.aJsonObject(
                                new HashMap<JsonStringNode, JsonNode>() {{
                                    put(JsonNodeFactory.aJsonString("Hunky"), JsonNodeFactory.aJsonString("dory"));
                                }}
                        )
                ));
    }
}
