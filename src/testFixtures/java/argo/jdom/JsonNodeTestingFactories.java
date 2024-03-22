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

import net.sourceforge.ickles.RandomListMemberSupplier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static argo.TestingFactories.randomSupplierSwitcher;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNumberNodeTestingFactories.aNumberNode;
import static argo.jdom.JsonStringNodeTestingFactories.aStringNode;
import static java.util.stream.Collectors.toList;

public final class JsonNodeTestingFactories {

    private static final Random RANDOM = new Random();
    private static final Supplier<JsonNode> RANDOM_LEAF_NODE = randomSupplierSwitcher(
            JsonStringNodeTestingFactories::aStringNode,
            JsonNumberNodeTestingFactories::aNumberNode,
            JsonNodeFactories::nullNode,
            JsonNodeFactories::trueNode,
            JsonNodeFactories::falseNode
    );

    private static final Supplier<Function<Integer, JsonNode>> RANDOM_NODE = new RandomListMemberSupplier<>(Arrays.asList(
            JsonNodeTestingFactories::anArrayNode,
            JsonNodeTestingFactories::anObjectNode,
            integer -> aStringNode(),
            integer -> aNumberNode(),
            integer -> nullNode(),
            integer -> trueNode(),
            integer -> falseNode()
    ));

    private JsonNodeTestingFactories() {
    }

    public static JsonNode anArrayNode() {
        return anArrayNode(10);
    }

    private static JsonNode anArrayNode(final int maxDepth) {
        return array(someJsonNodes(maxDepth));
    }

    public static JsonNode anObjectNode() {
        return anObjectNode(10);
    }

    private static JsonNode anObjectNode(final int maxDepth) {
        return object(someJsonFields(maxDepth));
    }

    public static JsonNode aJsonNode() {
        return aJsonNode(10);
    }

    public static JsonNode aJsonNodeDifferentTo(JsonNode jsonNode) {
        JsonNode result;
        do {
            result = aJsonNode();
        } while (result.equals(jsonNode));
        return result;
    }

    private static JsonNode aJsonNode(int maxDepth) {
        if (maxDepth <= 0) {
            return RANDOM_LEAF_NODE.get();
        } else {
            return RANDOM_NODE.get().apply(maxDepth - 1);
        }
    }

    public static Collection<JsonNode> someJsonNodes() {
        return someJsonNodes(5);
    }

    private static Collection<JsonNode> someJsonNodes(final int maxDepth) {
        return Stream.generate(() -> aJsonNode(maxDepth)).limit(RANDOM.nextInt(5)).collect(toList());
    }

    public static Collection<JsonField> someJsonFields() {
        return someJsonFields(5);
    }

    private static Collection<JsonField> someJsonFields(final int maxDepth) {
        return Stream.generate(() -> field(aStringNode(), aJsonNode(maxDepth))).limit(RANDOM.nextInt(5)).collect(toList());
    }


}
