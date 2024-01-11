/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import org.apache.commons.io.input.BrokenReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static argo.saj.BlackHoleJsonListener.BLACK_HOLE_JSON_LISTENER;
import static argo.saj.RecordingJsonListener.*;
import static argo.saj.RecordingJsonListener.NumberValue.numberValue;
import static argo.saj.RecordingJsonListener.StartField.startField;
import static argo.saj.RecordingJsonListener.StringValue.stringValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SajParserTest {

    @Test
    void tokenizesFromReader() throws Exception {
        RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse(new StringReader("null"), recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesFromString() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("null", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesNumber() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("1", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                numberValue("1"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesString() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("\"hello world\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world"),
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonNull() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("null", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonTrue() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("true", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                TRUE_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesJsonFalse() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("false", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                FALSE_VALUE,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesArray() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("[]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @Test
    void tokenizesObject() throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        new SajParser().parse("{\"hello\": \"world\"}", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_OBJECT,
                startField("hello"),
                stringValue("world"),
                END_FIELD,
                END_OBJECT,
                END_DOCUMENT
        ));
    }

    @Test
    void throwsOnInvalidInput() {
        final String inputString = "oops";
        final InvalidSyntaxException invalidSyntaxException = assertThrows(InvalidSyntaxException.class, () -> new SajParser().parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(invalidSyntaxException.getMessage(), equalTo("At line 1, column 1:  Invalid character [o] at start of value"));
        assertThat(invalidSyntaxException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxException.getLine(), equalTo(1));
    }

    @Test
    void rethrowsIOExceptionFromReader() {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> new SajParser().parse(new BrokenReader(ioException), BLACK_HOLE_JSON_LISTENER));
        assertThat(actualException, sameInstance(ioException));
    }

}
