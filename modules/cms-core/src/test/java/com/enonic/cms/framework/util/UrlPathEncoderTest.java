/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import junit.framework.TestCase;

public class UrlPathEncoderTest
    extends TestCase
{

    private static final String UTF8 = "UTF-8";

    private static final String ISO_8859_1 = "ISO-8859-1";


    public void testEncodeUrlPathWithUTF8()
    {

        // båt
        assertEquals( "b%C3%A5t", UrlPathEncoder.encodeUrlPath( decode( "b%C3%A5t" ), UTF8 ) );
        // båt/bjarne
        assertEquals( "b%C3%A5t/bjarne", UrlPathEncoder.encodeUrlPath( decode( "b%C3%A5t/bjarne" ), UTF8 ) );
        // /båt/bjarne
        assertEquals( "/b%C3%A5t/bjarne", UrlPathEncoder.encodeUrlPath( decode( "/b%C3%A5t/bjarne" ), UTF8 ) );
        // /båt/bjarne/
        assertEquals( "/b%C3%A5t/bjarne/", UrlPathEncoder.encodeUrlPath( decode( "/b%C3%A5t/bjarne/" ), UTF8 ) );
        // RussiskБ
        assertEquals( "Russisk%D0%91", UrlPathEncoder.encodeUrlPath( decode( "Russisk%D0%91" ), UTF8 ) );
    }

    public void testEncodeUrlPathWithISO88591()
        throws UnsupportedEncodingException, MalformedURLException
    {

        // båt
        assertEquals( "b%E5t", UrlPathEncoder.encodeUrlPath( URLDecoder.decode( "b%E5t", ISO_8859_1 ), ISO_8859_1 ) );
    }

    public void testEncodeUrlPathWithParameters()
    {
        // båt
        assertEquals( "b%C3%A5t?a=b", UrlPathEncoder.encodeUrlPath( decode( "b%C3%A5t?a=b" ), UTF8 ) );

        // /båt/bjarne
        assertEquals( "/b%C3%A5t/bjarne?a=b", UrlPathEncoder.encodeUrlPath( decode( "/b%C3%A5t/bjarne?a=b" ), UTF8 ) );

        // /båt/bjarne?æ=ø (skal ikke encode parametre)
        assertEquals( "/b%C3%A5t/bjarne?æ=ø", UrlPathEncoder.encodeUrlPath( decode( "/b%C3%A5t/bjarne?æ=ø" ), UTF8 ) );
    }

    public void testEncodeUrlWithoutParameters()
    {
        // ? skal encodes
        assertEquals( "/b%E2%88%9A%E2%80%A2tEllerBil+%3F+hepp",
                      UrlPathEncoder.encodeUrlPathNoParameters( decode( "/b%E2%88%9A%E2%80%A2tEllerBil+%3F+hepp" ), UTF8 ) );
    }

    public void testEncodeURL()
    {
        // båt
        assertEquals( "http://www.domain.com/b%C3%A5t", UrlPathEncoder.encodeURL( "http://www.domain.com/" + decode( "b%C3%A5t" ) ) );

        // båt
        assertEquals( "http://www.b%C3%A5t.com/", UrlPathEncoder.encodeURL( "http://www." + decode( "b%C3%A5t" ) + ".com/" ) );
    }

    public void testEncodeURLWithParameters()
    {
        // båt
        assertEquals( "http://www.domain.com/b%C3%A5t?a=b",
                      UrlPathEncoder.encodeURL( "http://www.domain.com/" + decode( "b%C3%A5t?a=b" ) ) );
    }

    public void testEncodeURLWithParametersThatNeedEncoding()
    {
        assertEquals( "http://www.domain.com/News?p1=b%C3%A5t",
                      UrlPathEncoder.encodeURL( "http://www.domain.com/News?p1=" + decode( "b%C3%A5t" ) ) );
    }

    public void testEncodeURLWithParametersThatDoNotNeedEncoding()
    {
        assertEquals( "http://www.domain.com/News?p1=b%C3%A5t", UrlPathEncoder.encodeURL( "http://www.domain.com/News?p1=b%C3%A5t" ) );
    }

    public void testEncodeURLWithParametersThatDoNotNeedEncodingButPathStillNeedsEncoding()
    {
        assertEquals( "http://www.domain.com/P%C3%A5melding?p1=b%C3%A5t",
                      UrlPathEncoder.encodeURL( "http://www.domain.com/" + decode( "P%C3%A5melding" ) + "?p1=b%C3%A5t" ) );

        assertEquals( "http://www.domain.com/P%C3%A5melding?p1=abc",
                      UrlPathEncoder.encodeURL( "http://www.domain.com/" + decode( "P%C3%A5melding" ) + "?p1=abc" ) );
    }

    public void testEncode()
    {

        assertEquals( "http%3A%2F%2Fwww.domain.com%2F", UrlPathEncoder.encode( "http://www.domain.com/" ) );

        assertEquals( "http%3A%2F%2Fwww.domain.com%2FP%C3%A5melding",
                      UrlPathEncoder.encode( "http://www.domain.com/" + decode( "P%C3%A5melding" ) ) );

        assertEquals( "%2F", UrlPathEncoder.encode( "/" ) );

        assertEquals( "%25", UrlPathEncoder.encode( "%" ) );

        assertEquals( "http%3A%2F%2Fwww.domain.com%2FB%25C3%25A5t", UrlPathEncoder.encode( "http://www.domain.com/B%C3%A5t" ) );
    }

    public void testEncodeOfStringAlreadyEncoded()
    {
        assertEquals( "%2F", UrlPathEncoder.encode( "/" ) );

        assertEquals( "%25", UrlPathEncoder.encode( "%" ) );

        assertEquals( "http%3A%2F%2Fwww.domain.com%2FB%25C3%25A5t", UrlPathEncoder.encode( "http://www.domain.com/B%C3%A5t" ) );
    }

    private String decode( String s )
    {
        try
        {
            return URLDecoder.decode( s, UTF8 );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

}
