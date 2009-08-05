package argo.jax;

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
