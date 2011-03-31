/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;

/**
 * This class implements a wraper around various xml technologies.
 */
public final class XMLDocumentImpl
    implements XMLDocument
{
    /**
     * System id.
     */
    private String systemId;

    /**
     * Fast infoset byte array.
     */
    private XMLBytes bytesDocument;

    /**
     * JDOM document.
     */
    private Document jdomDocument;

    /**
     * W3C DOM document.
     */
    private org.w3c.dom.Document w3cDocument;

    /**
     * String document.
     */
    private String stringDocument;

    /**
     * Construct the xml document.
     */
    public XMLDocumentImpl( XMLBytes bytesDocument )
    {
        this.bytesDocument = bytesDocument;
    }

    /**
     * Construct the xml document.
     */
    public XMLDocumentImpl( Document jdomDocument )
    {
        this.jdomDocument = jdomDocument;
    }

    /**
     * Construct the xml document.
     */
    public XMLDocumentImpl( org.w3c.dom.Document w3cDocument )
    {
        this.w3cDocument = w3cDocument;
    }

    /**
     * Construct the xml document.
     */
    public XMLDocumentImpl( String stringDocument )
    {
        this.stringDocument = stringDocument;
    }

    /**
     * Return the system id.
     */
    public String getSystemId()
    {
        return this.systemId;
    }

    /**
     * Set the system id.
     */
    public void setSystemId( String systemId )
    {
        this.systemId = systemId;
    }

    /**
     * Return the xml as text.
     */
    public String getAsString()
        throws XMLException
    {
        ensureStringDocument();
        return this.stringDocument;
    }

    /**
     * Return the xml as text.
     */
    public XMLBytes getAsBytes()
        throws XMLException
    {
        ensureBytesDocument();
        return this.bytesDocument;
    }

    /**
     * Return as W3C dom document.
     */
    public org.w3c.dom.Document getAsDOMDocument()
        throws XMLException
    {
        ensureW3CDocument();
        return this.w3cDocument;
    }

    /**
     * Return as JDOM document.
     */
    public Document getAsJDOMDocument()
        throws XMLException
    {
        ensureJDOMDocument();
        return this.jdomDocument;
    }

    /**
     * Return as a source.
     */
    public Source getAsSource()
        throws XMLException
    {
        if ( this.stringDocument != null )
        {
            return getAsStringSource();
        }
        else if ( this.jdomDocument != null )
        {
            return getAsJDOMSource();
        }
        else
        {
            ensureBytesDocument();
            Source source = this.bytesDocument.getAsSource();
            source.setSystemId( this.systemId );
            return source;
        }
    }

    /**
     * Return as a source.
     */
    public Source getAsJDOMSource()
        throws XMLException
    {
        JDOMSource source = new JDOMSource( getAsJDOMDocument() );
        source.setSystemId( this.systemId );
        return source;
    }

    /**
     * Return as a source.
     */
    public Source getAsDOMSource()
        throws XMLException
    {
        DOMSource source = new DOMSource( getAsDOMDocument() );
        source.setSystemId( this.systemId );
        return source;
    }

    /**
     * Return as a source.
     */
    public Source getAsStringSource()
        throws XMLException
    {
        StringSource source = new StringSource( getAsString() );
        source.setSystemId( this.systemId );
        return source;
    }

    /**
     * Ensure string document.
     */
    private void ensureStringDocument()
        throws XMLException
    {
        if ( this.stringDocument == null )
        {
            if ( this.bytesDocument != null )
            {
                this.stringDocument = XMLDocumentHelper.convertToString( this.bytesDocument );
            }
            else if ( this.jdomDocument != null )
            {
                this.stringDocument = XMLDocumentHelper.convertToString( this.jdomDocument );
            }
            else if ( this.w3cDocument != null )
            {
                this.stringDocument = XMLDocumentHelper.convertToString( this.w3cDocument );
            }
        }
    }

    /**
     * Ensure JDOM document.
     */
    private void ensureJDOMDocument()
        throws XMLException
    {
        if ( this.jdomDocument == null )
        {
            if ( this.bytesDocument != null )
            {
                this.jdomDocument = XMLDocumentHelper.convertToJDOMDocument( this.bytesDocument );
            }
            else if ( this.w3cDocument != null )
            {
                this.jdomDocument = XMLDocumentHelper.convertToJDOMDocument( this.w3cDocument );
            }
            else if ( this.stringDocument != null )
            {
                this.jdomDocument = XMLDocumentHelper.convertToJDOMDocument( this.stringDocument );
            }
        }
    }

    /**
     * Ensure W3C document.
     */
    private void ensureW3CDocument()
        throws XMLException
    {
        if ( this.w3cDocument == null )
        {
            if ( this.bytesDocument != null )
            {
                this.w3cDocument = XMLDocumentHelper.convertToW3CDocument( this.bytesDocument );
            }
            else if ( this.jdomDocument != null )
            {
                this.w3cDocument = XMLDocumentHelper.convertToW3CDocument( this.jdomDocument );
            }
            else if ( this.stringDocument != null )
            {
                this.w3cDocument = XMLDocumentHelper.convertToW3CDocument( this.stringDocument );
            }
        }
    }

    /**
     * Ensure document data.
     */
    private void ensureBytesDocument()
        throws XMLException
    {
        if ( this.bytesDocument == null )
        {
            if ( this.jdomDocument != null )
            {
                this.bytesDocument = XMLDocumentHelper.convertToDocumentData( this.jdomDocument );
            }
            else if ( this.w3cDocument != null )
            {
                this.bytesDocument = XMLDocumentHelper.convertToDocumentData( this.w3cDocument );
            }
            else if ( this.stringDocument != null )
            {
                this.bytesDocument = XMLDocumentHelper.convertToDocumentData( this.stringDocument );
            }
        }
    }
}
