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

/**
 * A {@code WriteableJsonArray} is called back with an {@code ArrayWriter} with which to write itself.
 */
public interface WriteableJsonArray {

    /**
     * Callback to request that this {@code WriteableJsonArray} writes its elements using the given {@code ArrayWriter}.
     *
     * @param arrayWriter the {@code ArrayWriter} to output elements to.
     * @throws IOException if there was a problem writing to the {@code ArrayWriter}.
     */
    void writeTo(ArrayWriter arrayWriter) throws IOException;
}
