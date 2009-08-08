package argo.staj;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

public final class StajParserTest {

    @Test
    public void hasNextBlocksIfNecessary() throws Exception {
        final BlockingReader in = new BlockingReader();
        final RecordingNextCaller recordingNextCaller = new RecordingNextCaller(new StajParser(in));
        new Thread(recordingNextCaller).start();
        assertFalse(recordingNextCaller.nextReturned);
        in.allowRead();
        assertTrue(recordingNextCaller.nextReturned);
    }

    private static final class RecordingNextCaller implements Runnable {

        private final StajParser stajParser;
        private boolean nextReturned = false;

        RecordingNextCaller(final StajParser stajParser) {
            this.stajParser = stajParser;
        }

        boolean nextReturned() {
            return nextReturned;
        }

        public void run() {
            stajParser.hasNext();
            nextReturned = true;
        }
    }

    private static final class BlockingReader extends Reader {

        private final Object lock = new Object();
        private boolean allowRead = false;

        public void allowRead() {
            synchronized (lock) {
                allowRead = true;
                lock.notifyAll();
            }
        }

        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            synchronized (lock) {
                while (!allowRead) {
                    try {
                        lock.wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 0;
        }

        public void close() throws IOException { }
    }
}
