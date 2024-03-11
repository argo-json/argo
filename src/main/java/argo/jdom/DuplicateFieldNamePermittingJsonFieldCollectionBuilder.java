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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class DuplicateFieldNamePermittingJsonFieldCollectionBuilder implements JsonFieldCollectionBuilder<Builder<JsonField>> {
    private final List<Builder<? extends JsonField>> fieldBuilders = new LinkedList<Builder<? extends JsonField>>();

    public void add(final Builder<JsonField> jsonFieldBuilder) {
        fieldBuilders.add(jsonFieldBuilder);
    }

    public Collection<JsonField> build() {
        return new BuildingCollection<JsonField>(fieldBuilders);
    }
}
