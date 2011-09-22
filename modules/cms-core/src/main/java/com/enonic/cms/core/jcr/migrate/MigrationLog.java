/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MigrationLog
    implements Log
{

    private static final Logger LOG = LoggerFactory.getLogger( MigrationLog.class );

    private final List<LogEntry> entries;

    MigrationLog()
    {
        entries = new CopyOnWriteArrayList<LogEntry>();
    }

    @Override
    public List<LogEntry> getEntries()
    {
        return new ArrayList<LogEntry>( entries );
    }

    @Override
    public void logInfo( String message )
    {
        log( new LogEntry( LogLevel.INFO, message ) );
    }

    @Override
    public void logWarning( String message )
    {
        log( new LogEntry( LogLevel.WARNING, message ) );
    }

    @Override
    public void logError( String message )
    {
        log( new LogEntry( LogLevel.ERROR, message ) );
    }

    @Override
    public void logError( String message, Throwable cause )
    {
        log( new LogEntry( LogLevel.ERROR, message, cause ) );
    }

    private void log( LogEntry entry )
    {
        switch ( entry.getLevel() )
        {
            case INFO:
                LOG.info( entry.getMessage(), entry.getCause() );
                break;

            case WARNING:
                LOG.warn( entry.getMessage(), entry.getCause() );
                break;

            case ERROR:
                LOG.error( entry.getMessage(), entry.getCause() );
                break;
        }

        this.entries.add( entry );
    }

    @Override
    public void clear()
    {
        entries.clear();
    }
}
