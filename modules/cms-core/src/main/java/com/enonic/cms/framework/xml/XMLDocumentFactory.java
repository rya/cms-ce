/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.jdom.Document;

/**
 * This class implements the xml document factory.
 */
public final class XMLDocumentFactory
{
    public static Document create( Reader input )
            throws XMLException
    {
        String str = XMLDocumentHelper.copyToString( input );
        return create( str );
    }

    public static Document create( String str )
            throws XMLException
    {
        return XMLDocumentHelper.convertToJDOMDocument( str );
    }

    public static Document create( byte[] xml, String encoding )
    {
        try
        {
            return create( new String( xml, encoding ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String asString( String str )
            throws XMLException
    {
        org.jdom.Document jdomDocument = create( str );
        return XMLDocumentHelper.convertToString( jdomDocument );
    }

}
