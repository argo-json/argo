/*
 * Copyright 2009 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.staj;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class StajParserTest {

    @Test
    public void hasNextBlocksIfNecessary() throws Exception {
        final BlockingReader in = new BlockingReader(new StringReader("{\"Hello\":\"World\"}"));
        final RecordingNextCaller recordingNextCaller = new RecordingNextCaller(new StajParser(in));
        new Thread(recordingNextCaller).start();
        in.waitForReadToBeCalled();
        assertFalse(recordingNextCaller.nextReturned);
        in.allowRead();
        recordingNextCaller.waitForNextToReturn();
        in.close();
    }

    private static final class RecordingNextCaller implements Runnable {

        private final StajParser stajParser;
        private boolean nextReturned = false;
        private final Object lock = new Object();

        RecordingNextCaller(final StajParser stajParser) {
            this.stajParser = stajParser;
        }

        boolean nextReturned() {
            return nextReturned;
        }

        void waitForNextToReturn() {
            synchronized (lock) {
                while (!nextReturned) {
                    try {
                        lock.wait();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException("Coding failue: interrupted waiting for next to return.", e);
                    }
                }
            }
        }

        public void run() {
            try {
                stajParser.hasNext();
            } catch (final JsonStreamException e) {
                throw new RuntimeException("Unexpected exception waiting for next element.", e);
            }
            synchronized (lock) {
                nextReturned = true;
                lock.notifyAll();
            }
        }
    }

    private static final class BlockingReader extends Reader {

        private final Object lock = new Object();
        private final Reader delegate;
        private boolean allowRead = false;
        private boolean readCalled = false;

        BlockingReader(final Reader delegate) {
            this.delegate = delegate;
        }

        void waitForReadToBeCalled() {
            synchronized (lock) {
                while (!readCalled) {
                    try {
                        lock.wait();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException("Coding failue: interrupted waiting for read to be called.", e);
                    }
                }
            }
        }

        void allowRead() {
            synchronized (lock) {
                allowRead = true;
                lock.notifyAll();
            }
        }

        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            synchronized (lock) {
                if (!readCalled) {
                    readCalled = true;
                    lock.notifyAll();
                }
                while (!allowRead) {
                    try {
                        lock.wait();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException("Coding failue: interrupted waiting for allowRead.", e);
                    }
                }
            }
            return delegate.read(cbuf, off, len);
        }

        public void close() throws IOException {
        }
    }
}
