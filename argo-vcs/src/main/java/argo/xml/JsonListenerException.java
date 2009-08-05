package argo.xml;

public final class JsonListenerException extends RuntimeException {
    public JsonListenerException() {
        super();
    }

    public JsonListenerException(final String message) {
        super(message);
    }

    public JsonListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonListenerException(final Throwable cause) {
        super(cause);
    }
}
