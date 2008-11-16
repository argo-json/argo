package argo.token;

import org.apache.commons.lang.builder.*;

final class Token {

    private final TokenType tokenType;
    private final String value;

    Token(final TokenType tokenType, final String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    TokenType getTokenType() {
        return tokenType;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("tokenType", tokenType)
                .append("value", value)
                .toString();
    }
}
