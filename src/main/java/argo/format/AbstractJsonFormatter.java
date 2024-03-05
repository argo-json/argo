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

import argo.internal.StringBuilderWriter;
import argo.jdom.JsonNode;

import java.io.IOException;

abstract class AbstractJsonFormatter implements JsonFormatter {
    public final String format(final JsonNode jsonNode) {
        final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        try {
            format(jsonNode, stringBuilderWriter);
        } catch (final IOException e) {
            throw new RuntimeException("Coding failure in Argo:  StringBuilderWriter threw an IOException", e);
        } finally {
            stringBuilderWriter.close();
        }
        return stringBuilderWriter.toString();
    }
}
