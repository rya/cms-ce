/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.net;

import org.junit.Test;

public class URLTest
{


    @Test(expected = IllegalArgumentException.class)
    public void testToStringWithEmptyUrl()
    {
        URL url = new URL( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToStringWithNullUrl()
    {
        String tmp = null;
        URL url = new URL( tmp );
    }
}
