/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils
{

    public static boolean verifyValidURL( String urlAsString )
    {
        try
        {
            new URL( urlAsString );
        }
        catch ( MalformedURLException e )
        {
            return false;
        }

        return true;
    }

}
