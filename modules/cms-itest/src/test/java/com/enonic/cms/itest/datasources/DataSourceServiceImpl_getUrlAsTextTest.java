/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.datasources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.DataSourceServiceImpl;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.MockHTTPServer;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class DataSourceServiceImpl_getUrlAsTextTest
    extends AbstractSpringTest
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserDao userDao;

    private DataSourceServiceImpl dataSourceService;

    @Autowired
    private HTTPService httpService;

    private MockHTTPServer httpServer;

    static private String SAMPLE_TEXT_RESPONSE =
        "H\u00e6\u00e6\u00e6?!?! \u00c6rlig talt! S\u00e5nt er \u00f8deleggende, eller som franskmenn ville sagt det: d\u00e9go\u00fbtant";

    private final static Random RANDOM_WHEEL = new SecureRandom();

    private int serverPort;

    @Before
    
    public void setUp()
        throws IOException
    {
        serverPort = random( 8090, 9090 );
        httpServer = new MockHTTPServer( serverPort );

        dataSourceService = new DataSourceServiceImpl();
        dataSourceService.setSecurityService( securityService );
        dataSourceService.setTimeService( new MockTimeService( new DateTime( 2010, 7, 1, 12, 0, 0, 0 ) ) );
        dataSourceService.setUserDao( userDao );
        dataSourceService.setHTTPService( httpService );

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
            httpServer = null;
        }
    }

    @Test
    public void test_get_url_as_xml_basic()
    {
        httpServer.setResponseText( SAMPLE_TEXT_RESPONSE );

        DataSourceContext context = new DataSourceContext();
        XMLDocument result = dataSourceService.getURLAsText( context, buildServerUrl( MockHTTPServer.TEXT_TYPE ), "UTF-8" );

        Document resultDoc = result.getAsJDOMDocument();
        String resultText = resultDoc.getRootElement().getText();
        assertEquals( SAMPLE_TEXT_RESPONSE, resultText );
    }

    @Test
    public void test_get_url_as_xml_win1252_charset()
        throws UnsupportedEncodingException
    {
        byte[] w1252 = SAMPLE_TEXT_RESPONSE.getBytes( "cp1252" );
        httpServer.setResponseBytes( w1252 );

        DataSourceContext context = new DataSourceContext();
        XMLDocument result = dataSourceService.getURLAsText( context, buildServerUrl( MockHTTPServer.BYTE_TYPE ), "Windows-1252" );

        Document resultDoc = result.getAsJDOMDocument();
        String resultText = resultDoc.getRootElement().getText();
        assertEquals( SAMPLE_TEXT_RESPONSE, resultText );
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

    private static int random( int low, int high )
    {
        return RANDOM_WHEEL.nextInt( high - low + 1 ) + low;
    }


}
