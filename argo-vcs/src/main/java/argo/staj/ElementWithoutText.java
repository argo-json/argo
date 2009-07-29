package argo.staj;

final class ElementWithoutText implements Element {

    private final JsonStreamElementType jsonStreamElementType;

    ElementWithoutText(final JsonStreamElementType jsonStreamElementType) {
        this.jsonStreamElementType = jsonStreamElementType;
    }

    public JsonStreamElementType getJsonStreamElementType() {
        return jsonStreamElementType;
    }

    public boolean hasText() {
        return false;
    }

    public String getText() {
        throw new IllegalStateException("Attempt to get text from an Element of type [" + jsonStreamElementType + "] that doesn't have text.");
    }
}
