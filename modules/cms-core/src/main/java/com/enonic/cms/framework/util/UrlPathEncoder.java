/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;


import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

public class UrlPathEncoder
{

    private static URLCodec URL_CODEC = new URLCodec( "UTF-8" );

    public static String encode( String anyString )
    {
        try
        {
            return URL_CODEC.encode( anyString );
        }
        catch ( EncoderException e )
        {
            throw new RuntimeException( "Failed to encode string: " + anyString, e );
        }

    }

    /**
     * Encodes given url (like http://www.salg.no/båt) to ensure all special characters are within the generally understood character set.
     *
     * @param url The URL to encode.
     * @return The encoded URL.
     */
    public static String encodeURL( String url )
    {
        try
        {
            // this trick converts any non us ascii char to toASCIIString
            return new URL( url ).toURI().toASCIIString();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to encode url using URI.toASCIIString, url was: " + url, e );
        }
    }

    /**
     * Encodes each element in path with given encoding using java.net.URLEncoder.  Parameters and other parts of the URL that is not in the
     * path, are just kept like they are passed in.
     *
     * @param path     The path to be translated.
     * @param encoding The name of a supported character encoding.
     * @return The translated path.
     */
    public static String encodeUrlPath( String path, String encoding )
    {
        if ( path.equals( "/" ) )
        {
            return path;
        }

        // Parameters should not be encoded
        String parameters = null;
        if ( path.indexOf( "?" ) > -1 )
        {
            parameters = path.substring( path.indexOf( "?" ) + 1 );
            path = path.substring( 0, path.indexOf( "?" ) );
        }

        StringBuffer encodedPath = doEncodePath( path, encoding );

        if ( parameters != null )
        {
            encodedPath.append( "?" );
            encodedPath.append( parameters );
        }

        return encodedPath.toString();
    }

    public static String encodeUrlPathNoParameters( final String localPath, final String encoding )
    {
        if ( localPath.equals( "/" ) )
        {
            return localPath;
        }

        StringBuffer encodedPath = doEncodePath( localPath, encoding );

        return encodedPath.toString();
    }

    private static StringBuffer doEncodePath( String localPath, String encoding )
    {
        StringBuffer encodedPath = new StringBuffer( localPath.length() * 2 );
        if ( localPath.startsWith( "/" ) )
        {
            encodedPath.append( "/" );
        }

        StringTokenizer st = new StringTokenizer( localPath, "/" );
        int i = 0;
        while ( st.hasMoreTokens() )
        {
            String pathElement = st.nextToken();
            i++;
            if ( i > 1 )
            {
                encodedPath.append( "/" );
            }
            try
            {
                encodedPath.append( URLEncoder.encode( pathElement, encoding ) );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new RuntimeException( "Failed to encode path '" + localPath + "' with encoding '" + encoding + "'", e );
            }
        }

        if ( localPath.endsWith( "/" ) )
        {
            encodedPath.append( "/" );
        }
        return encodedPath;
    }
}
