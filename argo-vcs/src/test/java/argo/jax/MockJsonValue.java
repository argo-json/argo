package argo.jax;

import argo.jdom.JsonValue;

/**
 * Test JsonValue that determines equality based on equality of an int field provided in the constructor.  Immutable.
 */
final class MockJsonValue implements JsonValue {

    private final int equalityValue;

    MockJsonValue(final int equalityValue) {
        this.equalityValue = equalityValue;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        final MockJsonValue thatMockJsonValue = (MockJsonValue) that;
        return equalityValue == thatMockJsonValue.equalityValue;
    }

    @Override
    public int hashCode() {
        return equalityValue;
    }
}
