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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.Collection;

public final class ElementTrackingStajParserMatcher extends TypeSafeDiagnosingMatcher<StajParser> {
    private final JsonStreamElement[] jsonStreamElements;

    private ElementTrackingStajParserMatcher(final JsonStreamElement... jsonStreamElements) {
        this.jsonStreamElements = jsonStreamElements;
    }

    public static Matcher<StajParser> generatesElements(final JsonStreamElement... jsonStreamElements) {
        return new ElementTrackingStajParserMatcher(jsonStreamElements);
    }

    private Collection<JsonStreamElement> actual = new ArrayList<>();

    @Override
    protected boolean matchesSafely(final StajParser item, final Description mismatchDescription) {
        boolean matches = true;
        for (JsonStreamElement jsonStreamElement : jsonStreamElements) {
            final boolean hasNextItem = item.hasNext();
            if (!hasNextItem) {
                matches = false;
            } else {
                final JsonStreamElement next = item.next();
                if (!EqualsBuilder.reflectionEquals(next, jsonStreamElement)) {
                    matches = false;
                }
                actual.add(next);
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
        description.appendText("a StajParser that generates the sequence ").appendValueList("", ", ", "", jsonStreamElements);
    }
}
