/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestParametersTest
{

    @Test
    public void testGetAsString1()
    {

        Map<String, String[]> paramsMap = new TreeMap<String, String[]>();
        paramsMap.put( "balle", new String[]{"rusk"} );
        paramsMap.put( "en", new String[]{"to"} );

        RequestParameters params = new RequestParameters( paramsMap );

        assertEquals( "balle=rusk&en=to", params.getAsString( false ) );

    }

    @Test
    public void testGetAsString2()
    {

        RequestParameters params = new RequestParameters();
        params.addParameterValue( "p1", "v1" );
        params.addParameterValue( "p2", "v2" );
        params.addParameterValue( "p3", "v3" );

        assertEquals( "p1=v1&p2=v2&p3=v3", params.getAsString( false ) );
    }

    @Test
    public void testAddDoubleParameters()
    {

        RequestParameters params = new RequestParameters();
        params.addParameterValue( "p1", "a" );
        params.addParameterValue( "p1", "b" );

        assertArrayEquals( new String[]{"a", "b"}, params.getParameterValues( "p1" ) );
    }

    @Test
    public void testSetParameterValue()
    {

        RequestParameters params = new RequestParameters();
        params.addParameterValue( "p1", "a" );
        params.addParameterValue( "p1", "b" );

        assertArrayEquals( new String[]{"a", "b"}, params.getParameterValues( "p1" ) );

        params.setParameterValue( "p1", "balle" );

        assertArrayEquals( new String[]{"balle"}, params.getParameterValues( "p1" ) );
    }

    @Test
    public void testGetAsStringWithDoubleParameterValues()
    {

        RequestParameters params = new RequestParameters();
        params.addParameterValue( "p1", "a" );
        params.addParameterValue( "p1", "b" );

        assertEquals( "p1=a&p1=b", params.getAsString( false ) );
    }

    @Test
    public void testGetAsStringWithTrippleParameterValues()
    {

        RequestParameters params = new RequestParameters();
        params.addParameterValue( "p1", "a" );
        params.addParameterValue( "p1", "b" );
        params.addParameterValue( "p1", "c" );

        assertEquals( "p1=a&p1=b&p1=c", params.getAsString( false ) );
    }
}
