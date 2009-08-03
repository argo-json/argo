package argo.jax;

final class InvalidSyntaxException extends RuntimeException {

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
