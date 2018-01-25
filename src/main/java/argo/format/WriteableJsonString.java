/*
 * Copyright 2018 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.format;

import java.io.IOException;
import java.io.Writer;

/**
 * A {@code WriteableJsonString} is called back with a {@code java.io.Writer} with which to write itself.
 */
public interface WriteableJsonString {
    /**
     * Callback to request that this {@code WriteableJsonString} writes itself using the given {@code Writer}.
     *
     * @param writer the {@code java.io.Writer} to output to.
     * @throws IOException if there was a problem writing to the {@code java.io.Writer}.
     */
    void writeTo(Writer writer) throws IOException;
}
