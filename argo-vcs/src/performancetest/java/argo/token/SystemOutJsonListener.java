package argo.token;

class SystemOutJsonListener implements JsonListener {
    public void startDocument() {
        System.out.println("Tokenizer.startDocument");
    }

    public void endDocument() {
        System.out.println("Tokenizer.endDocument");
    }

    public void startArray() {
        System.out.println("Tokenizer.startArray");
    }

    public void endArray() {
        System.out.println("Tokenizer.endArray");
    }

    public void startObject() {
        System.out.println("Tokenizer.startObject");
    }

    public void endObject() {
        System.out.println("Tokenizer.endObject");
    }

    public void numberValue(final String value) {
        System.out.println("Tokenizer.numberValue [" + value + "]");
    }

    public void trueValue() {
        System.out.println("Tokenizer.trueValue");
    }

    public void falseValue() {
        System.out.println("Tokenizer.falseValue");
    }

    public void nullValue() {
        System.out.println("Tokenizer.nullValue");
    }

    public void stringValue(final String value) {
        System.out.println("Tokenizer.stringValue [" + value + "]");
    }

    public void startField(final String name) {
        System.out.println("SystemOutJsonListener.startField [" + name + "]");
    }

    public void endField() {
        System.out.println("SystemOutJsonListener.endField");
    }
}
