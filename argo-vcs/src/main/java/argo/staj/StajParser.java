package argo.staj;

import argo.jax.JaxParser;
import argo.jax.JsonListenerException;

import java.io.IOException;
import java.io.Reader;

public final class StajParser implements JsonStreamReader {
    
    private final BlockingJsonListener blockingJsonListener;
    private Element next;

    public StajParser(final Reader in) throws JsonListenerException, IOException {
        blockingJsonListener = new BlockingJsonListener();
        new Thread("Argo parser") {
            @Override
            public void run() {
                try {
                    new JaxParser().parse(in, blockingJsonListener);
                } catch (final JsonListenerException e) {
                    blockingJsonListener.jsonListenerException(e);
                } catch (final IOException e) {
                    blockingJsonListener.ioException(e);
                } catch (final RuntimeException e) {
                    blockingJsonListener.runtimeException(e);
                } finally {
                    blockingJsonListener.close();
                }
            }
        }.start();
    }

    public JsonStreamElementType next() throws JsonStreamException {
        try {
            next = blockingJsonListener.getNext();
        } catch (JsonListenerException e) {
            throw new JsonStreamException(e);
        } catch (IOException e) {
            throw new JsonStreamException(e);
        }
        return next.getJsonStreamElementType();
    }

    public String getElementText() throws JsonStreamException {
        return next.getText();
    }

    public boolean hasNext() throws JsonStreamException {
        return next == null || JsonStreamElementType.END_DOCUMENT != next.getJsonStreamElementType();
    }

    public void close() throws JsonStreamException {
        blockingJsonListener.close();
    }

    public JsonStreamElementType getEventType() {
        return next.getJsonStreamElementType();
    }

    public String getText() {
        return next.getText();
    }

    public boolean hasText() {
        return next.hasText();
    }
}
