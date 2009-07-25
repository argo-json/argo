package argo.staj;

public enum JsonStreamElementType {

    /**
     * Indicates an event is a start element.
     */
    START_ELEMENT,

    /**
     * Indicates an event is an end element.
     */
    END_ELEMENT,

    /**
     * Indicates an event is characters.
     */
    CHARACTERS,

    /**
     * Indicates an event is numbers.
     */
    NUMBERS,

    /**
     * The characters are white space.
     * Events are only reported as SPACE if they are ignorable white
     * space.  Otherwise they are reported as CHARACTERS.
     */
    SPACE,

    /**
     * Indicates an event is a start document
     */
    START_DOCUMENT,

    /**
     * Indicates an event is an end document
     */
    END_DOCUMENT,


}
