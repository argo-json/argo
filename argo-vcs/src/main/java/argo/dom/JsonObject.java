package argo.dom;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

public final class JsonObject implements JsonValue {

    private final Map<JsonString, JsonValue> fields;

    public JsonObject(final Map<JsonString, JsonValue> fields) {
        this.fields = new HashMap<JsonString, JsonValue>(fields);
    }

    public Map<JsonString, JsonValue> getFields() {
        return new HashMap<JsonString, JsonValue>(fields);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonObject thatJsonObject = (JsonObject) that;
        return new EqualsBuilder()
                .append(this.fields, thatJsonObject.fields)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fields)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fields", fields)
                .toString();
    }
}
