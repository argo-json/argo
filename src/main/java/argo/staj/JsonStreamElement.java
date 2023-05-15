/*
 *  Copyright 2023 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import java.io.IOException;
import java.io.Reader;

/**
 * An element of a JSON document.
 */
public abstract class JsonStreamElement {

    private static final JsonStreamElement START_DOCUMENT = nonTextJsonStreamElement(JsonStreamElementType.START_DOCUMENT);
    private static final JsonStreamElement END_DOCUMENT = nonTextJsonStreamElement(JsonStreamElementType.END_DOCUMENT);
    private static final JsonStreamElement START_ARRAY = nonTextJsonStreamElement(JsonStreamElementType.START_ARRAY);
    private static final JsonStreamElement END_ARRAY = nonTextJsonStreamElement(JsonStreamElementType.END_ARRAY);
    private static final JsonStreamElement START_OBJECT = nonTextJsonStreamElement(JsonStreamElementType.START_OBJECT);
    private static final JsonStreamElement END_OBJECT = nonTextJsonStreamElement(JsonStreamElementType.END_OBJECT);
    private static final JsonStreamElement END_FIELD = nonTextJsonStreamElement(JsonStreamElementType.END_FIELD);
    private static final JsonStreamElement TRUE = nonTextJsonStreamElement(JsonStreamElementType.TRUE);
    private static final JsonStreamElement FALSE = nonTextJsonStreamElement(JsonStreamElementType.FALSE);
    private static final JsonStreamElement NULL = nonTextJsonStreamElement(JsonStreamElementType.NULL);

    private final JsonStreamElementType jsonStreamElementType;

    private static JsonStreamElement nonTextJsonStreamElement(final JsonStreamElementType jsonStreamElementType) {
        return new JsonStreamElement(jsonStreamElementType) {
            @Override
            public boolean hasText() {
                return false;
            }

            public Reader reader() {
                throw new IllegalStateException(jsonStreamElementType().name() + " does not have text associated with it");
            }

            @Override
            public String toString() {
                return "JsonStreamElement jsonStreamElementType: " + jsonStreamElementType;
            }
        };
    }

    private static JsonStreamElement textJsonStreamElement(final JsonStreamElementType jsonStreamElementType, final Reader reader) {
        return new JsonStreamElement(jsonStreamElementType) {
            @Override
            public boolean hasText() {
                return true;
            }

            @Override
            public Reader reader() {
                return reader;
            }

            @Override
            public String toString() {
                return "JsonStreamElement jsonStreamElementType: " + jsonStreamElementType + ", text: " + text();
            }
        };
    }

    static JsonStreamElement startDocument() {
        return START_DOCUMENT;
    }

    static JsonStreamElement endDocument() {
        return END_DOCUMENT;
    }

    static JsonStreamElement startArray() {
        return START_ARRAY;
    }

    static JsonStreamElement endArray() {
        return END_ARRAY;
    }

    static JsonStreamElement startObject() {
        return START_OBJECT;
    }

    static JsonStreamElement endObject() {
        return END_OBJECT;
    }

    static JsonStreamElement startField(final Reader reader) {
        return textJsonStreamElement(JsonStreamElementType.START_FIELD, reader);
    }

    static JsonStreamElement endField() {
        return END_FIELD;
    }

    static JsonStreamElement string(final Reader reader) {
        return textJsonStreamElement(JsonStreamElementType.STRING, reader);
    }

    static JsonStreamElement number(final Reader reader) {
        return textJsonStreamElement(JsonStreamElementType.NUMBER, reader);
    }

    static JsonStreamElement trueValue() {
        return TRUE;
    }

    static JsonStreamElement falseValue() {
        return FALSE;
    }

    static JsonStreamElement nullValue() {
        return NULL;
    }

    private JsonStreamElement(final JsonStreamElementType jsonStreamElementType) {
        this.jsonStreamElementType = jsonStreamElementType;
    }

    /**
     * Gets the type of this element.
     *
     * @return the type of the element.
     */
    public final JsonStreamElementType jsonStreamElementType() {
        return jsonStreamElementType;
    }

    /**
     * Determines whether the element has text.
     *
     * @return true if the element has text.
     */
    public abstract boolean hasText();

    /**
     * Gets a Reader to stream the text associated with the element.
     *
     * @return a reader of the text associated with the element.
     * @throws IllegalStateException if the element doesn't have any text associated with it.
     */
    public abstract Reader reader();

    private final Object lock = new Object();
    private String text;

    /**
     * Gets the text associated with the element.
     *
     * @return the text associated with the element.
     * @throws IllegalStateException if the element doesn't have any text associated with it.
     */
    public final String text() {
        // TODO constants for "", "0", and "1"
        synchronized (lock) {
            if (text == null) {
                final StringBuilder stringBuilder = new StringBuilder();
                final char[] buffer = new char[8096];
                try {
                    final Reader reader = reader();
                    try {
                        int c;
                        while((c = reader.read(buffer)) != -1) {
                            stringBuilder.append(buffer, 0, c);
                        }
                    } finally {
                        reader.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                text = stringBuilder.toString();
            }
        }
        return text;
    }
}
