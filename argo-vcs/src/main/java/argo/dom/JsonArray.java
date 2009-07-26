package argo.dom;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public final class JsonArray implements JsonValue {

    private final List<JsonValue> values;

    public JsonArray(final List<JsonValue> values) {
        this.values = new ArrayList<JsonValue>(values);
    }

    public List<JsonValue> getValues() {
        return new ArrayList<JsonValue>(values);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonArray thatJsonArray = (JsonArray) that;
        return new EqualsBuilder()
                .append(this.values, thatJsonArray.values)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(values)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("values", values)
                .toString();
    }
}
