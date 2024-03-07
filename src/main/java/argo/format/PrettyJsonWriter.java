/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import java.io.Writer;

import static argo.format.PrettyJsonPrinter.fieldSortingPrettyJsonPrinter;
import static argo.format.PrettyJsonPrinter.prettyJsonPrinter;

/**
 * JsonWriter that writes JSON in a human-readable form.  Instances of this class can safely be shared between threads.
 */
@Deprecated public final class PrettyJsonWriter extends AbstractJsonWriter { // TODO document deprecation

    private final String lineSeparator;
    private final boolean sortFields;

    public PrettyJsonWriter() {
        this(false);
    }

    @SuppressWarnings("SystemGetProperty")
    private PrettyJsonWriter(final boolean sortFields) {
        lineSeparator = System.getProperty("line.separator");
        this.sortFields = sortFields;
    }

    JsonWriter withFieldSorting(final boolean sortFields) {
        return new PrettyJsonWriter(sortFields);
    }

    @Override
    AbstractJsonPrinter newJsonPrinter(final Writer writer) {
        return sortFields ? fieldSortingPrettyJsonPrinter(writer, lineSeparator) : prettyJsonPrinter(writer, lineSeparator);
    }

}
