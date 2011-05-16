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

public final class JdomParserTest {

    @Test
    public void parsesNumberBetweenZeroAndOne() throws Exception {
        new JdomParser().parse("{\"value\": 0.6}");
    }

    @Test
    public void parsesNumberWithALowerCaseExponent() throws Exception {
        new JdomParser().parse("{ \"PI\":3.141e-10}");
    }
}
