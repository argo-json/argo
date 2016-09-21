/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import net.sourceforge.ickles.RandomListMemberSupplier;
import net.sourceforge.ickles.RandomSupplierSwitcher;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;
import static java.util.Arrays.asList;

public final class JsonNodeTestBuilder {

    private static final Random RANDOM = new Random();
    private static final Supplier<JsonNode> RANDOM_LEAF_NODE = new RandomSupplierSwitcher<>(
            JsonStringNodeTestBuilder::aStringNode,
            JsonNumberNodeTestBuilder::aNumberNode,
            JsonNodeFactories::nullNode,
            JsonNodeFactories::trueNode,
            JsonNodeFactories::falseNode
    );

    private static final Supplier<Function<Integer, JsonRootNode>> RANDOM_ROOT_NODE = new RandomListMemberSupplier<>(asList(
            JsonNodeTestBuilder::anArrayNode,
            JsonNodeTestBuilder::anObjectNode
    ));

    private static final Supplier<Function<Integer, JsonNode>> RANDOM_NODE = new RandomListMemberSupplier<>(asList(
            JsonNodeTestBuilder::aJsonRootNode,
            integer -> aJsonLeafNode()
    ));

    private static JsonRootNode anArrayNode(final int maxDepth) {
        return array(new ArrayList<JsonNode>() {{
            for (int i = 0; i < RANDOM.nextInt(5); i++) {
                add(aJsonNode(maxDepth));
            }
        }});
    }

    private static JsonRootNode anObjectNode(final int maxDepth) {
        return object(new ArrayList<JsonField>() {{
            for (int i = 0; i < RANDOM.nextInt(5); i++) {
                add(field(aStringNode(), aJsonNode(maxDepth)));
            }
        }});
    }

    public static JsonNode aJsonLeafNode() {
        return RANDOM_LEAF_NODE.get();
    }

    public static JsonRootNode aJsonRootNode() {
        return aJsonRootNode(10);
    }

    private static JsonRootNode aJsonRootNode(final int defaultDepth) {
        return RANDOM_ROOT_NODE.get().apply(defaultDepth);
    }

    public static JsonNode aJsonNode() {
        return aJsonNode(10);
    }

    private static JsonNode aJsonNode(int maxDepth) {
        if (maxDepth <= 0) {
            return aJsonLeafNode();
        } else {
            return RANDOM_NODE.get().apply(maxDepth - 1);
        }
    }
}
