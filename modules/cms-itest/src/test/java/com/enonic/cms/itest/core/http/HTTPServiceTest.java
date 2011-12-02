package com.enonic.cms.itest.core.http;

import java.io.IOException;
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

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.itest.util.MockHTTPServer;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HTTPServiceTest
{
    private static Random RANDOM_WHEEL = new SecureRandom();

    private static String SAMPLE_TEXT_RESPONSE = "sample text response";

    private static String SAMPLE_XML_RESPONSE = "<parent><child>response</child></parent>";

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
    public void getUrlAsTextTest()
    {
        httpServer.setResponseText( SAMPLE_TEXT_RESPONSE );
        String result = httpService.getURL( buildServerUrl( MockHTTPServer.TEXT_TYPE ), "utf8", 5000 );
        assertEquals( SAMPLE_TEXT_RESPONSE, result );
    }

    @Test
    public void getUrlAsXMLTest()
    {
        httpServer.setResponseText( SAMPLE_XML_RESPONSE );
        String result = httpService.getURL( buildServerUrl( MockHTTPServer.XML_TYPE ), null, 5000 );
        assertEquals( SAMPLE_XML_RESPONSE, result );
    }

    @Test
    public void getUrlAsTextWrongURLTest()
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
