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

import argo.RandomFunctionSwitcher;
import argo.RandomSupplierSwitcher;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import java.util.ArrayList;
import java.util.Random;

import static argo.jdom.JsonNodeFactories.*;
import static argo.jdom.JsonNumberNodeTestBuilder.aNumberNode;
import static argo.jdom.JsonStringNodeTestBuilder.aStringNode;

public final class JsonNodeTestBuilder {

    private static final Random RANDOM = new Random();
    private static final Supplier<JsonNode> RANDOM_LEAF_NODE = new RandomSupplierSwitcher<JsonNode>(
            new Supplier<JsonNode>() {
                public JsonNode get() {
                    return aStringNode();
                }
            },
            new Supplier<JsonNode>() {
                public JsonNode get() {
                    return aNumberNode();
                }
            },
            new Supplier<JsonNode>() {
                public JsonNode get() {
                    return nullNode();
                }
            },
            new Supplier<JsonNode>() {
                public JsonNode get() {
                    return trueNode();
                }
            },
            new Supplier<JsonNode>() {
                public JsonNode get() {
                    return falseNode();
                }
            }
    );

    private static final Function<Integer, JsonRootNode> RANDOM_ROOT_NODE = new RandomFunctionSwitcher<Integer, JsonRootNode>(
            new Function<Integer, JsonRootNode>() {
                public JsonRootNode apply(final Integer maxDepth) {
                    return anArrayNode(maxDepth);
                }
            },
            new Function<Integer, JsonRootNode>() {
                public JsonRootNode apply(final Integer maxDepth) {
                    return anObjectNode(maxDepth);
                }
            }
    );

    private static final Function<Integer, JsonNode> RANDOM_NODE = new RandomFunctionSwitcher<Integer, JsonNode>(
            new Function<Integer, JsonNode>() {
                public JsonNode apply(final Integer maxDepth) {
                    return aJsonRootNode(maxDepth);
                }
            },
            new Function<Integer, JsonNode>() {
                public JsonNode apply(final Integer integer) {
                    return aJsonLeafNode();
                }
            }
    );

    public static JsonRootNode anArrayNode() {
        return anArrayNode(10);
    }

    private static JsonRootNode anArrayNode(final int maxDepth) {
        return array(new ArrayList<JsonNode>() {{
            for (int i = 0; i < RANDOM.nextInt(5); i++) {
                add(aJsonNode(maxDepth));
            }
        }});
    }

    public static JsonRootNode anObjectNode() {
        return anObjectNode(10);
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
        return RANDOM_ROOT_NODE.apply(defaultDepth);
    }

    public static JsonNode aJsonNode() {
        return aJsonNode(10);
    }

    private static JsonNode aJsonNode(int maxDepth) {
        if (maxDepth <= 0) {
            return aJsonLeafNode();
        } else {
            return RANDOM_NODE.apply(maxDepth - 1);
        }
    }
}
