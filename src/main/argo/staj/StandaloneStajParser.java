/*
 * Copyright 2012 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.io.Reader;
import java.util.Iterator;
import java.util.Stack;

public final class StandaloneStajParser implements Iterator<JsonStreamElement> {

    private final PositionTrackingPushbackReader pushbackReader;
    private final Stack<JsonStreamElementType> stack = new Stack<JsonStreamElementType>();
    private JsonStreamElement current;
    private JsonStreamElement next;

    public StandaloneStajParser(final Reader in) {
        this.pushbackReader = new PositionTrackingPushbackReader(in);
    }

    public boolean hasNext() {
        if (current != null && current.jsonStreamElementType().equals(JsonStreamElementType.END_DOCUMENT)) {
            return false;
        }
        if (next == null) {
            next = getNextElement();
        }
        return true;
    }

    public JsonStreamElement next() {
        if (next != null) {
            current = next;
            next = null;
        } else {
            current = getNextElement();
        }
        return current;
    }

    private JsonStreamElement getNextElement() {
        if (current != null) {
            return current.jsonStreamElementType().parseNext(pushbackReader, stack);
        } else {
            return JsonStreamElementType.parseFirstElement(pushbackReader);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("StandaloneStajParser cannot remove elements from JSON it has parsed.");
    }
}
