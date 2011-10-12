/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.VerticalRuntimeException;

public class VerticalEngineLogger
    extends VerticalLogger
{

    public static void errorSecurity( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalSecurityException
    {
        try
        {
            error( clazz, msgKey, message, throwable, VerticalSecurityException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalSecurityException) ve;
        }
    }

    public static void errorSecurity( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalSecurityException
    {
        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalSecurityException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalSecurityException) ve;
        }
    }

    public static void errorSecurity( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalSecurityException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalSecurityException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalSecurityException) ve;
        }
    }

    public static void errorCopy( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
    {
        error( clazz, msgKey, message, msgData, throwable, VerticalCopyException.class );
    }

    public static void errorCopy( Class clazz, int msgKey, String message, Throwable throwable )
    {
        error( clazz, msgKey, message, throwable, VerticalRuntimeException.class );
    }

    public static void errorCreate( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalCreateException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalCreateException.class );
        }
        catch ( VerticalException ve )
        {
            throw ve;
        }
    }

    public static void errorCreate( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalCreateException.class );
        }
        catch ( VerticalException ve )
        {
            throw ve;
        }
    }

    public static void errorCreate( Class clazz, int msgKey, String message, Throwable throwable )
    {

        try
        {
            error( clazz, msgKey, message, throwable, VerticalCreateException.class );
        }
        catch ( VerticalException ve )
        {
            throw ve;
        }
    }

    public static void errorRemove( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalRemoveException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalRemoveException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalRemoveException) ve;
        }
    }

    public static void errorRemove( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalRemoveException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalRemoveException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalRemoveException) ve;
        }
    }

    public static void errorRemove( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalRemoveException
    {

        try
        {
            error( clazz, msgKey, message, throwable, VerticalRemoveException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalRemoveException) ve;
        }
    }

    public static void errorUpdate( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
        throws VerticalUpdateException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalUpdateException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUpdateException) ve;
        }
    }

    public static void errorUpdate( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalUpdateException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalUpdateException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUpdateException) ve;
        }
    }

    public static void errorKey( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
        throws VerticalKeyException
    {

        try
        {
            error( clazz, msgKey, message, msgData, throwable, VerticalKeyException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalKeyException) ve;
        }
    }

    public static void errorUpdate( Class clazz, int msgKey, String message, Throwable throwable )
        throws VerticalUpdateException
    {

        try
        {
            error( clazz, msgKey, message, throwable, VerticalUpdateException.class );
        }
        catch ( VerticalException ve )
        {
            throw (VerticalUpdateException) ve;
        }
    }

    public static void fatalEngine( Class clazz, int msgKey, String message, Object[] msgData, Throwable throwable )
    {
        fatal( clazz, msgKey, message, msgData, throwable, VerticalRuntimeException.class );
    }

    public static void fatalEngine( Class clazz, int msgKey, String message, Object msgData, Throwable throwable )
    {
        fatal( clazz, msgKey, message, msgData, throwable, VerticalRuntimeException.class );
    }

    public static void fatalEngine( Class clazz, int msgKey, String message, Throwable throwable )
    {
        fatal( clazz, msgKey, message, throwable, VerticalRuntimeException.class );
    }
}
