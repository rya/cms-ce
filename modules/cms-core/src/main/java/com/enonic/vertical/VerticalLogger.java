/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import java.io.StringWriter;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;

public class VerticalLogger
{
    private static Logger getLogger( Class logger )
    {
        return LoggerFactory.getLogger( logger.getName() );
    }

    private static void debugInternal( Class logger, String message, Throwable throwable )
    {
        Logger log = getLogger( logger );
        log.debug( message, throwable );
    }

    private static void infoInternal( Class logger, String message, Throwable throwable )
    {
        Logger log = getLogger( logger );
        log.info( message, throwable );
    }

    private static void warnInternal( Class logger, String message, Throwable throwable )
    {
        Logger log = getLogger( logger );
        log.warn( message, throwable );
    }

    private static void warnInternal( Class logger, String message, Throwable throwable, Class exception, int messageKey )
        throws VerticalException
    {
        warnInternal( logger, message, throwable );
        if ( exception != null )
        {
            throw createException( exception, message, throwable, messageKey );
        }
    }

    private static void errorInternal( Class logger, String message, Throwable throwable )
    {
        Logger log = getLogger( logger );
        log.error( message, throwable );
    }

    private static void errorInternal( Class logger, String message, Throwable throwable, Class exception, int messageKey )
        throws VerticalException
    {
        errorInternal( logger, message, throwable );
        if ( exception != null )
        {
            throw createException( exception, message, throwable, messageKey );
        }
    }

    private static void fatalInternal( Class logger, String message, Throwable throwable )
    {
        Logger log = getLogger( logger );
        log.error( message, throwable );
    }

    private static void fatalInternal( Class logger, String message, Throwable throwable, Class exception, int messageKey )
        throws VerticalRuntimeException
    {
        errorInternal( logger, message, throwable );
        if ( exception != null )
        {
            throw createRuntimeException( exception, message, throwable, messageKey );
        }
    }

    private static VerticalException createException( Class exception, String message, Throwable cause, int messageKey )
        throws VerticalRuntimeException
    {
        VerticalException ex = (VerticalException) createThrowable( VerticalException.class, exception, message, cause );
        ex.setMessageKey( messageKey );
        return ex;
    }

    private static VerticalRuntimeException createRuntimeException( Class exception, String message, Throwable cause, int messageKey )
        throws VerticalRuntimeException
    {
        VerticalRuntimeException ex =
            (VerticalRuntimeException) createThrowable( VerticalRuntimeException.class, exception, message, cause );
        ex.setMessageKey( messageKey );
        return ex;
    }

    private static Throwable createThrowable( Class type, Class exception, String message, Throwable cause )
        throws VerticalRuntimeException
    {
        if ( type.isAssignableFrom( exception ) )
        {
            try
            {
                Constructor constructor = exception.getConstructor( String.class );
                Throwable result = (Throwable) constructor.newInstance( message );
                result.initCause( cause );
                return result;
            }
            catch ( Exception e )
            {
                throw new VerticalRuntimeException( "Failed to create exception [" + exception.getName() + "]", e );
            }
        }
        else
        {
            throw new VerticalRuntimeException(
                "Exception [" + exception.getName() + "] must be of type [" + VerticalRuntimeException.class.getName() + "]" );
        }
    }

