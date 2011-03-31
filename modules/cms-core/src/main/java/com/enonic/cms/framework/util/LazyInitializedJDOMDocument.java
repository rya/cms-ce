/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Apr 27, 2010
 */
public class LazyInitializedJDOMDocument
    implements Serializable
{
    private final String documentAsString;

    //private transient Document cachedDocument;

    public static LazyInitializedJDOMDocument parse( Document document )
    {
        String docAsString = JDOMUtil.printDocument( document );
        return new LazyInitializedJDOMDocument( docAsString );
    }

    public LazyInitializedJDOMDocument( String documentAsString )
    {
        if ( documentAsString == null )
        {
            throw new IllegalArgumentException( "Given documentAsString cannot be null" );
        }
        this.documentAsString = documentAsString;
    }

    public LazyInitializedJDOMDocument( String documentAsString, Document cachedDocument )
    {
        this.documentAsString = documentAsString;
        /*if ( cachedDocument != null )
        {
            this.cachedDocument = (Document) cachedDocument.clone();
        }*/
    }

    public String getDocumentAsString()
    {
        return documentAsString;
    }

    public Document getDocument()
    {
        try
        {
            /*if ( cachedDocument == null )
            {
                cachedDocument = JDOMUtil.parseDocument( documentAsString );
            }
            return (Document) cachedDocument.clone();*/
            return JDOMUtil.parseDocument( documentAsString );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to parse document: " + e.getMessage(), e );
        }
        catch ( JDOMException e )
        {
            throw new RuntimeException( "Failed to parse document: " + e.getMessage(), e );
        }
    }

    public Object clone()
    {
        //return new LazyInitializedJDOMDocument( documentAsString, cachedDocument );
        return new LazyInitializedJDOMDocument( documentAsString );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof LazyInitializedJDOMDocument ) )
        {
            return false;
        }

        LazyInitializedJDOMDocument that = (LazyInitializedJDOMDocument) o;

        if ( documentAsString != null ? !documentAsString.equals( that.documentAsString ) : that.documentAsString != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 671, 11 ).append( documentAsString ).toHashCode();
    }
}
