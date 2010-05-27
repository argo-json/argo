/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import static argo.jdom.JsonNodeDoesNotMatchChainedJsonNodeSelectorException.getShortFormFailPath;

public final class JsonNodeDoesNotMatchPathElementsException extends JsonNodeDoesNotMatchJsonNodeSelectorException {

    static JsonNodeDoesNotMatchPathElementsException jsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements) {
        return new JsonNodeDoesNotMatchPathElementsException(delegate, pathElements);
    }

    private JsonNodeDoesNotMatchPathElementsException(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements) {
        super(formatMessage(delegate, pathElements));
    }

    private static String formatMessage(final JsonNodeDoesNotMatchChainedJsonNodeSelectorException delegate, final Object[] pathElements) {
        return "Failed to match any JSON node at [" + getShortFormFailPath(delegate.failPath) + "] while resolving [" + commaSeparate(pathElements) + "].";
    }

    private static String commaSeparate(final Object[] pathElements) {
        final StringBuilder result = new StringBuilder();
        boolean firstElement = true;
        for (Object pathElement : pathElements) {
            if (!firstElement) {
                result.append(".");
            }
            firstElement = false;
            if (pathElement instanceof String) {
                result.append("\"").append(pathElement).append("\"");
            } else {
                result.append(pathElement);
            }
        }
        return result.toString();
    }
}
