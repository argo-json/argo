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

import static argo.jdom.JsonNodeFactories.string;

/**
 * A field in a JSON object.  Immutable.
 */
public final class JsonField {
    private final JsonStringNode name;
    private final JsonNode value;

    /**
     * Constructs an instance of {@code JsonField} with the given name and value.
     *
     * @param name  a String representing the field name.
     * @param value any {@code JsonNode} representing the value of the field.
     */
    public JsonField(final String name, final JsonNode value) {
        this(name == null ? null : string(name), value);
    }

    /**
     * Constructs an instance of {@code JsonField} with the given name and value.
     *
     * @param name  a JSON string representing the field name.
     * @param value any {@code JsonNode} representing the value of the field.
     */
    public JsonField(final JsonStringNode name, final JsonNode value) {
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        this.name = name;
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        this.value = value;
    }

    /**
     * @return a JSON string representing the name.
     */
    public JsonStringNode getName() {
        return name;
    }

    /**
     * @return the String representation of the name of the field.
     */
    public String getNameText() {
        return name.getText();
    }

    /**
     * @return a {@code JsonNode} representing the value of the field.
     */
    public JsonNode getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        final JsonField thatJsonField = (JsonField) that;
        return name.equals(thatJsonField.name) && value.equals(thatJsonField.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JsonField{name=" + name + ", value=" + value + '}';
    }
}
