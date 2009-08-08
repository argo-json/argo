package argo.staj;

interface Element {

    JsonStreamElementType getJsonStreamElementType();
    boolean hasText();
    String getText();

}
