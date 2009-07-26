package argo.token;

public interface JsonListener {

    void startDocument() throws JsonListenerException;

    void endDocument() throws JsonListenerException;

    void startArray() throws JsonListenerException;

    void endArray() throws JsonListenerException;

    void startObject() throws JsonListenerException;

    void endObject() throws JsonListenerException;

    void startField(String name) throws JsonListenerException;

    void endField() throws JsonListenerException;

    void stringValue(String value) throws JsonListenerException;

    void numberValue(String value) throws JsonListenerException;

    void trueValue() throws JsonListenerException;

    void falseValue() throws JsonListenerException;

    void nullValue() throws JsonListenerException;
}
