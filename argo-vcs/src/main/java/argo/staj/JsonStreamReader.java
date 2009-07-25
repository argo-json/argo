package argo.staj;

import javax.xml.stream.Location;
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
     * Test if the current event is of the given type and if the namespace and name match the current
     * namespace and name of the current event.  If the namespaceURI is null it is not checked for equality,
     * if the localName is null it is not checked for equality.
     *
     * @param type         the event type
     * @param namespaceURI the uri of the event, may be null
     * @param localName    the localName of the event, may be null
     * @throws JsonStreamException if the required values are not matched.
     */
    public void require(JsonStreamElementType type, String namespaceURI, String localName) throws JsonStreamException;

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
     * @throws JsonStreamException if the current event is not a START_ELEMENT
     *                             or if a non text element is encountered
     */
    public String getElementText() throws JsonStreamException;

    /**
     * Skips any white space (isWhiteSpace() returns true), COMMENT,
     * or PROCESSING_INSTRUCTION,
     * until a START_ELEMENT or END_ELEMENT is reached.
     * If other than white space characters, COMMENT, PROCESSING_INSTRUCTION, START_ELEMENT, END_ELEMENT
     * are encountered, an exception is thrown. This method should
     * be used when processing element-only content seperated by white space.
     * <p/>
     * <br /> Precondition: none
     * <br /> Postcondition: the current event is START_ELEMENT or END_ELEMENT
     * and cursor may have moved over any whitespace event.
     * <p/>
     * <br />Essentially it does the following (implementations are free to optimized
     * but must do equivalent processing):
     * <pre>
     * int eventType = next();
     * while((eventType == XMLStreamConstants.CHARACTERS &amp;&amp; isWhiteSpace()) // skip whitespace
     * || (eventType == XMLStreamConstants.CDATA &amp;&amp; isWhiteSpace())
     * // skip whitespace
     * || eventType == XMLStreamConstants.SPACE
     * || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
     * || eventType == XMLStreamConstants.COMMENT
     * ) {
     * eventType = next();
     * }
     * if (eventType != XMLStreamConstants.START_ELEMENT &amp;&amp; eventType != XMLStreamConstants.END_ELEMENT) {
     * throw new String XMLStreamException("expected start or end tag", getLocation());
     * }
     * return eventType;
     * </pre>
     *
     * @return the event type of the element read (START_ELEMENT or END_ELEMENT)
     * @throws JsonStreamException    if the current event is not white space, PROCESSING_INSTRUCTION,
     *                                START_ELEMENT or END_ELEMENT
     * @throws NoSuchElementException if this is called when hasNext() returns false
     */
    public JsonStreamElementType nextTag() throws JsonStreamException;

    /**
     * Returns true if there are more parsing events and false
     * if there are no more events.  This method will return
     * false if the current state of the XMLStreamReader is
     * END_DOCUMENT
     *
     * @return true if there are more events, false otherwise
     * @throws JsonStreamException if there is a fatal error detecting the next state
     */
    public boolean hasNext() throws JsonStreamException;

    /**
     * Frees any resources associated with this Reader.  This method does not close the
     * underlying input source.
     *
     * @throws JsonStreamException if there are errors freeing associated resources
     */
    public void close() throws JsonStreamException;

    /**
     * Returns true if the cursor points to a start tag (otherwise false)
     *
     * @return true if the cursor points to a start tag, false otherwise
     */
    public boolean isStartElement();

    /**
     * Returns true if the cursor points to an end tag (otherwise false)
     *
     * @return true if the cursor points to an end tag, false otherwise
     */
    public boolean isEndElement();

    /**
     * Returns true if the cursor points to a character data event
     *
     * @return true if the cursor points to character data, false otherwise
     */
    public boolean isCharacters();

    /**
     * Returns true if the cursor points to a character data event
     * that consists of all whitespace
     *
     * @return true if the cursor points to all whitespace, false otherwise
     */
    public boolean isWhiteSpace();


    /**
     * Returns an integer code that indicates the type
     * of the event the cursor is pointing to.
     */
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
     * Returns an array which contains the characters from this event.
     * This array should be treated as read-only and transient. I.e. the array will
     * contain the text characters until the XMLStreamReader moves on to the next event.
     * Attempts to hold onto the character array beyond that time or modify the
     * contents of the array are breaches of the contract for this interface.
     *
     * @return the current text or an empty array
     * @throws java.lang.IllegalStateException
     *          if this state is not
     *          a valid text state.
     */
    public char[] getTextCharacters();

    /**
     * Gets the the text associated with a CHARACTERS, SPACE or CDATA event.
     * Text starting a "sourceStart" is copied into "target" starting at "targetStart".
     * Up to "length" characters are copied.  The number of characters actually copied is returned.
     * <p/>
     * The "sourceStart" argument must be greater or equal to 0 and less than or equal to
     * the number of characters associated with the event.  Usually, one requests text starting at a "sourceStart" of 0.
     * If the number of characters actually copied is less than the "length", then there is no more text.
     * Otherwise, subsequent calls need to be made until all text has been retrieved. For example:
     * <p/>
     * <code>
     * int length = 1024;
     * char[] myBuffer = new char[ length ];
     * <p/>
     * for ( int sourceStart = 0 ; ; sourceStart += length )
     * {
     * int nCopied = stream.getTextCharacters( sourceStart, myBuffer, 0, length );
     * <p/>
     * if (nCopied < length)
     * break;
     * }
     * </code>
     * XMLStreamException may be thrown if there are any XML errors in the underlying source.
     * The "targetStart" argument must be greater than or equal to 0 and less than the length of "target",
     * Length must be greater than 0 and "targetStart + length" must be less than or equal to length of "target".
     *
     * @param sourceStart the index of the first character in the source array to copy
     * @param target      the destination array
     * @param targetStart the start offset in the target array
     * @param length      the number of characters to copy
     * @return the number of characters actually copied
     * @throws JsonStreamException           if the underlying XML source is not well-formed
     * @throws IndexOutOfBoundsException     if targetStart < 0 or > than the length of target
     * @throws IndexOutOfBoundsException     if length < 0 or targetStart + length > length of target
     * @throws UnsupportedOperationException if this method is not supported
     * @throws NullPointerException          is if target is null
     */
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws JsonStreamException;

    /**
     * Returns the offset into the text character array where the first
     * character (of this text event) is stored.
     *
     * @throws java.lang.IllegalStateException
     *          if this state is not
     *          a valid text state.
     */
    public int getTextStart();

    /**
     * Returns the length of the sequence of characters for this
     * Text event within the text character array.
     *
     * @throws java.lang.IllegalStateException
     *          if this state is not
     *          a valid text state.
     */
    public int getTextLength();

    /**
     * Return input encoding if known or null if unknown.
     *
     * @return the encoding of this instance or null
     */
    public String getEncoding();

    /**
     * Return true if the current event has text, false otherwise
     * The following events have text:
     * CHARACTERS,DTD ,ENTITY_REFERENCE, COMMENT, SPACE
     */
    public boolean hasText();

    /**
     * Return the current location of the processor.
     * If the Location is unknown the processor should return
     * an implementation of Location that returns -1 for the
     * location and null for the publicId and systemId.
     * The location information is only valid until next() is
     * called.
     */
    public Location getLocation();

    /**
     * Returns the (local) name of the current event.
     * For START_ELEMENT or END_ELEMENT returns the (local) name of the current element.
     * For ENTITY_REFERENCE it returns entity name.
     * The current event must be START_ELEMENT or END_ELEMENT,
     * or ENTITY_REFERENCE
     *
     * @return the localName
     * @throws IllegalStateException if this not a START_ELEMENT,
     *                               END_ELEMENT or ENTITY_REFERENCE
     */
    public String getLocalName();

    /**
     * returns true if the current event has a name (is a START_ELEMENT or END_ELEMENT)
     * returns false otherwise
     */
    public boolean hasName();

}
