package argo.jdom;

public final class JsonString implements JsonValue {

    private final String value;

    public JsonString(final String value) {
        if (value == null) {
            throw new NullPointerException("Attempt to construct a JsonNumber with a null value.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final JsonString thatJsonString = (JsonString) that;
        return this.value.equals(thatJsonString.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonString value:[")
                .append(value)
                .append("]")
                .toString();
    }
}
