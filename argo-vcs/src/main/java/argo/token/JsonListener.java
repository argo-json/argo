package argo.token;

public interface JsonListener {

    void startDocument();

    void endDocument();

    void startArray();

    /**
     * @deprecated this doesn't appear to be required for anything
     */
    void startValue();

    /**
     * @deprecated this doesn't appear to be required for anything
     */
    void endValue();

    void endArray();

    void startObject();

    void endObject();

    void startField(String name);

    void endField();

    void stringValue(String value);

    void numberValue(String value);

    void trueValue();

    void falseValue();

    void nullValue();
}
