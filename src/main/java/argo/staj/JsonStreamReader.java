package argo.staj;

/**
 * A <code>JsonStreamReader</code> provides methods to read from a JsonStream.
 */
public interface JsonStreamReader {

    /**
     * Moves to the next JSON element from the stream.
     *
     * @return the type of the next element.
     * @throws JsonStreamException if the next element could not be read, for example if the next element turns out not to be valid JSON
     * @throws IllegalStateException if the stream is already closed.
     */
    public JsonStreamElementType next() throws JsonStreamException;

    public String getElementText();

    /**
     * Determines whether there are any more elements.
     *
     * @return true if there are more elements.
     */
    public boolean hasNext();

    /**
     * Frees any resources associated with this Reader.  This method does not close the
     * underlying input source.
     */
    public void close();

    public JsonStreamElementType getEventType();

    /**
     * Returns the current value of the parse event as a string,
     * this returns the string value of a CHARACTERS event,
     * returns the value of a COMMENT, the replacement value
     * for an ENTITY_REFERENCE, the string value of a CDATA section,
     * the string value for a SPACE event,
     * or the String value of the internal subset of the DTD.
     * If an ENTITY_REFERENCE has been resolved, any character data
     * will be reported as CHARACTERS events.
     *
     * @return the current text or null
     * @throws java.lang.IllegalStateException
     *          if this state is not
     *          a valid text state.
     */
    public String getText();

    /**
     * Return true if the current event has text, false otherwise
     * The following events have text:
     * CHARACTERS,DTD ,ENTITY_REFERENCE, COMMENT, SPACE
     */
    public boolean hasText();

}