    public static void debug( Class logger, String message, Object[] msgData, Throwable throwable )
    {
        debugInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    public static void debug( Class logger, String message, Object msgData, Throwable throwable )
    {
        debugInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    public static void debug( Class logger, String message, Throwable throwable )
    {
        debugInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable );
    }

    public static void debug( Class logger, Document doc )
    {
        Logger log = getLogger( logger );
        if ( log.isDebugEnabled() )
        {
            StringWriter swriter = new StringWriter();
            XMLTool.printDocument( swriter, doc, 2 );
            log.debug( swriter.toString() );
        }
    }

    public static void info( Class logger, String message, Object[] msgData, Throwable throwable )
    {
        infoInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    public static void info( Class logger, Document doc )
    {
        Logger log = getLogger( logger );
        if ( log.isInfoEnabled() )
        {
            StringWriter swriter = new StringWriter();
            XMLTool.printDocument( swriter, doc, 2 );
            log.info( swriter.toString() );
        }
    }

    public static void info( Class logger, String message, Object msgData, Throwable throwable )
    {
        infoInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    public static void info( Class logger, String message, Throwable throwable )
    {
        infoInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable );
    }

    public static void warn( Class logger, String message, Object[] msgData, Throwable throwable )
    {
        warnInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    protected static void warn( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable, Class exception )
        throws VerticalException
    {
        warnInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    public static void warn( Class logger, String message, Object msgData, Throwable throwable )
    {
        warnInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    protected static void warn( Class logger, int messageKey, String message, Object msgData, Throwable throwable, Class exception )
        throws VerticalException
    {
        warnInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    public static void warn( Class logger, String message, Throwable throwable )
    {
        warnInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable );
    }

    protected static void warn( Class logger, int messageKey, String message, Throwable throwable, Class exception )
        throws VerticalException
    {
        warnInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable, exception, messageKey );
    }

    public static void error( Class logger, String message, Object[] msgData, Throwable throwable )
    {
        errorInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    protected static void error( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable, Class exception )
        throws VerticalException
    {
        errorInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    public static void error( Class logger, String message, Object msgData, Throwable throwable )
    {
        errorInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable );
    }

    protected static void error( Class logger, int messageKey, String message, Object msgData, Throwable throwable, Class exception )
        throws VerticalException
    {
        errorInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    public static void error( Class logger, String message, Throwable throwable )
    {
        errorInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable );
    }

    protected static void error( Class logger, int messageKey, String message, Throwable throwable, Class exception )
        throws VerticalException
    {
        errorInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable, exception, messageKey );
    }

    protected static void fatal( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable, Class exception )
        throws VerticalRuntimeException
    {
        fatalInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    protected static void fatal( Class logger, int messageKey, String message, Object msgData, Throwable throwable, Class exception )
        throws VerticalRuntimeException
    {
        fatalInternal( logger, StringUtil.expandString( message, msgData, throwable ), throwable, exception, messageKey );
    }

    protected static void fatal( Class logger, int messageKey, String message, Throwable throwable, Class exception )
        throws VerticalRuntimeException
    {
        fatalInternal( logger, StringUtil.expandString( message, (Object) null, throwable ), throwable, exception, messageKey );
    }

    //
    // Public backward compatible methods
    //

    public static void debug( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable )
    {
        debug( logger, message, msgData, throwable );
    }

    public static void debug( Class logger, int messageKey, String message, Object msgData, Throwable throwable )
    {
        debug( logger, message, msgData, throwable );
    }

    public static void debug( Class logger, int messageKey, String message, Throwable throwable )
    {
        debug( logger, message, throwable );
    }

    public static void info( Class logger, int messageKey, String message, Object msgData, Throwable throwable )
    {
        info( logger, message, msgData, throwable );
    }

    public static void info( Class logger, int messageKey, String message, Throwable throwable )
    {
        info( logger, message, throwable );
    }

    public static void warn( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable )
    {
        warn( logger, message, msgData, throwable );
    }

    public static void warn( Class logger, int messageKey, String message, Object msgData, Throwable throwable )
    {
        warn( logger, message, msgData, throwable );
    }

    public static void warn( Class logger, int messageKey, String message, Throwable throwable )
    {
        warn( logger, message, throwable );
    }

    public static void error( Class logger, int messageKey, String message, Object[] msgData, Throwable throwable )
    {
        error( logger, message, msgData, throwable );
    }

    public static void error( Class logger, int messageKey, String message, Object msgData, Throwable throwable )
    {
        error( logger, message, msgData, throwable );
    }

    public static void error( Class logger, int messageKey, String message, Throwable throwable )
    {
        error( logger, message, throwable );
    }

    public static boolean isDebugEnabled( Class logger )
    {
        return getLogger( logger ).isDebugEnabled();
    }

    public static void debug( Class logger, int messageKey, Document doc )
    {
        debug( logger, doc );
    }
}
