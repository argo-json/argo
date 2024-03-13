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

import argo.saj.RecordingJsonListener;
import argo.saj.SajParser;
import org.apache.commons.io.input.BrokenReader;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

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

    static final class ParserArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    new SajParserJsonParserShim.Saj(new SajParser()),
                    new SajParserJsonParserShim.Json(new JsonParser())
            ).map(Arguments::arguments);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesFromReader(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse(new StringReader("null"), recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesFromString(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("null", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesNumber(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("1", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                numberValue("1"),
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesString(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("\"hello world\"", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                stringValue("hello world"),
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesJsonNull(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("null", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                NULL_VALUE,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesJsonTrue(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("true", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                TRUE_VALUE,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesJsonFalse(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("false", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                FALSE_VALUE,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesArray(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("[]", recordingJsonListener);
        assertThat(recordingJsonListener.jsonListenerEvents(), contains(
                START_DOCUMENT,
                START_ARRAY,
                END_ARRAY,
                END_DOCUMENT
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void tokenizesObject(final SajParserJsonParserShim sajParserJsonParserShim) throws Exception {
        final RecordingJsonListener recordingJsonListener = new RecordingJsonListener();
        sajParserJsonParserShim.parse("{\"hello\": \"world\"}", recordingJsonListener);
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

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void throwsOnInvalidInput(final SajParserJsonParserShim sajParserJsonParserShim) {
        final String inputString = "oops";
        final InvalidSyntaxException invalidSyntaxException = assertThrows(InvalidSyntaxException.class, () -> sajParserJsonParserShim.parse(inputString, BLACK_HOLE_JSON_LISTENER));
        assertThat(invalidSyntaxException.getMessage(), equalTo("At line 1, column 1:  Invalid character [o] at start of value"));
        assertThat(invalidSyntaxException.getColumn(), equalTo(1));
        assertThat(invalidSyntaxException.getLine(), equalTo(1));
    }

    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void rethrowsIOExceptionFromReader(final SajParserJsonParserShim sajParserJsonParserShim) {
        final IOException ioException = new IOException("An IOException");
        final IOException actualException = assertThrows(IOException.class, () -> sajParserJsonParserShim.parse(new BrokenReader(ioException), BLACK_HOLE_JSON_LISTENER));
        assertThat(actualException, sameInstance(ioException));
    }


    @ParameterizedTest
    @ArgumentsSource(ParserArgumentsProvider.class)
    void parsingInvalidJsonThrowsInvalidSyntaxExceptionFromReader(final SajParserJsonParserShim sajParserJsonParserShim) {
        assertThrows(InvalidSyntaxException.class, () -> sajParserJsonParserShim.parse(new StringReader("not json"), BLACK_HOLE_JSON_LISTENER));
    }

}
