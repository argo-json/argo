package argo.token;

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
    public String toString() {
        return new ToStringBuilder(this)
                .append("values", values)
                .toString();
    }
}
