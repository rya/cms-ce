/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;


public class UrlPathDecoder
{
    private static URLCodec URL_CODEC = new URLCodec( "UTF-8" );

    public static String decode( String anyString )
    {
        try
        {
            return URL_CODEC.decode( anyString );
        }
        catch ( DecoderException e )
        {
            throw new RuntimeException( "Failed to decode string: " + anyString, e );
        }

    }
}
