/*
 * Copyright 2015 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import static argo.jdom.JsonNodeFactories.field;

final class JsonFieldBuilder {

    private final JsonStringNode key;
    private JsonNodeBuilder valueBuilder;

    private JsonFieldBuilder(final JsonStringNode key) {
        this.key = key;
    }

    static JsonFieldBuilder aJsonFieldBuilder(final JsonStringNode key) {
        return new JsonFieldBuilder(key);
    }

    JsonFieldBuilder withValue(final JsonNodeBuilder jsonNodeBuilder) {
        valueBuilder = jsonNodeBuilder;
        return this;
    }

    JsonStringNode buildKey() {
        return key;
    }

    JsonField build() {
        return field(buildKey(), valueBuilder.build());
    }
}
