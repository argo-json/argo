/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import java.io.Reader;

public final class BlackHoleJsonListener implements JsonListener {

    public static final BlackHoleJsonListener BLACK_HOLE_JSON_LISTENER = new BlackHoleJsonListener();

    public void startDocument() {
    }

    public void endDocument() {
    }

    public void startArray() {
    }

    public void endArray() {
    }

    public void startObject() {
    }

    public void endObject() {
    }

    public void startField(Reader name) {
    }

    public void endField() {
    }

    public void stringValue(Reader value) {
    }

    public void numberValue(Reader value) {
    }

    public void trueValue() {
    }

    public void falseValue() {
    }

    public void nullValue() {
    }
}
