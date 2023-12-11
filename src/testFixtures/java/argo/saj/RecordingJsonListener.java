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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static argo.staj.JsonStreamElement.asString;

public final class RecordingJsonListener implements JsonListener {

    public static final JsonListenerEvent START_DOCUMENT = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent END_DOCUMENT = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent START_ARRAY = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent END_ARRAY = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent START_OBJECT = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent END_OBJECT = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent END_FIELD = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent TRUE_VALUE = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent FALSE_VALUE = new JsonListenerEvent() {
    };
    public static final JsonListenerEvent NULL_VALUE = new JsonListenerEvent() {
    };

    private final List<JsonListenerEvent> jsonListenerEvents = new ArrayList<>();

    public interface JsonListenerEvent {
    }

    public static final class StartField implements JsonListenerEvent {
        public final String name;

        public static StartField startField(final String name) {
            return new StartField(name);
        }

        public StartField(final String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                final StartField that = (StartField) o;
                return Objects.equals(name, that.name);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "StartField{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static final class StringValue implements JsonListenerEvent {
        public final String value;

        public static StringValue stringValue(final String value) {
            return new StringValue(value);
        }

        public StringValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                StringValue that = (StringValue) o;
                return Objects.equals(value, that.value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "StringValue{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    public static final class NumberValue implements JsonListenerEvent {
        public final String value;

        public static NumberValue numberValue(final String value) {
            return new NumberValue(value);
        }

        public NumberValue(final String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                NumberValue that = (NumberValue) o;
                return Objects.equals(value, that.value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "NumberValue{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    @Override
    public void startDocument() {
        jsonListenerEvents.add(START_DOCUMENT);
    }

    @Override
    public void endDocument() {
        jsonListenerEvents.add(END_DOCUMENT);
    }

    @Override
    public void startArray() {
        jsonListenerEvents.add(START_ARRAY);
    }

    @Override
    public void endArray() {
        jsonListenerEvents.add(END_ARRAY);
    }

    @Override
    public void startObject() {
        jsonListenerEvents.add(START_OBJECT);
    }

    @Override
    public void endObject() {
        jsonListenerEvents.add(END_OBJECT);
    }

    @Override
    public void startField(Reader name) {
        jsonListenerEvents.add(new StartField(asString(name)));
    }

    @Override
    public void endField() {
        jsonListenerEvents.add(END_FIELD);
    }

    @Override
    public void stringValue(Reader value) {
        jsonListenerEvents.add(new StringValue(asString(value)));
    }

    @Override
    public void numberValue(Reader value) {
        jsonListenerEvents.add(new NumberValue(asString(value)));
    }

    @Override
    public void trueValue() {
        jsonListenerEvents.add(TRUE_VALUE);
    }

    @Override
    public void falseValue() {
        jsonListenerEvents.add(FALSE_VALUE);
    }

    @Override
    public void nullValue() {
        jsonListenerEvents.add(NULL_VALUE);
    }

    public List<JsonListenerEvent> jsonListenerEvents() {
        return Collections.unmodifiableList(new ArrayList<>(jsonListenerEvents));
    }
}
