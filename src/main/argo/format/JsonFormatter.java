/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import argo.jdom.JsonField;
import argo.jdom.JsonRootNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A {@code JsonFormatter} provides operations to turn {@code JsonRootNode}s into valid JSON text.
 */
public interface JsonFormatter {

    FieldSorter DO_NOTHING_FIELD_SORTER = new FieldSorter() {
        public List<JsonField> sort(List<JsonField> unsorted) {
            return unsorted;
        }
    };

    Comparator<JsonField> JSON_FIELD_COMPARATOR = new Comparator<JsonField>() {
        public int compare(JsonField jsonField, JsonField jsonField1) {
            return jsonField.getName().compareTo(jsonField1.getName());
        }
    };

    FieldSorter ALPHABETIC_FIELD_SORTER = new FieldSorter() {
        public List<JsonField> sort(List<JsonField> unsorted) {
            final List<JsonField> sorted = new ArrayList<JsonField>(unsorted);
            Collections.sort(sorted, JSON_FIELD_COMPARATOR);
            return sorted;
        }
    };

    /**
     * Returns the specified {@code JsonRootNode} formatted as a String.
     *
     * @param jsonRootNode the {@code JsonRootNode} to format.
     * @return the specified {@code JsonRootNode} formatted as a String.
     */
    String format(JsonRootNode jsonRootNode);

    /**
     * Streams the specified {@code JsonRootNode} formatted to the specified {@code Writer}.
     *
     * @param jsonRootNode the {@code JsonRootNode} to format.
     * @param writer       the {@code Writer} to stream the formatted {@code JsonRootNode} to.
     * @throws IOException if there was a problem writing to the {@code Writer}.
     */
    void format(JsonRootNode jsonRootNode, Writer writer) throws IOException;

    static interface FieldSorter {
        List<JsonField> sort(final List<JsonField> unsorted);
    }
}
