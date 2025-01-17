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

import argo.jdom.JsonNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.IOException;
import java.util.Iterator;

import static argo.JsonGenerator.JsonGeneratorStyle.PRETTY;
import static org.hamcrest.CoreMatchers.equalTo;

public final class RoundTrippingStajParserMatcher extends TypeSafeDiagnosingMatcher<Iterator<JsonStreamElement>> {

    private static final JsonGenerator JSON_GENERATOR = new JsonGenerator().style(PRETTY);

    private final Matcher<String> stringMatcher;
    private final JsonNode jsonNode;
    private JsonNode parseResult;

    private RoundTrippingStajParserMatcher(final JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        stringMatcher = equalTo(JSON_GENERATOR.generate(jsonNode));
        parseResult = null;
    }

    public static Matcher<Iterator<JsonStreamElement>> parsesTo(final JsonNode jsonNode) {
        return new RoundTrippingStajParserMatcher(jsonNode);
    }

    @Override
    protected boolean matchesSafely(final Iterator<JsonStreamElement> item, final Description mismatchDescription) {
        if (parseResult == null) {
            try {
                parseResult = JsonParserScopeExpander.parse(jsonListener -> JsonParserScopeExpander.parse(jsonListener, item));
            } catch (final InvalidSyntaxException | IOException e) {
                throw new RuntimeException("Caught exception matching", e);
            }
        }
        final boolean result = stringMatcher.matches(JSON_GENERATOR.generate(parseResult));
        if (!result) {
            stringMatcher.describeMismatch(JSON_GENERATOR.generate(parseResult), mismatchDescription);
        }
        return result;
    }

    public void describeTo(final Description description) {
        description.appendText("StajParser that generates JSON equal to ").appendValue(JSON_GENERATOR.generate(jsonNode));
    }
}
