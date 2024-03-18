/*
 *  Copyright 2024 Mark Slater
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.jdom;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnmodifiableIteratorTest {

    @Test
    void removeThrowsUnsupportedOperationException() {
        final Iterator<Object> unmodifiableIterator = new UnmodifiableIterator<Object>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Object next() {
                return new Object();
            }
        };
        assertThrows(UnsupportedOperationException.class, unmodifiableIterator::remove);
    }

    @Test
    void hasNextIsHandledByConcreteSubclass() {
        final Iterator<Object> delegate = Collections.singletonList(new Object()).iterator();
        final Iterator<Object> unmodifiableIterator = new UnmodifiableIterator<Object>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Object next() {
                return delegate.next();
            }
        };
        assertThat(unmodifiableIterator.hasNext(), equalTo(true));
        unmodifiableIterator.next();
        assertThat(unmodifiableIterator.hasNext(), equalTo(false));
    }

    @Test
    void nextIsHandledByConcreteSubclass() {
        final Object next = new Object();
        final Iterator<Object> delegate = Collections.singletonList(next).iterator();
        final Iterator<Object> unmodifiableIterator = new UnmodifiableIterator<Object>() {
            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Object next() {
                return delegate.next();
            }
        };
        assertThat(unmodifiableIterator.next(), equalTo(next));
        assertThrows(NoSuchElementException.class, unmodifiableIterator::next);
    }

}