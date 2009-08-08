package argo.staj;

/**
 * Types of element a <code>JsonStreamReader</code> can produce. 
 */
public enum JsonStreamElementType {
    START_ARRAY, END_ARRAY, START_OBJECT, END_OBJECT, START_FIELD, END_FIELD, STRING, TRUE, FALSE, NULL, NUMBER, START_DOCUMENT, END_DOCUMENT
}
