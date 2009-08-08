package argo.staj;

import java.util.NoSuchElementException;

public interface JsonStreamReader {

    /**
     * Get next parsing event - a processor may return all contiguous
     * character data in a single chunk, or it may split it into several chunks.
     * <p/>
     * If element content is empty (i.e. content is "") then no CHARACTERS event will be reported.
     * <p/>
     * <p>Given the following XML:<br>
     * &lt;foo>&lt;!--description-->content text&lt;![CDATA[&lt;greeting>Hello&lt;/greeting>]]>other content&lt;/foo><br>
     * The behavior of calling next() when being on foo will be:<br>
     * 1- the comment (COMMENT)<br>
     * 2- then the characters section (CHARACTERS)<br>
     * 3- then the CDATA section (another CHARACTERS)<br>
     * 4- then the next characters section (another CHARACTERS)<br>
     * 5- then the END_ELEMENT<br>
     * <p/>
     * <p><b>NOTE:</b> empty element (such as &lt;tag/>) will be reported
     * with  two separate events: START_ELEMENT, END_ELEMENT - This preserves
     * parsing equivalency of empty element to &lt;tag>&lt;/tag>.
     * <p/>
     * This method will throw an IllegalStateException if it is called after hasNext() returns false.
     *
     * @return the integer code corresponding to the current parse event
     * @throws NoSuchElementException if this is called when hasNext() returns false
     * @throws JsonStreamException    if there is an error processing the underlying XML source
     * @see javax.xml.stream.events.XMLEvent
     */
    public JsonStreamElementType next() throws JsonStreamException;

    /**
     * Reads the content of a text-only element, an exception is thrown if this is
     * not a text-only element.
     * Regardless of value of javax.xml.stream.isCoalescing this method always returns coalesced content.
     * <br /> Precondition: the current event is START_ELEMENT.
     * <br /> Postcondition: the current event is the corresponding END_ELEMENT.
     * <p/>
     * <br />The method does the following (implementations are free to optimized
     * but must do equivalent processing):
     * <pre>
     * if(getEventType() != XMLStreamConstants.START_ELEMENT) {
     * throw new XMLStreamException(
     * "parser must be on START_ELEMENT to read next text", getLocation());
     * }
     * int eventType = next();
     * StringBuffer content = new StringBuffer();
     * while(eventType != XMLStreamConstants.END_ELEMENT ) {
     * if(eventType == XMLStreamConstants.CHARACTERS
     * || eventType == XMLStreamConstants.CDATA
     * || eventType == XMLStreamConstants.SPACE
     * || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
     * buf.append(getText());
     * } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
     * || eventType == XMLStreamConstants.COMMENT) {
     * // skipping
     * } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
     * throw new XMLStreamException(
     * "unexpected end of document when reading element text content", this);
     * } else if(eventType == XMLStreamConstants.START_ELEMENT) {
     * throw new XMLStreamException(
     * "element text content may not contain START_ELEMENT", getLocation());
     * } else {
     * throw new XMLStreamException(
     * "Unexpected event type "+eventType, getLocation());
     * }
     * eventType = next();
     * }
     * return buf.toString();
     * </pre>
     *
     */
    public String getElementText();

    /**
     * Returns true if there are more parsing events and false
     * if there are no more events.  This method will return
     * false if the current state of the XMLStreamReader is
     * END_DOCUMENT
     *
     * @return true if there are more events, false otherwise
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
