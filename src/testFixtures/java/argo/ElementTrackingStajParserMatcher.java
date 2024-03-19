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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static argo.JsonStreamElementMatchers.aJsonStreamElementMatching;
import static java.util.Arrays.asList;

public final class ElementTrackingStajParserMatcher extends TypeSafeDiagnosingMatcher<Iterator<JsonStreamElement>> {

    private final Collection<JsonStreamElement> actual = new ArrayList<>();
    private final Collection<JsonStreamElement> expected;
    private final Collection<Matcher<JsonStreamElement>> expectedMatchers;

    private ElementTrackingStajParserMatcher(final JsonStreamElement... expected) {
        expectedMatchers = new ArrayList<>();
        for (JsonStreamElement jsonStreamElement : expected) {
            expectedMatchers.add(aJsonStreamElementMatching(jsonStreamElement));
        }
        this.expected = asList(expected);
    }

    public static Matcher<Iterator<JsonStreamElement>> generatesElements(final JsonStreamElement... jsonStreamElements) {
        return new ElementTrackingStajParserMatcher(jsonStreamElements);
    }

    @Override
    protected boolean matchesSafely(final Iterator<JsonStreamElement> item, final Description mismatchDescription) {
        boolean matches = true;
        for (Matcher<JsonStreamElement> jsonStreamElementMatcher : expectedMatchers) {
            final boolean hasNextItem = item.hasNext();
            if (hasNextItem) {
                final JsonStreamElement next = item.next();
                if (!jsonStreamElementMatcher.matches(next)) {
                    matches = false;
                }
                actual.add(next);
            } else {
                matches = false;
            }
        }
        while (item.hasNext()) {
            matches = false;
            actual.add(item.next());
        }
        if (!matches) {
            mismatchDescription.appendText("a StajParser actually generated sequence ").appendValueList("", ", ", "", actual);
        }
        return matches;
    }

    public void describeTo(final Description description) {
        description.appendText("a StajParser that generates the sequence ").appendValueList("", ", ", "", expected);
    }
}
