/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.servlet.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

public class HttpServletRequestWrapperTest
{

    private HttpServletRequestWrapper secureTestObj;

    private HttpServletRequestWrapper unsecureTestObj;

    @Before
    public void startUp()
    {
        MockHttpServletRequest testReq = new MockHttpServletRequest();
        testReq.addParameter( "a", "1" );
        testReq.addParameter( "a", "1b" );
        testReq.addParameter( "b", "2a" );
        testReq.addParameter( "c", "3" );
        testReq.addParameter( "c", "3b" );
        testReq.addParameter( "c", "3c" );
        testReq.setContentType( "text/html" );
        testReq.setRemotePort( 1234 );
        testReq.setRemoteUser( "jsi" );
        testReq.setRemoteHost( "bender.enonic.com" );
        testReq.setServerName( "intra.enonic.com" );
        testReq.setServerPort( 8080 );
        testReq.setSecure( false );
        testReq.setServletPath( "/servlet/DateServlet" );
        testReq.setContextPath( "/dateapp" );
        testReq.setRequestURI( "/dateapp/servlet/DateServlet" );  // Må settes eksplisitt, fordi Mock objektet ikke genererer denne.

        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put( "b", new String[]{"2b", "2c", "2d"} );
        params.put( "d", new String[]{"4"} );
        params.put( "e", new String[]{"5a", "5b", "5c", "5d", "5e", "5f"} );

        unsecureTestObj = new HttpServletRequestWrapper( testReq, params );

        testReq = new MockHttpServletRequest();
        testReq.setServerName( "localhost" );
        testReq.setServerPort( 4848 );
        testReq.setSecure( true );
        testReq.setServletPath( "/servlet/TimeServlet" );
        testReq.setContextPath( "/timeapp" );
        testReq.setRequestURI( "/timeapp/servlet/TimeServlet" );  // Må settes eksplisitt, fordi Mock objektet ikke genererer denne.
        secureTestObj = new HttpServletRequestWrapper( testReq, params );
    }

    @Test
    public void testGetParameter()
    {
        String aTestNo = unsecureTestObj.getParameter( "a" );
        unsecureTestObj.setInherit( true );
        String aTestYes = unsecureTestObj.getParameter( "a" );
        assertNull( "a should be null when not inherited", aTestNo );
        assertNotNull( "a should not be null when inherited", aTestYes );
    }

    @Test
    public void testGetParameterMap()
    {
        Map<String, String[]> notInheritedMap = unsecureTestObj.getParameterMap();
        unsecureTestObj.setInherit( true );
        Map<String, String[]> inheritedMap = unsecureTestObj.getParameterMap();
        assertEquals( "There should be 3 keys when not inherited", 3, notInheritedMap.keySet().size() );
        assertEquals( "There should be 5 keys when inherited", 5, inheritedMap.keySet().size() );
        assertEquals( "The 'e' parameter should have the same value in both maps", notInheritedMap.get( "e" ), inheritedMap.get( "e" ) );
        assertNull( "a only exists when inherited.", notInheritedMap.get( "a" ) );
        assertNotNull( "a should exist when inherited", inheritedMap.get( "a" ) );
    }

    @Test
    public void testGetParameterNames()
    {
        Enumeration<String> notInheritedEnum = unsecureTestObj.getParameterNames();
        unsecureTestObj.setInherit( true );
        Enumeration<String> inheritedEnum = unsecureTestObj.getParameterNames();
        ArrayList<String> notInheritedArray = convertEnumToArrayList( notInheritedEnum );
        ArrayList<String> inheritedArray = convertEnumToArrayList( inheritedEnum );
        assertEquals( "The not inherited list should have 3 elements", 3, notInheritedArray.size() );
        assertEquals( "The inherited list should have 5 elements", 5, inheritedArray.size() );
        assertTrue( "There should not be any 'a' among the not inherited params.", !notInheritedArray.contains( "a" ) );
        assertTrue( "There should be any 'a' among the not inherited params.", inheritedArray.contains( "a" ) );
    }

    @Test
    public void testSetGetParameterValues()
    {
        String[] notInheritedValues = unsecureTestObj.getParameterValues( "a" );
        unsecureTestObj.setInherit( true );
        String[] inheritedValues = unsecureTestObj.getParameterValues( "a" );
        unsecureTestObj.setParameterValues( "a", new String[]{"1.0a", "1.0b", "1.1", "1.2"} );
        String[] overWrittenInheritedValues = unsecureTestObj.getParameterValues( "a" );
        unsecureTestObj.setInherit( false );
        String[] overWrittenInheritedValues2 = unsecureTestObj.getParameterValues( "a" );
        assertNull( "Before inheriting, 'a' should be null", notInheritedValues );
        assertEquals( "After inheriting, a[0] should be '1'", "1", inheritedValues[0] );
        assertEquals( "After inheriting, a[1] should be '1b'", "1b", inheritedValues[1] );
        assertEquals( "After overwriting, a[1] should be '1.0b'", "1.0b", overWrittenInheritedValues[1] );
        assertEquals( "After overwriting, a[3] should be '1.2'", "1.2", overWrittenInheritedValues[3] );
        assertEquals( "After overwriting, a[1] should be '1.0b' even when not inheriting", "1.0b", overWrittenInheritedValues2[1] );
        assertEquals( "After overwriting, a[3] should be '1.2' even when not inheriting", "1.2", overWrittenInheritedValues2[3] );
    }

