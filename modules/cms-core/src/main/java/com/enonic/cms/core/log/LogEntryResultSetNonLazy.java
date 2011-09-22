/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.domain.AbstractResultSet;


public final class LogEntryResultSetNonLazy
    extends AbstractResultSet
    implements LogEntryResultSet
{
    private List<LogEntryEntity> logEntries;

    public LogEntryResultSetNonLazy( LogEntryEntity entry )
    {
        super( 0, 1 );
        this.logEntries = new ArrayList<LogEntryEntity>( 1 );
        this.logEntries.add( entry );
    }

    /**
     * Creates an ContentResultSet with given list as contents.
     */
    public LogEntryResultSetNonLazy( List<LogEntryEntity> entries, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.logEntries = entries;
    }

    /**
     * Creates an ContentResultSet based on a collecion of contents.
     */
    public LogEntryResultSetNonLazy( Collection<LogEntryEntity> entries, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.logEntries = new ArrayList<LogEntryEntity>();
        this.logEntries.addAll( entries );
    }

    /**
     * Creates an empty ContentResultSet.
     */
    public LogEntryResultSetNonLazy( int fromIndex )
    {
        super( fromIndex, 0 );

        this.logEntries = new ArrayList<LogEntryEntity>();
    }

    /**
     * @inheritDoc
     */
    public int getLength()
    {
        return this.logEntries.size();
    }

    /**
     * @inheritDoc
     */
    public LogEntryKey getKey( int index )
    {
        return this.logEntries.get( index ).getKey();
    }

    /**
     * @inheritDoc
     */
    public List<LogEntryKey> getKeys()
    {
        List<LogEntryKey> keys = new ArrayList<LogEntryKey>( logEntries.size() );
        for ( LogEntryEntity content : logEntries )
        {
            keys.add( content.getKey() );
        }
        return keys;
    }

    /**
     * @inheritDoc
     */
    public LogEntryEntity getLogEntry( int index )
    {
        return this.logEntries.get( index );
    }

    /**
     * @inheritDoc
     */
    public Collection<LogEntryEntity> getLogEntries()
    {
        return this.logEntries;
    }


}