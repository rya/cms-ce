/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.VerticalRuntimeException;

public class VerticalAdminLogger
    extends VerticalLogger
{
    public static void errorAdmin( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void errorAdmin( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void errorAdmin( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            error( clazz, msgKey, message, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void warnAdmin( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            warn( clazz, msgKey, message, msgData, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void warnAdmin( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            warn( clazz, msgKey, message, msgData, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void warnAdmin( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalAdminException
    {

        try
        {
            warn( clazz, msgKey, message, throwable, VerticalAdminException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalAdminException) ve;
        }
    }

    public static void fatalAdmin( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalAdminRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, msgData, throwable, VerticalAdminRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (VerticalAdminRuntimeException) vre;
        }
    }

    public static void fatalAdmin( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalAdminRuntimeException
    {

        try
        {
            fatal( clazz, msgKey, message, msgData, throwable, VerticalAdminRuntimeException.class );
        }
        catch ( VerticalRuntimeException vre )
        {
            throw (VerticalAdminRuntimeException) vre;
        }
    }

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