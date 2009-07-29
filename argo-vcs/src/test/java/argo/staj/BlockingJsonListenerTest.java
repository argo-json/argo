package argo.staj;

import argo.token.JsonListenerException;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

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
            } catch (final JsonListenerException e) {
                fail("A JsonListenerException was thrown when none was expected [" + e + "].");
            }
        }
    }
}
