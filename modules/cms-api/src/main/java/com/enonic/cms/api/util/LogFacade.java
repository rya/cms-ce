package com.enonic.cms.api.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogFacade
{
    private final Logger logger;
    private final String fcqn;

    private LogFacade( final Class clz, final Class fcqn )
    {
        this.logger = Logger.getLogger( clz.getName() );
        this.fcqn = fcqn != null ? fcqn.getName() : LogFacade.class.getName();
    }

    public void debug( final String message, final Object... args )
    {
        log( Level.FINEST, message, args, null );
    }

    public void debugCause(final String message, final Throwable cause, final Object... args)
    {
        log( Level.FINEST, message, args, cause );
    }

    public void info( final String message, final Object... args )
    {
        log( Level.INFO, message, args, null );
    }

    public void infoCause(final String message, final Throwable cause, final Object... args)
    {
        log( Level.INFO, message, args, cause );
    }

    public void warning( final String message, final Object... args )
    {
        log( Level.WARNING, message, args, null );
    }

    public void warningCause(final String message, final Throwable cause, final Object... args)
    {
        log( Level.WARNING, message, args, cause );
    }

    public void error( final String message, final Object... args )
    {
        log( Level.SEVERE, message, args, null );
    }

    public void errorCause(final String message, final Throwable cause, final Object... args)
    {
        log( Level.SEVERE, message, args, cause );
    }

    private void log( final Level level, final String message, final Object[] args, final Throwable cause )
    {
        if ( !this.logger.isLoggable( level ) )
        {
            return;
        }

        final LogRecord record = new LogRecord( level, MessageFormat.format( message, args ) );
        record.setThrown( cause );

        final StackTraceElement source = findStackTraceElement();
        if ( source != null )
        {
            record.setSourceClassName( source.getClassName() );
            record.setSourceMethodName( source.getMethodName() );
        }

        this.logger.log( record );
    }

    public boolean isDebugEnabled()
    {
        return this.logger.isLoggable( Level.FINEST );
    }

    public boolean isInfoEnabled()
    {
        return this.logger.isLoggable( Level.INFO );
    }

    public boolean isWarningEnabled()
    {
        return this.logger.isLoggable( Level.WARNING );
    }

    private StackTraceElement findStackTraceElement()
    {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        boolean foundFcqn = false;
        for ( final StackTraceElement elem : trace )
        {
            if ( elem.getClassName().equals( this.fcqn ) )
            {
                foundFcqn = true;
            }
            else if ( foundFcqn )
            {
                return elem;
            }
        }

        return null;
    }

    public static LogFacade get( final Class clz )
    {
        return new LogFacade( clz, null );
    }

    public static LogFacade get( final Class clz, final Class fcqn )
    {
        return new LogFacade( clz, fcqn );
    }
}
