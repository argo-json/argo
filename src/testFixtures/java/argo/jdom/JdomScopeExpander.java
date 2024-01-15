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

import argo.saj.InvalidSyntaxException;
import argo.staj.StajParser;

import java.io.IOException;

public final class JdomScopeExpander {

    private JdomScopeExpander() {
    }

    public static JsonNode parse(final JdomParser jdomParser, final StajParser stajParser) throws InvalidSyntaxException, IOException {
        return jdomParser.parse(stajParser);
    }

}