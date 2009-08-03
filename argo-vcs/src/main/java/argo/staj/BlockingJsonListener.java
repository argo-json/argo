package argo.staj;

import argo.jax.JsonListener;
import argo.jax.JsonListenerException;

import java.io.IOException;

final class BlockingJsonListener implements JsonListener {

    private final Object lock = new Object();

    private Element currentElement;
    private boolean hasNext = false;
    private State state = State.IN_PROGRESS;
    private IOException ioException;
    private JsonListenerException jsonListenerException;
    private RuntimeException runtimeException;

    Element getNext() throws JsonListenerException, IOException {
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

    private Element handleTermination() throws IOException, JsonListenerException {
        switch (state) {
            case IO_EXCEPTION:
                throw ioException;
            case JSON_LISTENER_EXCEPTION:
                throw jsonListenerException;
            case RUNTIME_EXCEPTION:
                throw runtimeException;
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

    public void startDocument() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_DOCUMENT));
    }

    public void endDocument() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_DOCUMENT));
    }

    public void startArray() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_ARRAY));
    }

    public void endArray() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_ARRAY));
    }

    public void startObject() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.START_OBJECT));
    }

    public void endObject() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_OBJECT));
    }

    public void startField(final String name) throws JsonListenerException {
        gotNext(new ElementWithText(JsonStreamElementType.START_FIELD, name));
    }

    public void endField() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.END_FIELD));
    }

    public void stringValue(final String value) throws JsonListenerException {
        gotNext(new ElementWithText(JsonStreamElementType.STRING, value));
    }

    public void numberValue(final String value) throws JsonListenerException {
        gotNext(new ElementWithText(JsonStreamElementType.NUMBER, value));
    }

    public void trueValue() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.TRUE));
    }

    public void falseValue() throws JsonListenerException {
        gotNext(new ElementWithoutText(JsonStreamElementType.FALSE));
    }

    public void nullValue() throws JsonListenerException {
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

    void jsonListenerException(final JsonListenerException e) {
        synchronized (lock) {
            if (!terminated()) {
                state = State.JSON_LISTENER_EXCEPTION;
                this.jsonListenerException = e;
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

    private static enum State {
        IN_PROGRESS, CLOSED, IO_EXCEPTION, JSON_LISTENER_EXCEPTION, RUNTIME_EXCEPTION
    }
}
