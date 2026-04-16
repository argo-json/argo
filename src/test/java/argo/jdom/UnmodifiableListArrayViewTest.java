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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class UnmodifiableListArrayViewTest {

    private static final RandomSizeListSupplier<Object> RANDOM_SIZE_OBJECT_LIST_FACTORY = new RandomSizeListSupplier<>(Object::new);

    private static Object[] anArray() {
        return RANDOM_SIZE_OBJECT_LIST_FACTORY.get().toArray();
    }

    @Test
    void handlesEmptyArray() {
        assertThat(new UnmodifiableListArrayView<>(new Object[0]), is(emptyList()));
    }

    @Test
    void rejectsNullArray() {
        assertThrows(NullPointerException.class, () -> new UnmodifiableListArrayView<>(null));
    }

    @Test
    void returnedListIsEqualToListOfSourceArray() {
        final Object[] sourceArray = anArray();
        assertThat(new UnmodifiableListArrayView<>(sourceArray), equalTo(Arrays.asList(sourceArray)));
    }

    @Test
    void returnedListIsImmutable() {
        final List<Object> list = new UnmodifiableListArrayView<>(anArray());
        assertThrows(UnsupportedOperationException.class, () -> list.add(new Object()));
    }

    @Test
    void sizeMatchesUnderlyingArrayLength() {
        final Object[] sourceArray = anArray();
        assertThat(new UnmodifiableListArrayView<>(sourceArray).size(), equalTo(sourceArray.length));
    }

    @Test
    void canGetAnElement() {
        final Object element = new Object();
        assertThat(new UnmodifiableListArrayView<>(new Object[]{element}).get(0), sameInstance(element));
    }

    @Test
    void gettingAnElementLessThanZeroThrowsIndexOutOfBoundsException() {
        final List<Object> list = new UnmodifiableListArrayView<>(anArray());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void gettingAnElementBeyondEndOfUnderlyingArrayThrowsIndexOutOfBoundsException() {
        final Object[] sourceArray = anArray();
        final List<Object> list = new UnmodifiableListArrayView<>(sourceArray);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(sourceArray.length));
    }

    @Test
    void iteratorOfEmptyArrayDoesNotHaveNext() {
        assertThat(new UnmodifiableListArrayView<>(new Object[0]).iterator().hasNext(), equalTo(false));
    }

    @Test
    void iteratorOfEmptyArrayThrowsOnNext() {
        final Iterator<Object> iterator = new UnmodifiableListArrayView<>(new Object[0]).iterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorOfNonEmptyArrayHasNext() {
        assertThat(new UnmodifiableListArrayView<>(new Object[]{new Object()}).iterator().hasNext(), equalTo(true));
    }

    @Test
    void iteratorOfNonEmptyArrayReturnsNext() {
        final Object element = new Object();
        assertThat(new UnmodifiableListArrayView<>(new Object[]{element}).iterator().next(), sameInstance(element));
    }

    @Test
    void iteratorOfNonEmptyArrayDoesNotHaveNextWhenExhausted() {
        final Iterator<Object> iterator = new UnmodifiableListArrayView<>(new Object[]{new Object()}).iterator();
        iterator.next();
        assertThat(iterator.hasNext(), equalTo(false));
    }

    @Test
    void iteratorOfNonEmptyArrayThrowsOnNextWhenExhausted() {
        final Iterator<Object> iterator = new UnmodifiableListArrayView<>(new Object[]{new Object()}).iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

}