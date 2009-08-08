package argo.staj;

import argo.jax.InvalidSyntaxException;
import argo.jax.JaxParser;

import java.io.IOException;
import java.io.Reader;

public final class StajParser implements JsonStreamReader {
    
    private final BlockingJsonListener blockingJsonListener;
    private Element next;

    public StajParser(final Reader in) {
        blockingJsonListener = new BlockingJsonListener();
        new Thread("Argo StajParser") {
            @Override
            public void run() {
                try {
                    new JaxParser().parse(in, blockingJsonListener);
                } catch (final InvalidSyntaxException e) {
                    blockingJsonListener.invalidSyntaxException(e);
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
        } catch (final IOException e) {
            throw new JsonStreamException(e);
        } catch (final InvalidSyntaxException e) {
            throw new JsonStreamException(e);
        }
        return next.getJsonStreamElementType();
    }

    public String getElementText() {
        return next.getText();
    }

    public boolean hasNext() {
        return next == null || JsonStreamElementType.END_DOCUMENT != next.getJsonStreamElementType();
    }

    public void close() {
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
