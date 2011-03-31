/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalLogger;

public class VerticalUserServicesLogger
    extends VerticalLogger
{

    public static void warnUserServices( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalUserServicesException
    {

        try
        {
            warn( clazz, msgKey, message, throwable, VerticalUserServicesException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUserServicesException) ve;
        }
    }

    public static void errorUserServices( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalUserServicesException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalUserServicesException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUserServicesException) ve;
        }
    }

    public static void errorUserServices( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalUserServicesException
    {

        try
        {
            error( clazz, msgKey, message, throwable, VerticalUserServicesException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUserServicesException) ve;
        }
    }

}