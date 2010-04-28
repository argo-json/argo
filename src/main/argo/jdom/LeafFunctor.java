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

abstract class LeafFunctor<T, V> implements Functor<T, V> {
    public final V applyTo(final T jsonNode) {
        if (!matchesNode(jsonNode)) {
            throw new JsonNodeDoesNotMatchJsonNodeSelectorException(this);
        } else {
            return typeSafeApplyTo(jsonNode);
        }
    }

    protected abstract V typeSafeApplyTo(final T jsonNode);
}