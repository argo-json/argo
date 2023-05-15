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

import org.apache.commons.io.IOUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.IOException;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.Matchers.equalTo;

public class JsonStreamElementMatchers {
    public static Matcher<JsonStreamElement> aTextJsonStreamElementWithType(final JsonStreamElementType jsonStreamElementType, final String textValue) {
        return allOf(jsonStreamElementWithType(equalTo(jsonStreamElementType)), jsonStreamElementWithText(equalTo(textValue)));
    }

    public static Matcher<JsonStreamElement> aNonTextJsonStreamElementWithType(final JsonStreamElementType jsonStreamElementType) {
        return allOf(jsonStreamElementWithType(equalTo(jsonStreamElementType)), jsonStreamElementWithoutText());
    }

    public static Matcher<JsonStreamElement> aJsonStreamElementMatching(final JsonStreamElement expected) {
        final Matcher<JsonStreamElement> result;
        if (expected.hasText()) {
            final String expectedString;
            try (Reader expectedStringReader = expected.reader()) {
                expectedString = IOUtils.toString(expectedStringReader);
            } catch (IOException e) {
                throw new RuntimeException("Failed to retrieve expected String", e);
            }
            result = aTextJsonStreamElementWithType(expected.jsonStreamElementType(), expectedString);
        } else {
            result = aNonTextJsonStreamElementWithType(expected.jsonStreamElementType());
        }
        return result;
    }

    private static Matcher<JsonStreamElement> jsonStreamElementWithType(final Matcher<JsonStreamElementType> jsonStreamElementTypeMatcher) {
        return new TypeSafeDiagnosingMatcher<JsonStreamElement>() {
            @Override
            protected boolean matchesSafely(final JsonStreamElement jsonStreamElement, final Description description) {
                final JsonStreamElementType jsonStreamElementType = jsonStreamElement.jsonStreamElementType();
                final boolean matches = jsonStreamElementTypeMatcher.matches(jsonStreamElementType);
                if (!matches) {
                    jsonStreamElementTypeMatcher.describeMismatch(jsonStreamElementType, description);
                }
                return matches;
            }

            public void describeTo(final Description description) {
                description.appendText("A JsonStreamElement with JsonStreamElementType ").appendDescriptionOf(jsonStreamElementTypeMatcher);
            }
        };
    }

    private static Matcher<JsonStreamElement> jsonStreamElementWithoutText() {
        return allOf(jsonStreamElementHasText(equalTo(false)), jsonStreamElementTextThrows(any(IllegalStateException.class)));
    }

    private static Matcher<JsonStreamElement> jsonStreamElementWithText(final Matcher<String> textMatcher) {
        return allOf(jsonStreamElementHasText(equalTo(true)), jsonStreamElementText(textMatcher));
    }

    private static Matcher<JsonStreamElement> jsonStreamElementText(final Matcher<String> textMatcher) {
        return new TypeSafeDiagnosingMatcher<JsonStreamElement>() {
            @Override
            protected boolean matchesSafely(final JsonStreamElement item, final Description mismatchDescription) {
                final String text = item.text();
                final boolean matches = textMatcher.matches(text);
                if (!matches) {
                    textMatcher.describeMismatch(text, mismatchDescription);
                }
                return matches;
            }

            public void describeTo(final Description description) {
                description.appendText("a JsonStreamText for which text() returns ").appendDescriptionOf(textMatcher);
            }
        };
    }

    private static TypeSafeDiagnosingMatcher<JsonStreamElement> jsonStreamElementTextThrows(final Matcher<? extends Throwable> exceptionMatcher) {
        return new TypeSafeDiagnosingMatcher<JsonStreamElement>() {
            @Override
            protected boolean matchesSafely(final JsonStreamElement item, final Description mismatchDescription) {
                try {
                    item.text();
                    mismatchDescription.appendText("no exception was thrown");
                    return false;
                } catch (final IllegalStateException e) {
                    final boolean matches = exceptionMatcher.matches(e);
                    if (!matches) {
                        exceptionMatcher.describeMismatch(e, mismatchDescription);
                    }
                    return matches;
                }
            }

            public void describeTo(final Description description) {
                description.appendText("a JsonStreamElement for which calling text() throws an exception ").appendDescriptionOf(exceptionMatcher);
            }
        };
    }

    private static Matcher<JsonStreamElement> jsonStreamElementHasText(final Matcher<Boolean> booleanMatcher) {
        return new TypeSafeDiagnosingMatcher<JsonStreamElement>() {
            @Override
            protected boolean matchesSafely(final JsonStreamElement jsonStreamElement, final Description description) {
                boolean hasText = jsonStreamElement.hasText();
                final boolean matches = booleanMatcher.matches(hasText);
                if (!matches) {
                    booleanMatcher.describeMismatch(hasText, description);
                }
                return matches;
            }

            public void describeTo(final Description description) {
                description.appendText("A JsonStreamElement for which hasText() returns ").appendDescriptionOf(booleanMatcher);
            }
        };
    }

    private JsonStreamElementMatchers() {
    }
}
