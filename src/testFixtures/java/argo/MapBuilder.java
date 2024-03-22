/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

    private final Map<K, V> map;

    private MapBuilder() {
        map = new HashMap<>();
    }

    private MapBuilder(final Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> mapBuilder() {
        return new MapBuilder<>();
    }

    public static <K, V> MapBuilder<K, V> mapBuilder(final K key, final V value) {
        return new MapBuilder<K, V>().put(key, value);
    }

    public MapBuilder<K, V> put(final K key, final V value) {
        final Map<K, V> updated = new HashMap<>(map);
        updated.put(key, value);
        return new MapBuilder<>(updated);
    }

    public Map<K, V> build() {
        return new HashMap<>(map);
    }
}
