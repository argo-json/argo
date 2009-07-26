package argo.token;

class SystemOutJsonListener implements JsonListener {
    public void startDocument() {
        System.out.println("JsonParser.startDocument");
    }

    public void endDocument() {
        System.out.println("JsonParser.endDocument");
    }

    public void startArray() {
        System.out.println("JsonParser.startArray");
    }

    public void endArray() {
        System.out.println("JsonParser.endArray");
    }

    public void startObject() {
        System.out.println("JsonParser.startObject");
    }

    public void endObject() {
        System.out.println("JsonParser.endObject");
    }

    public void numberValue(final String value) {
        System.out.println("JsonParser.numberValue [" + value + "]");
    }

    public void trueValue() {
        System.out.println("JsonParser.trueValue");
    }

    public void falseValue() {
        System.out.println("JsonParser.falseValue");
    }

    public void nullValue() {
        System.out.println("JsonParser.nullValue");
    }

    public void stringValue(final String value) {
        System.out.println("JsonParser.stringValue [" + value + "]");
    }

    public void startField(final String name) {
        System.out.println("SystemOutJsonListener.startField [" + name + "]");
    }

    public void endField() {
        System.out.println("SystemOutJsonListener.endField");
    }
}
