package argo.token;

public interface JsonListener {

    void startDocument();

    void endDocument();

    void startArray();

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
