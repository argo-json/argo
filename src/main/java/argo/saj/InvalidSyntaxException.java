package argo.saj;

/**
 * Thrown to indicate a given character stream is not valid JSON.
 */
public final class InvalidSyntaxException extends Exception {

    InvalidSyntaxException() {
    }

    InvalidSyntaxException(final String s) {
        super(s);
    }

    InvalidSyntaxException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    InvalidSyntaxException(final Throwable throwable) {
        super(throwable);
    }
}
