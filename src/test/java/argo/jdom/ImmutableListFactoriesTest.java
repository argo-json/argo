/*
 *  Copyright  2019 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import net.sourceforge.ickles.RandomSizeListSupplier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

final class ImmutableListFactoriesTest {

    private static final RandomSizeListSupplier<Object> RANDOM_SIZE_OBJECT_LIST_FACTORY = new RandomSizeListSupplier<>(Object::new);

    private static List<Object> aList() {
        return RANDOM_SIZE_OBJECT_LIST_FACTORY.get();
    }

    @Test
    void handlesEmptyIterator() {
        assertThat(ImmutableListFactories.immutableListOf(emptyIterator()), is(emptyList()));
    }

    @Test
    void handlesEmptyList() {
        assertThat(ImmutableListFactories.immutableListOf(emptyList()), is(emptyList()));
    }

    @Test
    void returnedListIsEqualToSourceIterator() {
        final List<Object> sourceList = aList();
        assertThat(ImmutableListFactories.immutableListOf(sourceList.iterator()), equalTo(sourceList));
    }

    @Test
    void returnedListIsEqualToSourceList() {
        final List<Object> sourceList = aList();
        assertThat(ImmutableListFactories.immutableListOf(sourceList), equalTo(sourceList));
    }

    @Test
    void returnedListIsImmutable() {
        final List<Object> originalSourceList = aList();
        final List<Object> mutableSourceList = new ArrayList<>(originalSourceList);
        final List<Object> immutableList = ImmutableListFactories.immutableListOf(mutableSourceList);
        mutableSourceList.add(new Object());
        assertThat(immutableList, equalTo(originalSourceList));
        assertThat(immutableList, not(equalTo(mutableSourceList)));
    }

}
