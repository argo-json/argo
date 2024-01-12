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

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static argo.TestingFactories.randomSupplierSwitcher;
import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public final class JsonNodeTestBuilder {

    private static final Random RANDOM = new Random();
    private static final Supplier<JsonNode> RANDOM_LEAF_NODE = randomSupplierSwitcher(
            JsonStringNodeTestBuilder::aStringNode,
            JsonNumberNodeTestBuilder::aNumberNode,
            JsonNodeFactories::nullNode,
            JsonNodeFactories::trueNode,
            JsonNodeFactories::falseNode
    );

    private static final Supplier<Function<Integer, JsonNode>> RANDOM_NODE = new RandomListMemberSupplier<>(asList(
            JsonNodeTestBuilder::anArrayNode,
            JsonNodeTestBuilder::anObjectNode,
            integer -> aStringNode(),
            integer -> aNumberNode(),
            integer -> nullNode(),
            integer -> trueNode(),
            integer -> falseNode()
    ));

    private static JsonNode anArrayNode(final int maxDepth) {
        return array(
                Stream.generate(() -> aJsonNode(maxDepth)).limit(RANDOM.nextInt(5)).collect(toList())
        );
    }

    private static JsonNode anObjectNode(final int maxDepth) {
        return object(
                Stream.generate(() -> field(aStringNode(), aJsonNode(maxDepth))).limit(RANDOM.nextInt(5)).collect(toList())
        );
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

    private JsonNodeTestBuilder() {
    }
}
