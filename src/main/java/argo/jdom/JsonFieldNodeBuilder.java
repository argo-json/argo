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

import static argo.jdom.JsonNodeFactories.field;

final class JsonFieldNodeBuilder implements NamedJsonFieldBuilder {

    private final JsonStringNode name;
    private final JsonNodeBuilder<?> valueBuilder;

    private JsonFieldNodeBuilder(final JsonStringNode name, final JsonNodeBuilder<?> valueBuilder) {
        this.name = name;
        this.valueBuilder = valueBuilder;
    }

    static JsonFieldNodeBuilder aJsonFieldBuilder(final JsonStringNode key, final JsonNodeBuilder<?> valueBuilder) {
        return new JsonFieldNodeBuilder(key, valueBuilder);
    }

    public String name() {
        return name.getText();
    }

    public JsonField build() {
        return field(name, valueBuilder.build());
    }
}
