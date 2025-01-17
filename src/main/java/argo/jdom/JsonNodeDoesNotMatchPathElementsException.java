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

import argo.JsonGenerator;

import static argo.JsonGenerator.JsonGeneratorStyle.COMPACT;
import static argo.jdom.JsonNodeDoesNotMatchChainedJsonNodeSelectorException.getShortFormFailPath;

final class JsonNodeDoesNotMatchPathElementsException extends JsonNodeDoesNotMatchJsonNodeSelectorException {

    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(COMPACT);

    private JsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements, final JsonNode jsonNode) {
        super(formatMessage(delegate, pathElements, jsonNode));
    }

    static JsonNodeDoesNotMatchPathElementsException jsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements, final JsonNode jsonNode) {
        return new JsonNodeDoesNotMatchPathElementsException(delegate, pathElements, jsonNode);
    }

    private static String formatMessage(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements, final JsonNode jsonNode) {
        return delegate.failPath.size() == 1 && pathElements.length == 1
                ? "Failed to find " + delegate.failedNode.toString() + " while resolving [" + commaSeparate(pathElements) + "] in " + JSON_GENERATOR.generate(jsonNode)
                : "Failed to find " + delegate.failedNode.toString() + " at [" + getShortFormFailPath(delegate.failPath) + "] while resolving [" + commaSeparate(pathElements) + "] in " + JSON_GENERATOR.generate(jsonNode);
    }

    private static String commaSeparate(final Object[] pathElements) {
        final StringBuilder result = new StringBuilder();
        boolean firstElement = true;
        for (final Object pathElement : pathElements) {
            if (!firstElement) {
                result.append('.');
            }
            firstElement = false;
            if (pathElement instanceof String) {
                result.append('"').append(pathElement).append('"');
            } else {
                result.append(pathElement);
            }
        }
        return result.toString();
    }
}
