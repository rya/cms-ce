/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import junit.framework.TestCase;

import com.enonic.esl.util.DigestUtil;


public class HttpServletUtilTest
    extends TestCase
{

    public void testSetDateHeader()
    {
        GregorianCalendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
        cal.set( 1994, 11, 1, 16, 0, 0 );
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        HttpServletUtil.setDateHeader( mockResponse, cal.getTime() );
        assertEquals( "Thu, 01 Dec 1994 16:00:00 GMT", mockResponse.getHeader( "Date" ) );
    }

    public void testSetExpiresHeader()
    {
        GregorianCalendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
        cal.set( 1994, 11, 1, 16, 0, 0 );
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        HttpServletUtil.setExpiresHeader( mockResponse, cal.getTime() );
        assertEquals( "Thu, 01 Dec 1994 16:00:00 GMT", mockResponse.getHeader( "Expires" ) );
    }

    public void testSetExpiresHeaderConvertsLocalTimeToGMT()
    {
        GregorianCalendar cal = new GregorianCalendar( TimeZone.getTimeZone( "Europe/Oslo" ) );
        cal.set( 1994, 11, 1, 16, 0, 0 );
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        HttpServletUtil.setExpiresHeader( mockResponse, cal.getTime() );
        assertEquals( "Thu, 01 Dec 1994 15:00:00 GMT", mockResponse.getHeader( "Expires" ) );
    }

    public void testIsContentModifiedAccordingToIfNoneMatchHeader()
    {
        String etagFor123 = DigestUtil.generateSHA( "123" );
        String etagFor321 = DigestUtil.generateSHA( "321" );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader( "If-None-Match", etagFor123 );

        assertFalse( HttpServletUtil.isContentModifiedAccordingToIfNoneMatchHeader( mockRequest, etagFor123 ) );
        assertTrue( HttpServletUtil.isContentModifiedAccordingToIfNoneMatchHeader( mockRequest, etagFor321 ) );
    }
}
