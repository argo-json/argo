package argo.xml;

final class JsonListenerException extends RuntimeException {
    JsonListenerException() {
        super();
    }

    JsonListenerException(final String message) {
        super(message);
    }

    JsonListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    JsonListenerException(final Throwable cause) {
        super(cause);
    }
}
