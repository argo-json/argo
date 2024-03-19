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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class JsonNodeDoesNotMatchChainedJsonNodeSelectorException extends JsonNodeDoesNotMatchJsonNodeSelectorException {

    final Functor<?, ?> failedNode;
    final List<JsonNodeSelector<?, ?>> failPath;

    private JsonNodeDoesNotMatchChainedJsonNodeSelectorException(final Functor<?, ?> failedNode, final List<JsonNodeSelector<?, ?>> failPath) {
        super("Failed to match [" + failedNode.shortForm() + "] at [" + getShortFormFailPath(failPath) + "]");
        this.failedNode = failedNode;
        this.failPath = failPath;
    }

    static JsonNodeDoesNotMatchJsonNodeSelectorException createJsonNodeDoesNotMatchJsonNodeSelectorException(final Functor<?, ?> failedNode) {
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(failedNode, Collections.<JsonNodeSelector<?, ?>>emptyList());
    }

    static JsonNodeDoesNotMatchJsonNodeSelectorException createChainedJsonNodeDoesNotMatchJsonNodeSelectorException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e,
                                                                                                                    final JsonNodeSelector<?, ?> parentJsonNodeSelector) {
        final List<JsonNodeSelector<?, ?>> chainedFailPath = new ArrayList<JsonNodeSelector<?, ?>>(e.failPath.size() + 1);
        chainedFailPath.addAll(e.failPath);
        chainedFailPath.add(parentJsonNodeSelector);
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(e.failedNode, chainedFailPath);
    }

    static JsonNodeDoesNotMatchJsonNodeSelectorException createUnchainedJsonNodeDoesNotMatchJsonNodeSelectorException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException e,
                                                                                                                      final JsonNodeSelector<?, ?> parentJsonNodeSelector) {
        return new JsonNodeDoesNotMatchChainedJsonNodeSelectorException(e.failedNode, Collections.<JsonNodeSelector<?, ?>>singletonList(parentJsonNodeSelector));
    }

    static String getShortFormFailPath(final List<JsonNodeSelector<?, ?>> failPath) {
        final StringBuilder result = new StringBuilder();
        for (int i = failPath.size() - 1; i >= 0; i--) {
            result.append(failPath.get(i).shortForm());
            if (i != 0) {
                result.append('.');
            }
        }
        return result.toString();
    }

}
