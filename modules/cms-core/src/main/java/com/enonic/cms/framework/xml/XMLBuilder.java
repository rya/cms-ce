/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

/**
 * This class implements the XML builder.
 */
public final class XMLBuilder
{
    /**
     * The DOM document being built by this builder.
     */
    private final Document document;

    /**
     * Root node.
     */
    private Element root = null;

    /**
     * The current element.
     */
    private Element current;

    /**
     * Creates a builder for a new document (with no root element added).
     */
    public XMLBuilder()
    {
        this( null );
    }


    /**
     * Creates a builder for a new document.
     *
     * @param name the name of the root node.
     */
    public XMLBuilder( String name )
    {
        this.document = new Document();

        if ( name != null )
        {
            this.current = new Element( name );
            this.document.addContent( this.current );
            this.root = this.current;
        }
    }

    /**
     * @return The document as an <code>XMLDocument</code>
     */
    public XMLDocument getDocument()
    {
        return XMLDocumentFactory.create( this.document );
    }

    /**
     * Creates a new element with the given name as the child of the current element and makes the created element current. The {@link
     * #endElement() endElement} method needs to be called to return back to the original element.
     *
     * @param name name of the new element
     */
    public void startElement( String name )
    {
        Element element = new Element( name );

        if ( this.current == null )
        {
            // document contains not root element yet, add the new element as docs root element
            this.document.addContent( element );
            this.root = element;
        }
        else
        {
            this.current.addContent( element );
        }

        this.current = element;
    }

    /**
     * Makes the parent element current. This method should be invoked after a child element created with the {@link #startElement(String)
     * startElement} method has been fully built.
     */
    public void endElement()
    {
        if ( this.current == this.root )
        {
            this.current = null;
        }
        else
        {
            this.current = this.current.getParentElement();
        }
    }

    /**
     * Sets the named attribute of the current element.
     *
     * @param name  attribute name
     * @param value attribute value
     */
    public void setAttribute( String name, String value )
    {
        this.current.setAttribute( name, value );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value boolean attribute value
     */
    public void setAttribute( String name, boolean value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value byte attribute value
     */
    public void setAttribute( String name, byte value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value short attribute value
     */
    public void setAttribute( String name, short value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value int attribute value
     */
    public void setAttribute( String name, int value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value long attribute value
     */
    public void setAttribute( String name, long value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value float attribute value
     */
    public void setAttribute( String name, float value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Sets the named boolean attribute of the current element.
     *
     * @param name  attribute name
     * @param value double attribute value
     */
    public void setAttribute( String name, double value )
    {
        setAttribute( name, String.valueOf( value ) );
    }

    /**
     * Adds the given string as text content to the current element.
     *
     * @param content text content
     */
    public void addContent( String content )
    {
        this.current.addContent( content );
    }

    /**
     * Adds the given string as CDATA content to the current element.
     *
     * @param content CDATA content
     */
    public void addContentAsCDATA( String content )
    {
        this.current.addContent( new CDATA( content ) );
    }

    /**
     * Adds a new child element with the given name and text content. The created element will contain no attributes and no child elements
     * of its own.
     *
     * @param name    child element name
     * @param content child element content
     */
    public void addContentElement( String name, String content )
    {
        startElement( name );
        addContent( content );
        endElement();
    }

    /**
     * Takes the root element of the passed document and adds it at the current position in this creation instance.
     *
     * @param doc The entire document to import.
     */
    public void importElement( XMLDocument doc )
    {
        importElement( doc.getAsJDOMDocument().getRootElement() );
    }

    public Element getCurrentElement()
    {
        return this.current;
    }

    public void setCurrentElement( Element value )
    {
        this.current = value;
    }

    /**
     * Takes the passed element, detaches it from it's current context and inserts it at hte current position in this creation instance.
     *
     * @param element The element to insert.
     */
    private void importElement( Element element )
    {
        this.current.addContent( element.detach() );
    }

    /**
     * @return The entire XML as <code>org.jdom.Document</code>.
     */
    public Document getRootDocument()
    {
        return document;
    }

    /**
     * @return the <code>org.jdom.Element</code> representing the root of the XML.
     */
    public Element getRootElement()
    {
        return root;
    }
}
