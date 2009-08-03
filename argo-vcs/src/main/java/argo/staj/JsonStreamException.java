package argo.staj;

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
