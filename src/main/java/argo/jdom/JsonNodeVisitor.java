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

import java.util.List;

/**
 * Called back using the method that matches the type of element being visited.
 */
public interface JsonNodeVisitor {

    /**
     * Called by JSON objects.
     *
     * @param fields the members of the object.
     */
    void object(List<JsonField> fields);

    /**
     * Called by JSON arrays.
     *
     * @param elements the elements of the array.
     */
    void array(List<JsonNode> elements);

    /**
     * Called by JSON strings.
     *
     * @param value the text of the string.
     */
    void string(String value);

    /**
     * Called by JSON numbers.
     *
     * @param value the text of the number.
     *
     * @see JsonNumberUtils for utility methods for converting JSON number text into Java numeric types.
     */
    void number(String value);

    /**
     * Called by JSON true values.
     */
    void trueNode();

    /**
     * Called by JSON false values.
     */
    void falseNode();

    /**
     * Called by JSON null values.
     */
    void nullNode();
}
