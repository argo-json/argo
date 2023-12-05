/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo;

import argo.saj.JsonListener;
import org.openjdk.jmh.infra.Blackhole;

public final class BlackholeJsonListener implements JsonListener {

    private final Blackhole blackhole;
    public BlackholeJsonListener(final Blackhole blackhole) {
        this.blackhole = blackhole;
    }

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

    public void startField(String name) {
        blackhole.consume(name);
    }

    public void endField() {
    }

    public void stringValue(String value) {
        blackhole.consume(value);
    }

    public void numberValue(String value) {
        blackhole.consume(value);
    }

    public void trueValue() {
    }

    public void falseValue() {
    }

    public void nullValue() {
    }
}
