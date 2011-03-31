/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import javax.xml.transform.Source;

import org.jdom.Document;

/**
 * This interface defines the XML document.
 */
public interface XMLDocument
{
    /**
     * Return the system id.
     */
    public String getSystemId();

    /**
     * Set the system id.
     */
    public void setSystemId( String systemId );

    /**
     * Return the xml as text.
     */
    public String getAsString()
        throws XMLException;

    /**
     * Return the xml as text.
     */
    public XMLBytes getAsBytes()
        throws XMLException;

    /**
     * Return as W3C dom document.
     */
    public org.w3c.dom.Document getAsDOMDocument()
        throws XMLException;

    /**
     * Return as JDOM document.
     */
    public Document getAsJDOMDocument()
        throws XMLException;

    /**
     * Return as a source.
     */
    public Source getAsSource()
        throws XMLException;

    /**
     * Return as a source.
     */
    public Source getAsJDOMSource()
        throws XMLException;

    /**
     * Return as a source.
     */
    public Source getAsDOMSource()
        throws XMLException;

    /**
     * Return as a source.
     */
    public Source getAsStringSource()
        throws XMLException;
}
