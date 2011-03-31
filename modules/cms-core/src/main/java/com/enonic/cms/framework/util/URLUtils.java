/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Sep 29, 2010
 * Time: 9:54:53 AM
 */
public class URLUtils
{

    public static boolean verifyValidURL( String urlAsString )
    {
        try
        {
            URL url = new URL( urlAsString );
        }
        catch ( MalformedURLException e )
        {
            return false;
        }

        return true;
    }

}
