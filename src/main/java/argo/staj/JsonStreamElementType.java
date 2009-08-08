package argo.staj;

public enum JsonStreamElementType {

    /**
     * Indicates an event is a start array.
     */
    START_ARRAY,

    /**
     * Indicates an event is an end array.
     */
    END_ARRAY,

    /**
     * Indicates an event is a start object.
     */
    START_OBJECT,

    /**
     * Indicates an event is an end object.
     */
    END_OBJECT,

    /**
     * Indicates an event is a start field.
     */
    START_FIELD,

    /**
     * Indicates an event is an end field.
     */
    END_FIELD,

    /**
     * Indicates an event is a string.
     */
    STRING,

    /**
     * Indicates an event is the token 'true'.
     */
    TRUE,

    /**
     * Indicates an event is the token 'false'.
     */
    FALSE,

    /**
     * Indicates an event is the token 'null'.
     */
    NULL,

    /**
     * Indicates an event is number.
     */
    NUMBER,

    /**
     * Indicates an event is a start document
     */
    START_DOCUMENT,

    /**
     * Indicates an event is an end document
     */
    END_DOCUMENT,


}
