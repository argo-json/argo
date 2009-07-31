package argo.staj;

import argo.token.JsonListener;
import argo.token.JsonListenerException;

final class BlockingJsonListener implements JsonListener {

    private final Object lock = new Object();

    private Element currentElement;
    private boolean hasNext = false;
    private boolean closed = false;

    Element getNext() {
        final Element result;
        synchronized (lock) {
            if (closed) {
                throw new IllegalStateException("Illegal call to get next element when already closed.");
            }
            while (!hasNext) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting with request for next element;");
                }
            }
            result = currentElement;
            hasNext = false;
            lock.notify();
        }
        return result;
    }

    private void gotNext(final Element element) {
        synchronized (lock) {
            while (hasNext) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting with next element;");
                }
            }
            currentElement = element;
            hasNext = true;
            lock.notify();
        }
    }

    void close() {
        synchronized (lock) {
            while (hasNext) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Coding failure in Argo:  Interrupted waiting to apply close;");
                }
            }
            closed = true;
            lock.notify();
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
}
