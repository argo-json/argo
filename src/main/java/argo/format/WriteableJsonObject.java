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
 * A {@code WriteableJsonObject} is called back with an {@code ObjectWriter} with which to write itself.
 */
public interface WriteableJsonObject {

    /**
     * Callback to request that this {@code WriteableJsonObject} writes its fields using the given {@code ObjectWriter}.
     *
     * @param objectWriter the {@code ObjectWriter} to output fields to.
     * @throws IOException if there was a problem writing to the {@code ObjectWriter}.
     */
    void writeTo(ObjectWriter objectWriter) throws IOException;
}
