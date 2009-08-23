/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeSelectors;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

public final class SimpleJdomExamples {
    @Test
    public void ParseSimpleExample() throws Exception {
        final String jsonText = FileUtils.readFileToString(new File(this.getClass().getResource("SimpleExample.json").getFile()));
JsonNode json = new JdomParser().parse(jsonText);
String secondSingle = JsonNodeSelectors.aStringNode("singles", 1)
        .getValue(json);
        System.out.println("secondSingle = " + secondSingle);
    }
}
