/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javax.xml.transform.Source;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.Document;
import org.jvnet.fastinfoset.FastInfosetSource;

/**
 * This class implements the actual document data in its binary form.
 */
public final class XMLBytes
    implements Serializable
{
    /**
     * Fast infoset byte array.
     */
    private final byte[] compiledBytes;

    /**
     * MD5 hash code.
     */
    private String md5HashCode;

    /**
     * Construct the xml document.
     */
    public XMLBytes( byte[] compiledBytes )
    {
        this.compiledBytes = compiledBytes;
    }

    /**
     * Return hash code.
     */
    public synchronized String getMD5()
    {
        if ( this.md5HashCode == null )
        {
            this.md5HashCode = DigestUtils.md5Hex( this.compiledBytes );
        }

        return this.md5HashCode;
    }

    /**
     * Return the xml as compiled bytes.
     */
    public byte[] getData()
    {
        return this.compiledBytes;
    }

    /**
     * Return the xml as text.
     */
    public String getAsString()
        throws XMLException
    {
        return XMLDocumentHelper.convertToString( this );
    }

    /**
     * Return as W3C dom document.
     */
    public org.w3c.dom.Document getAsDOMDocument()
        throws XMLException
    {
        return XMLDocumentHelper.convertToW3CDocument( this );
    }

    /**
     * Return as JDOM document.
     */
    public Document getAsJDOMDocument()
        throws XMLException
    {
        return XMLDocumentHelper.convertToJDOMDocument( this );
    }

    /**
     * Return as a source.
     */
    public Source getAsSource()
    {
        ByteArrayInputStream in = new ByteArrayInputStream( this.compiledBytes );
        return new FastInfosetSource( in );
    }

    /**
     * Return the hash code.
     */
    public int hashCode()
    {
        return getMD5().hashCode();
    }

    /**
     * Return true if equals.
     */
    public boolean equals( Object o )
    {
        return ( o == this ) || ( ( o instanceof XMLBytes ) && getMD5().equals( ( (XMLBytes) o ).getMD5() ) );
    }
}
