/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Sep 29, 2010
 * Time: 10:00:22 AM
 */
public class URLUtilsTest
    extends TestCase
{


    @Test
    public void testNullUrl()
    {
        assertFalse( URLUtils.verifyValidURL( null ) );

    }

    @Test
    public void testEmptyUrl()
    {
        assertFalse( URLUtils.verifyValidURL( "" ) );

    }


    @Test
    public void testValidURl()
    {
        assertTrue( URLUtils.verifyValidURL( "http://www.enonic.com" ) );
    }


    @Test
    public void testValidSecureURl()
    {
        assertTrue( URLUtils.verifyValidURL( "https://www.enonic.com" ) );
    }


    @Test
    public void testInvalidURLs()
    {
        assertFalse( URLUtils.verifyValidURL( "http//www.enonic.com" ) );
        assertFalse( URLUtils.verifyValidURL( "htp://www.enonic.com" ) );
    }


}
