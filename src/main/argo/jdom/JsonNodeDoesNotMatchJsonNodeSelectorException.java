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

import java.util.LinkedList;
import java.util.List;

public final class JsonNodeDoesNotMatchJsonNodeSelectorException extends IllegalArgumentException {

    private final Functor failedNode;
    private final List<JsonNodeSelector> failPath;

    public JsonNodeDoesNotMatchJsonNodeSelectorException(final Functor failedNode) {
        super();
        this.failedNode = failedNode;
        this.failPath = new LinkedList<JsonNodeSelector>();
    }

    public JsonNodeDoesNotMatchJsonNodeSelectorException(final JsonNodeDoesNotMatchJsonNodeSelectorException e, final JsonNodeSelector parentJsonNodeSelector) {
        super();
        this.failedNode = e.failedNode;
        this.failPath = new LinkedList<JsonNodeSelector>(e.failPath);
        failPath.add(parentJsonNodeSelector);
    }

    @Override
    public String toString() {
        return "JsonNodeDoesNotMatchJsonNodeSelectorException{" +
                "failedNode=" + failedNode +
                ", failPath=" + failPath +
                '}';
    }

    public List<Object> getFailPath() {
        throw new UnsupportedOperationException("Mark didn't implement.");
    }
}
