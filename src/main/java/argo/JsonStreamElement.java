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

import java.io.IOException;
import java.io.Reader;

/**
 * An element of a JSON document.
 */
public abstract class JsonStreamElement {

    private final JsonStreamElementType jsonStreamElementType;

    private JsonStreamElement(final JsonStreamElementType jsonStreamElementType) {
        this.jsonStreamElementType = jsonStreamElementType;
    }

    static JsonStreamElement startField(final Reader reader) {
        return new TextJsonStreamElement.StartFieldJsonStreamElement(reader);
    }

    static JsonStreamElement string(final Reader reader) {
        return new TextJsonStreamElement.StringJsonStreamElement(reader);
    }

    static JsonStreamElement number(final Reader reader) {
        return new TextJsonStreamElement.NumberJsonStreamElement(reader);
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

    /**
     * Converts this element into a callback to a JsonListener.
     *
     * @param jsonListener the JsonListener to call back with the details of this element
     */
    public abstract void visit(JsonListener jsonListener);

    @Override
    public final String toString() {
        return "JsonStreamElement jsonStreamElementType: " + jsonStreamElementType;
    }

    abstract void close() throws IOException;

    static abstract class NonTextJsonStreamElement extends JsonStreamElement {
        static final JsonStreamElement START_DOCUMENT = new NonTextJsonStreamElement(JsonStreamElementType.START_DOCUMENT) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.startDocument();
            }
        };
        static final JsonStreamElement END_DOCUMENT = new NonTextJsonStreamElement(JsonStreamElementType.END_DOCUMENT) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.endDocument();
            }
        };
        static final JsonStreamElement START_ARRAY = new NonTextJsonStreamElement(JsonStreamElementType.START_ARRAY) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.startArray();
            }
        };
        static final JsonStreamElement END_ARRAY = new NonTextJsonStreamElement(JsonStreamElementType.END_ARRAY) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.endArray();
            }
        };
        static final JsonStreamElement START_OBJECT = new NonTextJsonStreamElement(JsonStreamElementType.START_OBJECT) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.startObject();
            }
        };
        static final JsonStreamElement END_OBJECT = new NonTextJsonStreamElement(JsonStreamElementType.END_OBJECT) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.endObject();
            }
        };
        static final JsonStreamElement END_FIELD = new NonTextJsonStreamElement(JsonStreamElementType.END_FIELD) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.endField();
            }

        };
        static final JsonStreamElement TRUE = new NonTextJsonStreamElement(JsonStreamElementType.TRUE) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.trueValue();
            }
        };
        static final JsonStreamElement FALSE = new NonTextJsonStreamElement(JsonStreamElementType.FALSE) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.falseValue();
            }
        };
        static final JsonStreamElement NULL = new NonTextJsonStreamElement(JsonStreamElementType.NULL) {
            @Override
            public void visit(final JsonListener jsonListener) {
                jsonListener.nullValue();
            }
        };

        private NonTextJsonStreamElement(final JsonStreamElementType jsonStreamElementType) {
            super(jsonStreamElementType);
        }

        @Override
        public final boolean hasText() {
            return false;
        }

        public final Reader reader() {
            throw new IllegalStateException(jsonStreamElementType().name() + " does not have text associated with it");
        }

        @Override
        final void close() {
        }
    }

    private static abstract class TextJsonStreamElement extends JsonStreamElement {

        private final Reader reader;

        private TextJsonStreamElement(final JsonStreamElementType jsonStreamElementType, final Reader reader) {
            super(jsonStreamElementType);
            this.reader = reader;
        }

        @Override
        public final boolean hasText() {
            return true;
        }

        @Override
        public final Reader reader() {
            return reader;
        }

        @Override
        final void close() throws IOException {
            reader.close();
        }

        private static final class StartFieldJsonStreamElement extends TextJsonStreamElement {
            private StartFieldJsonStreamElement(final Reader reader) {
                super(JsonStreamElementType.START_FIELD, reader);
            }

            public void visit(final JsonListener jsonListener) {
                jsonListener.startField(reader());
            }
        }

        private static final class StringJsonStreamElement extends TextJsonStreamElement {
            private StringJsonStreamElement(final Reader reader) {
                super(JsonStreamElementType.STRING, reader);
            }

            public void visit(final JsonListener jsonListener) {
                jsonListener.stringValue(reader());
            }
        }

        private static final class NumberJsonStreamElement extends TextJsonStreamElement {
            private NumberJsonStreamElement(final Reader reader) {
                super(JsonStreamElementType.NUMBER, reader);
            }

            public void visit(final JsonListener jsonListener) {
                jsonListener.numberValue(reader());
            }
        }
    }
}
