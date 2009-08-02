package argo.dom;

public final class JsonNumber implements JsonValue {

    private final String value;

    public JsonNumber(final String value) {
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

        final JsonNumber thatJsonNumber = (JsonNumber) that;
        return this.value.equals(thatJsonNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("JsonNumber value:[")
                .append(value)
                .append("]")
                .toString();
    }
}
