/*
 *  Copyright 2026 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class FastStackTest {

    @Test
    void canPopAnElementThatHasBeenPushed() {
        final FastStack<Object> fastStack = new FastStack<>();
        final Object element = new Object();
        fastStack.push(element);
        assertThat(fastStack.pop(), equalTo(element));
    }

    @Test
    void canPeekAnElementThatHasBeenPushedWithoutRemovingIt() {
        final FastStack<Object> fastStack = new FastStack<>();
        final Object element = new Object();
        fastStack.push(element);
        assertThat(fastStack.peek(), equalTo(element));
        assertThat(fastStack.pop(), equalTo(element));
    }

    @Test
    void poppingAnEmptyFastStackThrowsArrayIndexOutOfBoundsException() {
        final FastStack<Object> fastStack = new FastStack<>();
        assertThrows(ArrayIndexOutOfBoundsException.class, fastStack::pop);
    }

    @Test
    void peekingAnEmptyFastStackThrowsArrayIndexOutOfBoundsException() {
        final FastStack<Object> fastStack = new FastStack<>();
        assertThrows(ArrayIndexOutOfBoundsException.class, fastStack::peek);
    }

    @Test
    void toleratesASingleExpansion() {
        final FastStack<Object> fastStack = new FastStack<>();
        final Object element = new Object();
        for (int i = 0; i < 17; i++) { // capacity is deliberately hidden as an implementation detail, so this test requires knowledge of the initial size
            fastStack.push(element);
        }
        for (int i = 0; i < 17; i++) {
            assertThat(fastStack.pop(), equalTo(element));
        }
    }

    @Test
    void toleratesAnExpansionBeyondSmallExpansionSize() {
        final FastStack<Object> fastStack = new FastStack<>();
        final Object element = new Object();
        for (int i = 0; i < 65; i++) {
            fastStack.push(element);
        }
        for (int i = 0; i < 65; i++) {
            assertThat(fastStack.pop(), equalTo(element));
        }
    }

    @Test
    @DisabledIfSystemProperty(named = "argo.excludeLargeHeapDependentTests", matches = "true", disabledReason = "disabled by system property: requires ~24 GB heap")
    void canGrowUpToButNotBeyondMaxArraySize() {
        final FastStack<Object> fastStack = new FastStack<>();
        final Object element = new Object();
        for (int i = 0; i < Integer.MAX_VALUE - 8; i++) {
            fastStack.push(element);
        }
        assertThrows(IllegalStateException.class, () -> fastStack.push(element));
    }
}
