package com.enonic.cms.itest.core.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.itest.util.MockHTTPServer;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HTTPServiceTest
{
    private static Random RANDOM_WHEEL = new SecureRandom();

    static private String SAMPLE_TEXT_RESPONSE = "sample text response with special chars: \u00C5\u00F8 \u00E9";

    static private String SAMPLE_XML_RESPONSE = "<base><node1>H\u00e6?</node1><node2>\u00c6\u00d8\u00c5</node2><node3>Citro\u00ebn est d\u00e9go\u00fbtant</node3></base>";

    @Autowired
    private HTTPService httpService;

    private MockHTTPServer httpServer;

    private int serverPort;

    @Before
    public void before()
        throws IOException
    {
        serverPort = random( 8090, 9090 );
        httpServer = new MockHTTPServer( serverPort );
        Properties props = new Properties();
        props.setProperty( "cms.enonic.vertical.presentation.dataSource.getUrl.userAgent",
                           "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)" );
        VerticalProperties.getVerticalProperties().setProperties( props );

    }

    @After
    public void after()
    {
        if ( httpServer != null )
        {
            httpServer.stop();
        }
    }

    @Test
    public void get_url_as_text_test()
    {
        String testText="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        httpServer.setResponseText( testText );
        String result = httpService.getURL( buildServerUrl( MockHTTPServer.TEXT_TYPE ), "utf8", 5000 );
        System.out.println( "Original: " + testText );
        System.out.println( "Result:   " + result );
        assertEquals( testText, result );
    }

    @Test
    public void get_url_as_bytes_test()
        throws UnsupportedEncodingException
    {
        String testText="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        byte[] utf8 = testText.getBytes( "utf8" );

        httpServer.setResponseBytes( utf8 );
        String result = httpService.getURL( buildServerUrl( MockHTTPServer.BYTE_TYPE ), "utf8", 5000 );
        assertEquals( testText, result );
    }

    @Test
    public void get_win1252_response_test()
        throws UnsupportedEncodingException
    {
        byte[] w1252 = SAMPLE_TEXT_RESPONSE.getBytes( "cp1252" );

        httpServer.setResponseBytes( w1252 );
        String result = httpService.getURL( buildServerUrl( MockHTTPServer.BYTE_TYPE ), "cp1252", 5000 );
        assertEquals( SAMPLE_TEXT_RESPONSE, result );
    }

    @Test
    public void get_win1252_respons_when_encoding_is_not_known_test()
        throws UnsupportedEncodingException
    {
        // This is the typical situation when calls to getUrlAsText or getUrlAsXML are made from the datasource.
        // The datasource does not know the encoding of the source, so we need to do something to detect it in "getUrl".
        String header = "<?xml version=\"1.0\" encoding=\"Windows-1252\" ?>";
        byte[] w1252 = header.concat( SAMPLE_XML_RESPONSE ).getBytes( "cp1252" );

        httpServer.setResponseBytes( w1252 );
        byte[] httpResult = httpService.getURLAsBytes( buildServerUrl( MockHTTPServer.BYTE_TYPE ), 5000 );
        ByteArrayInputStream byteStream = new ByteArrayInputStream( httpResult );
        Document resultDoc = XMLTool.domparse( byteStream );
        String resultXML = XMLTool.documentToString( resultDoc );

        int xmlBodyStart = resultXML.indexOf( "<base>" );
        String xmlBody = resultXML.substring( xmlBodyStart );
        assertEquals( SAMPLE_XML_RESPONSE, xmlBody );
    }

    @Test
    public void get_url_as_text_wrong_url_test()
    {
        String result = httpService.getURL( buildServerUrl( "wrong" ), null, 5000 );
        assertNull( result );
    }

    private String buildServerUrl( String type )
    {
        StringBuffer sb = new StringBuffer( "http://localhost:" );
        sb.append( serverPort );
        sb.append( "?" );
        sb.append( MockHTTPServer.TYPE_PARAM );
        sb.append( "=" );
        sb.append( type );
        return sb.toString();
    }

    public void setHttpService( HTTPService httpService )
    {
        this.httpService = httpService;
    }

    private static int random( int low, int high )
    {
        return RANDOM_WHEEL.nextInt( high - low + 1 ) + low;
    }
}
