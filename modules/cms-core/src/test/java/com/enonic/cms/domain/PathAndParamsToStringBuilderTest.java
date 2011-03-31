/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Feb 12, 2010
 * Time: 12:10:48 PM
 */
public class PathAndParamsToStringBuilderTest
    extends TestCase
{

    RequestParameters defaultParameters;

    String defaultEncoding = "UTF-8";

    @Before
    public void setUp()
    {
        Map<String, String[]> parameters = new HashMap<String, String[]>();

        parameters.put( "p1", new String[]{"v1"} );
        parameters.put( "p2", new String[]{"v2"} );
        parameters.put( "p3", new String[]{"v3"} );

        defaultParameters = new RequestParameters( parameters );

    }

    @Test
    public void testEncodePath()
    {
        Path encodablePath = new Path( "/denne/m�/encodes?Ja&nei�serru�" );

        PathAndParams pathAndParam = new PathAndParams( encodablePath, defaultParameters );

        PathAndParamsToStringBuilder builder = new PathAndParamsToStringBuilder();
        builder.setEncoding( defaultEncoding );
        builder.setIncludeParamsInPath( true );
        builder.setUrlEncodePath( true );
        builder.setIncludeFragment( true );
        builder.setHtmlEscapeParameterAmps( false );

        assertEquals( encodablePath.getAsUrlEncoded( false, defaultEncoding ) + "?p1=v1&p2=v2&p3=v3", builder.toString( pathAndParam ) );

    }

    @Test
    public void testNoParameters()
    {
        Path encodablePath = new Path( "/test/path" );

        PathAndParams pathAndParam = new PathAndParams( encodablePath, new RequestParameters() );

        PathAndParamsToStringBuilder builder = new PathAndParamsToStringBuilder();
        builder.setEncoding( defaultEncoding );
        builder.setIncludeParamsInPath( true );
        builder.setUrlEncodePath( true );
        builder.setIncludeFragment( true );
        builder.setHtmlEscapeParameterAmps( false );

        assertEquals( "/test/path", builder.toString( pathAndParam ) );

    }

    @Test
    public void testWithFragment()
    {
        Path encodablePath = new Path( "/test/path#fragment" );

        PathAndParams pathAndParam = new PathAndParams( encodablePath, defaultParameters );

        PathAndParamsToStringBuilder builder = new PathAndParamsToStringBuilder();
        builder.setEncoding( defaultEncoding );
        builder.setIncludeParamsInPath( true );
        builder.setUrlEncodePath( true );
        builder.setIncludeFragment( true );
        builder.setHtmlEscapeParameterAmps( false );

        assertEquals( "/test/path?p1=v1&p2=v2&p3=v3#fragment", builder.toString( pathAndParam ) );

    }
}