    @Test
    public void testGetQueryString()
    {
        String queryString = unsecureTestObj.getQueryString();
        unsecureTestObj.setInherit( true );
        String queryStringInherited = unsecureTestObj.getQueryString();

        assertTrue( "Incorrect query string: " + queryString, queryString.indexOf( "b=2b&b=2c&b=2d" ) >= 0 );
        assertTrue( "Incorrect query string: " + queryString, queryString.indexOf( "d=4" ) >= 0 );
        assertTrue( "Incorrect query string: " + queryString, queryString.indexOf( "a=1b" ) == -1 );
        assertTrue( "Incorrect query string: " + queryString, queryString.indexOf( "c=3c" ) == -1 );
        assertTrue( "Incorrect inherited query string: " + queryStringInherited, queryStringInherited.indexOf( "a=1b" ) >= 0 );
        assertTrue( "Incorrect inherited query string: " + queryStringInherited, queryStringInherited.indexOf( "b=2a" ) == -1 );
        assertTrue( "Incorrect inherited query string: " + queryStringInherited, queryStringInherited.indexOf( "b=2b" ) >= 0 );
        assertTrue( "Incorrect inherited query string: " + queryStringInherited, queryStringInherited.indexOf( "c=3c" ) >= 0 );
        assertTrue( "Incorrect inherited query string: " + queryStringInherited, queryStringInherited.indexOf( "e=5d&e=5e&e=5f" ) >= 0 );
    }

    @Test
    public void testGetRequestURI()
    {
        String secureURI = secureTestObj.getRequestURI();
        String unsecureURI = unsecureTestObj.getRequestURI();

        secureTestObj.setServletPath( "/servlet/PageServlet" );
        unsecureTestObj.setServletPath( "servlet/LineServlet" );
        String secureURI2 = secureTestObj.getRequestURI();
        String unsecureURI2 = unsecureTestObj.getRequestURI();

        assertEquals( "Incorrect secure URI", "/timeapp/servlet/TimeServlet", secureURI );
        assertEquals( "Incorrect unsecure URI", "/dateapp/servlet/DateServlet", unsecureURI );
        assertEquals( "Incorrect locally modified secure URI", "/timeapp/servlet/PageServlet", secureURI2 );
        assertEquals( "Incorrect locally modified unsecure URI", "/dateapp/servlet/LineServlet", unsecureURI2 );

    }

    @Test
    public void testGetRequestURL()
    {
        String secureURL = secureTestObj.getRequestURL().toString();
        String unsecureURL = unsecureTestObj.getRequestURL().toString();

        assertEquals( "Incorrect secure URL", "https://localhost:4848/timeapp/servlet/TimeServlet", secureURL );
        assertEquals( "Incorrect unsecure URL", "http://intra.enonic.com:8080/dateapp/servlet/DateServlet", unsecureURL );
    }

    @Test
    public void testServletPath()
    {
        String requestServletPath = unsecureTestObj.getServletPath();
        unsecureTestObj.setServletPath( "local/apache2/htdocs/" );
        String localServletPath = unsecureTestObj.getServletPath();
        unsecureTestObj.setServletPath( "/local/tomcat/apps/enonic/" );
        String localServletPath2 = unsecureTestObj.getServletPath();
        unsecureTestObj.setServletPath( null );
        String requestServletPath2 = unsecureTestObj.getServletPath();

        assertEquals( "Incorrect servlet path", "/servlet/DateServlet", requestServletPath );
        assertEquals( "Servlet path error", requestServletPath, requestServletPath2 );
        assertEquals( "Incorrect servlet path", "/local/apache2/htdocs/", localServletPath );
        assertEquals( "Incorrect servlet path", "/local/tomcat/apps/enonic/", localServletPath2 );
    }

    @Test
    public void testWrappedCalls()
    {
        assertEquals( "Incorrect contentType", "text/html", unsecureTestObj.getContentType() );
        assertEquals( "Incorrect remote port", 1234, unsecureTestObj.getRemotePort() );
        assertEquals( "Incorrect remote host", "bender.enonic.com", unsecureTestObj.getRemoteHost() );
        assertEquals( "Incorrect remote user", "jsi", unsecureTestObj.getRemoteUser() );
    }

    private static <T> ArrayList<T> convertEnumToArrayList( Enumeration<T> e )
    {
        ArrayList<T> res = new ArrayList<T>();
        while ( e.hasMoreElements() )
        {
            res.add( e.nextElement() );
        }
        return res;
    }
}
