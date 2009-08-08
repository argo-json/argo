package argo.staj;

/**
 * A <code>JsonStreamReader</code> provides operations to read from a JSON stream.
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

    /**
     * Determines whether there are any more elements.
     *
     * @return true if there are more elements.
     */
    public boolean hasNext();

    /**
     * Frees resources associate with this reader.  It is important that this method is called after finishing with an instace.
     */
    public void close();

    /**
     * Returns the type of the current element, in other words, what the previous call to <code>next</code> returned.
     *
     * @return the type of the current element.
     */
    public JsonStreamElementType getElementType();

    /**
     * Determine whether the current element has text.
     *
     * @return true if the current element has text.
     * @throws IllegalStateException if there is no current element.
     */
    public boolean hasText();

    /**
     * Get the text associated with this element.
     *
     * @return the text associated with this element.
     * @throws IllegalStateException if there is no current element, or the current element doesn't have text.
     */
    public String getText();

}
