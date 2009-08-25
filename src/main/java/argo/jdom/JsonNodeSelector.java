/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

/**
 * Instances of <code>JsonNodeSelector</code> extract values from <code>Object</code>s of a specified type.
 *
 * <p>For example, given a <code>JsonNode</code> representing <code>{"Fee":{"fi":"fo"}}</code>,
 * <pre>
    anObjectNodeWithField("Fee")
        .withChild(anObjectNodeWithField("fi"))
        .withChild(aStringNode())
        .getValue(jsonNode)
 * </pre> will return the <code>String</code> "fo".</p>
 *
 * @param <T> The type of Object worked on.
 * @param <U> The type of Object returned.
 */
public final class JsonNodeSelector<T, U> {

    private final Functor<T, U> valueGetter;

    JsonNodeSelector(final Functor<T, U> valueGetter) {
        this.valueGetter = valueGetter;
    }

    public boolean matches(final T jsonNode) {
        return valueGetter.matchesNode(jsonNode);
    }

    public U getValue(final T argument) {
        if (!matches(argument)) {
            throw new IllegalArgumentException("Argument [" + argument + "] does not match the requirments of this selector.");
        } else {
            return valueGetter.applyTo(argument);
        }
    }

    public <V> JsonNodeSelector<T, V> with(final JsonNodeSelector<U, V> childJsonNodeSelector) {
        return new JsonNodeSelector<T,V>(new ChainedFunctor<T, U, V>(this, childJsonNodeSelector));
    }

    @Override
    public String toString() {
        return valueGetter.toString();
    }

}
