package argo.staj;

import argo.saj.InvalidSyntaxException;
import argo.saj.SajParser;

import java.io.IOException;
import java.io.Reader;

/**
 * Implementation of <code>JsonStreamReader</code> based on a character stream of JSON.
 */
public final class StajParser implements JsonStreamReader {
    
    private final BlockingJsonListener blockingJsonListener;
    private Element next;

    /**
     * Constructs a StajParser reading from the specified Reader.
     *
     * @param in the reader to convert into JSON elements.
     */
    public StajParser(final Reader in) {
        blockingJsonListener = new BlockingJsonListener();
        new Thread("Argo StajParser") {
            @Override
            public void run() {
                try {
                    new SajParser().parse(in, blockingJsonListener);
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

    public boolean hasNext() throws JsonStreamException {
        try {
            return blockingJsonListener.hasNext();
        } catch (final IOException e) {
            throw new JsonStreamException(e);
        } catch (final InvalidSyntaxException e) {
            throw new JsonStreamException(e);
        }
    }

    public void close() {
        blockingJsonListener.close();
    }

    public JsonStreamElementType getElementType() {
        if (next == null) {
            throw new IllegalStateException("Attempt to get element type of current element before first call to next().");
        }
        return next.getJsonStreamElementType();
    }

    public String getText() {
        if (next == null) {
            throw new IllegalStateException("Attempt to get text of current element before first call to next().");
        }
        return next.getText();
    }

    public boolean hasText() {
        if (next == null) {
            throw new IllegalStateException("Attempt to determine whether current element has text before first call to next().");
        }
        return next.hasText();
    }
}
