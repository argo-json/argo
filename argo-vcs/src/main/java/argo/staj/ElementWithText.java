package argo.staj;

final class ElementWithText implements Element {

    private final JsonStreamElementType jsonStreamElementType;
    private final String text;

    ElementWithText(final JsonStreamElementType jsonStreamElementType, final String text) {
        this.jsonStreamElementType = jsonStreamElementType;
        this.text = text;
    }

    public JsonStreamElementType getJsonStreamElementType() {
        return jsonStreamElementType;
    }

    public boolean hasText() {
        return true;
    }

    public String getText() {
        return text;
    }
}
