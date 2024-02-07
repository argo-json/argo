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

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static argo.TestingFactories.aString;
import static argo.jdom.JsonNodeFactories.string;
import static java.util.Arrays.asList;

public final class JsonStringNodeTestBuilder {

    private static final Random RANDOM = new Random();

    public static JsonStringNode aStringNode() {
        return string(aString());
    }

    public static JsonStringNode aStringNodeDifferentTo(JsonStringNode... jsonStringNode) {
        final Set<JsonStringNode> exclusions = new HashSet<>(asList(jsonStringNode));
        JsonStringNode result;
        do {
            result = string(aString());
        } while (exclusions.contains(result));
        return result;
    }

    public static String aNonEmptyString() {
        return RandomStringUtils.random(RANDOM.nextInt(20) + 1);
    }

    private JsonStringNodeTestBuilder() {
    }
}
