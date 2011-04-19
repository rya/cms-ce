package com.enonic.cms.api.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogFacade
{
    private final static String FQCN = LogFacade.class.getName();

    private final Logger logger;

    private LogFacade( final String clz )
    {
        this.logger = Logger.getLogger( clz );
    }

    public void debug( final String message, final Object... args )
    {
        log( FQCN, Level.FINEST, message, args, null );
    }

    public void debug( final Throwable cause, final String message, final Object... args )
    {
        log( FQCN, Level.FINEST, message, args, cause );
    }

    public void info( final String message, final Object... args )
    {
        log( FQCN, Level.INFO, message, args, null );
    }

    public void info( final Throwable cause, final String message, final Object... args )
    {
        log( FQCN, Level.INFO, message, args, cause );
    }

    public void warning( final String message, final Object... args )
    {
        log( FQCN, Level.WARNING, message, args, null );
    }

    public void warning( final Throwable cause, final String message, final Object... args )
    {
        log( FQCN, Level.WARNING, message, args, cause );
    }

    public void error( final String message, final Object... args )
    {
        log( FQCN, Level.SEVERE, message, args, null );
    }

    public void error( final Throwable cause, final String message, final Object... args )
    {
        log( FQCN, Level.SEVERE, message, args, cause );
    }

    public void debugFqcn( final String fqcn, final String message, final Object... args )
    {
        log( fqcn, Level.FINEST, message, args, null );
    }

    public void debugFqcn( final String fqcn, final Throwable cause, final String message, final Object... args )
    {
        log( fqcn, Level.FINEST, message, args, cause );
    }

    public void infoFqcn( final String fqcn, final String message, final Object... args )
    {
        log( fqcn, Level.INFO, message, args, null );
    }

    public void infoFqcn( final String fqcn, final Throwable cause, final String message, final Object... args )
    {
        log( fqcn, Level.INFO, message, args, cause );
    }

    public void warningFqcn( final String fqcn, final String message, final Object... args )
    {
        log( fqcn, Level.WARNING, message, args, null );
    }

    public void warningFqcn( final String fqcn, final Throwable cause, final String message, final Object... args )
    {
        log( fqcn, Level.WARNING, message, args, cause );
    }

    public void errorFqcn( final String fqcn, final String message, final Object... args )
    {
        log( fqcn, Level.SEVERE, message, args, null );
    }

    public void errorFqcn( final String fqcn, final Throwable cause, final String message, final Object... args )
    {
        log( fqcn, Level.SEVERE, message, args, cause );
    }

    private void log( final String fqcn, final Level level, final String message, final Object[] args, final Throwable cause )
    {
        if ( !this.logger.isLoggable( level ) )
        {
            return;
        }

        final LogRecord record = new LogRecord( level, MessageFormat.format( message, args ) );
        record.setThrown( cause );

        final StackTraceElement source = findStackTraceElement( fqcn );
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

    public boolean isErrorEnabled()
    {
        return this.logger.isLoggable( Level.SEVERE );
    }

    private StackTraceElement findStackTraceElement( final String fqcn )
    {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        boolean foundFcqn = false;
        for ( final StackTraceElement elem : trace )
        {
            if ( elem.getClassName().equals( fqcn ) )
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
        return new LogFacade( clz.getName() );
    }

    public static LogFacade get( final String clz )
    {
        return new LogFacade( clz );
    }
}
