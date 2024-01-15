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

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

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
        assertThat(unmodifiableIterator.hasNext(), equalTo(true));
    }

    @Test
    void nextIsHandledByConcreteSubclass() {
        final Object next = new Object();
        final Iterator<Object> unmodifiableIterator = new UnmodifiableIterator<Object>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Object next() {
                return next;
            }
        };
        assertThat(unmodifiableIterator.next(), equalTo(next));
    }

}