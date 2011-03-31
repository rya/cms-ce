/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.log;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the upgrade log.
 */
public final class UpgradeLog
{
    private final static Logger LOG = LoggerFactory.getLogger( UpgradeLog.class );

    private final List<UpgradeLogEntry> entries;

    public UpgradeLog()
    {
        this.entries = new ArrayList<UpgradeLogEntry>();
    }

    public synchronized List<UpgradeLogEntry> getEntries()
    {
        return new ArrayList<UpgradeLogEntry>( this.entries );
    }

    public void logInfo( int modelNumber, String message )
    {
        log( UpgradeLogLevel.INFO, modelNumber, message, null );
    }

    public void logWarning( int modelNumber, String message )
    {
        log( UpgradeLogLevel.WARNING, modelNumber, message, null );
    }

    public void logWarning( int modelNumber, String message, Throwable cause )
    {
        log( UpgradeLogLevel.WARNING, modelNumber, message, cause );
    }

    public void logError( int modelNumber, String message )
    {
        logError( modelNumber, message, null );
    }

    public void logError( int modelNumber, String message, Throwable cause )
    {
        log( UpgradeLogLevel.ERROR, modelNumber, message, cause );
    }

    private void log( UpgradeLogLevel level, int modelNumber, String message, Throwable cause )
    {
        log( new UpgradeLogEntry( level, modelNumber, message, cause ) );
    }

    private synchronized void log( UpgradeLogEntry entry )
    {
        if ( entry.getLevel() == UpgradeLogLevel.INFO )
        {
            LOG.info( entry.getMessage(), entry.getCause() );
        }
        else if ( entry.getLevel() == UpgradeLogLevel.WARNING )
        {
            LOG.warn( entry.getMessage(), entry.getCause() );
        }
        else if ( entry.getLevel() == UpgradeLogLevel.ERROR )
        {
            LOG.error( entry.getMessage(), entry.getCause() );
        }

        this.entries.add( entry );
    }

    public void clear()
    {
        this.entries.clear();
    }
}