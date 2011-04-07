/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.VerticalRuntimeException;

public class VerticalAdminLogger
    extends VerticalLogger
{
    public static void fatalAdmin( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalAdminRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, throwable, VerticalAdminRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (VerticalAdminRuntimeException) vre;
        }
    }
}