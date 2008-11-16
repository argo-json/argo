package argo.token;

import org.apache.commons.lang.builder.ToStringBuilder;

final class JsonField {

    private final JsonString name;
    private final JsonValue value;

    JsonField(final JsonString name, final JsonValue value) {
        this.name = name;
        this.value = value;
    }

    JsonString getName() {
        return name;
    }

    JsonValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("value", value)
                .toString();
    }
}
