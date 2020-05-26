/*
 *  Copyright  2020 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package samples;

import argo.jdom.JdomParser;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

final class SimpleJdomExamples {

    @Test
    void parseSimpleExample() throws Exception {
        final String jsonText = readFileToString(new File(this.getClass().getResource("SimpleExample.json").getFile()), UTF_8);
        final String secondSingle = new JdomParser().parse(jsonText).getStringValue("singles", 1);
        assertThat(secondSingle, equalTo("Agadoo"));
    }
}
