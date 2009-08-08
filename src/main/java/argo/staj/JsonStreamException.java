package argo.staj;

/**
 * Thrown to indicate that it was not possible to read any further along the JSON stream.
 */
public final class JsonStreamException extends Exception {
    public JsonStreamException() {
        super();
    }

    public JsonStreamException(final String message) {
        super(message);
    }

    public JsonStreamException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonStreamException(final Throwable cause) {
        super(cause);
    }
}
