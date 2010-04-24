/*
 * Copyright 2010 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class BlockingJsonListenerTest {

    @Test
    public void waitsForNextToBeCalledBeforeContinuingParsing() throws Exception {
        final BlockingJsonListener blockingJsonListener = new BlockingJsonListener();
        final EventFeedingThread eventFeedingThread = new EventFeedingThread(blockingJsonListener);
        new Thread(eventFeedingThread).start();
        assertThat(eventFeedingThread.hasEndedDocument(), equalTo(false));
        final Element firstElement = blockingJsonListener.getNext();
        assertThat(firstElement.getJsonStreamElementType(), equalTo(JsonStreamElementType.START_DOCUMENT));
        assertThat(eventFeedingThread.hasStartedDocument(), equalTo(true));
        final Element secondElement = blockingJsonListener.getNext();
        assertThat(secondElement.getJsonStreamElementType(), equalTo(JsonStreamElementType.END_DOCUMENT));
        assertThat(eventFeedingThread.hasEndedDocument(), equalTo(true));
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void throwsExceptionIfNextIsCalledAfterClose() throws Exception {
        final BlockingJsonListener blockingJsonListener = new BlockingJsonListener();
        blockingJsonListener.close();
        blockingJsonListener.getNext();
    }

    private static final class EventFeedingThread implements Runnable {
        private final BlockingJsonListener blockingJsonListener;
        private boolean hasStartedDocument = false;
        private boolean hasEndedDocument = false;

        public boolean hasStartedDocument() {
            return hasStartedDocument;
        }

        public boolean hasEndedDocument() {
            return hasEndedDocument;
        }

        public EventFeedingThread(final BlockingJsonListener blockingJsonListener) {
            this.blockingJsonListener = blockingJsonListener;
        }

        public void run() {
            try {
                blockingJsonListener.startDocument();
                hasStartedDocument = true;
                blockingJsonListener.endDocument();
                hasEndedDocument = true;
            } finally {
                blockingJsonListener.close();
            }
        }
    }
}
