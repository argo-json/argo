/*
 *  Copyright 2026 Mark Slater
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

import java.util.*;

import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

final class ArrayFactoriesTest {

    private static final RandomSizeListSupplier<Object> RANDOM_SIZE_OBJECT_LIST_FACTORY = new RandomSizeListSupplier<>(Object::new);

    private static List<Object> aList() {
        return RANDOM_SIZE_OBJECT_LIST_FACTORY.get();
    }

    @Test
    void handlesEmptyList() {
        assertThat(ArrayFactories.nullFreeArrayOf(emptyList(), new Object[0]), is(new Object[0]));
    }

    @Test
    void handlesEmptyNonCollectionIterable() {
        assertThat(ArrayFactories.nullFreeArrayOf(Collections::emptyIterator, new Object[0]), is(new Object[0]));
    }

    @Test
    void handlesEmptyIterator() {
        assertThat(ArrayFactories.nullFreeArrayOf(emptyIterator(), new Object[0]), is(new Object[0]));
    }

    @Test
    void rejectsNullList() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf((Collection<Object>) null, new Object[0]));
    }

    @Test
    void rejectsNullNonCollectionIterable() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf((Iterable<Object>) null, new Object[0]));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void rejectsNullIterator() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf((Iterator<Object>) null, new Object[0]));
    }

    @Test
    void rejectsEmptyListWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(emptyList(), null));
    }

    @Test
    void rejectsEmptyNonCollectionIterableWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(Collections::emptyIterator, null));
    }

    @Test
    void rejectsEmptyIteratorWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(emptyIterator(), null));
    }

    @Test
    void rejectsNonEmptyListWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(new Object()), null));
    }

    @Test
    void rejectsNonEmptyNonCollectionIterableWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(new Object())::iterator, null));
    }

    @Test
    void rejectsNonEmptyIteratorWithNullTypeArray() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(new Object()).iterator(), null));
    }

    @Test
    void rejectsListContainingNull() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(null), new Object[0]));
    }

    @Test
    void rejectsNonCollectionIterableContainingNull() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(null)::iterator, new Object[0]));
    }

    @Test
    void rejectsIteratorContainingNull() {
        assertThrows(NullPointerException.class, () -> ArrayFactories.nullFreeArrayOf(singletonList(null).iterator(), new Object[0]));
    }

    @Test
    void returnedArrayIsEqualToSourceListToArray() {
        final List<Object> sourceList = aList();
        assertThat(ArrayFactories.nullFreeArrayOf(sourceList, new Object[0]), equalTo(sourceList.toArray()));
    }

    @Test
    void returnedArrayIsEqualToNonCollectionSourceIterableToArray() {
        final List<Object> sourceList = aList();
        assertThat(ArrayFactories.nullFreeArrayOf(sourceList::iterator, new Object[0]), equalTo(sourceList.toArray()));
    }

    @Test
    void returnedArrayIsEqualToSourceIteratorToArray() {
        final List<Object> sourceList = aList();
        assertThat(ArrayFactories.nullFreeArrayOf(sourceList.iterator(), new Object[0]), equalTo(sourceList.toArray()));
    }

}