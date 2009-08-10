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

import argo.saj.InvalidSyntaxException;
import argo.saj.JsonListener;

import java.io.IOException;

final class BlockingJsonListener implements JsonListener {

    private final Object lock = new Object();

    private Element currentElement;
    private boolean hasNext = false;
    private State state = State.IN_PROGRESS;
    private IOException ioException;
    private RuntimeException runtimeException;
    private InvalidSyntaxException invalidSyntaxException;

    Element getNext() throws IOException, InvalidSyntaxException {
        final Element result;
        synchronized (lock) {
            while (!hasNext && !terminated()) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting with request for next element;");
                }
            }
            if (terminated()) {
                handleTermination();
            }
            result = currentElement;
            hasNext = false;
            lock.notifyAll();
        }
        return result;
    }

    boolean hasNext() throws IOException, InvalidSyntaxException {
        final boolean result;
        synchronized (lock) {
            while (!hasNext && !terminated()) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting with request for has next element;");
                }
            }
            if (state == State.CLOSED) {
                result = false;
            } else if (hasNext) {
                result = true;
            } else {
                handleTermination();
                result = false;
            }
            lock.notifyAll();
        }
        return result;
    }

    private void gotNext(final Element element) {
        synchronized (lock) {
            while (hasNext && !terminated()) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting with next element;");
                }
            }
            if (!terminated()) {
                currentElement = element;
                hasNext = true;
                lock.notifyAll();
            }
        }
    }

    private boolean terminated() {
        return State.IN_PROGRESS != state;
    }

    private void handleTermination() throws IOException, InvalidSyntaxException {
        switch (state) {
            case IO_EXCEPTION:
                throw ioException;
            case RUNTIME_EXCEPTION:
                throw runtimeException;
            case INVALID_SYNTAX_EXCEPTION:
                throw invalidSyntaxException;
            case CLOSED:
                throw new IllegalStateException("Illegal call after already closed.");
            default:
                throw new RuntimeException("Coding failure in Argo:  Unhandled state [" + state + "].");
        }
    }

    void close() {
        synchronized (lock) {
            while (hasNext) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting to apply close.");
                }
            }
            if (!terminated()) {
                state = State.CLOSED;
                lock.notifyAll();
            }
        }
    }

    public void startDocument() {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_DOCUMENT));
    }

    public void endDocument() {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_DOCUMENT));
    }

    public void startArray() {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_ARRAY));
    }

    public void endArray() {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_ARRAY));
    }

    public void startObject() {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_OBJECT));
    }

    public void endObject() {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_OBJECT));
    }

    public void startField(final String name) {
        gotNext(new ElementWithText(JsonStreamElementType.START_FIELD, name));
    }

    public void endField() {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_FIELD));
    }

    public void stringValue(final String value) {
        gotNext(new ElementWithText(JsonStreamElementType.STRING, value));
    }

    public void numberValue(final String value) {
        gotNext(new ElementWithText(JsonStreamElementType.NUMBER, value));
    }

    public void trueValue() {
        gotNext(new ElementWithoutText(JsonStreamElementType.TRUE));
    }

    public void falseValue() {
        gotNext(new ElementWithoutText(JsonStreamElementType.FALSE));
    }

    public void nullValue() {
        gotNext(new ElementWithoutText(JsonStreamElementType.NULL));
    }

    void ioException(final IOException e) {
        synchronized (lock) {
            if (!terminated()) {
                state = State.IO_EXCEPTION;
                this.ioException = e;
                lock.notifyAll();
            }
        }
    }

    void runtimeException(final RuntimeException e) {
        synchronized (lock) {
            if (!terminated()) {
                state = State.RUNTIME_EXCEPTION;
                this.runtimeException = e;
                lock.notifyAll();
            }
        }
    }

    public void invalidSyntaxException(final InvalidSyntaxException e) {
        synchronized (lock) {
            if (!terminated()) {
                state = State.INVALID_SYNTAX_EXCEPTION;
                this.invalidSyntaxException = e;
                lock.notifyAll();
            }
        }
    }

    private static enum State {
        IN_PROGRESS, CLOSED, IO_EXCEPTION, INVALID_SYNTAX_EXCEPTION, RUNTIME_EXCEPTION
    }
}
