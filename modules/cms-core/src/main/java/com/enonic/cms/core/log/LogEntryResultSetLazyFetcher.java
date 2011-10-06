/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.index.LogEntryEntityFetcher;

import com.enonic.cms.domain.AbstractResultSet;

public final class LogEntryResultSetLazyFetcher
    extends AbstractResultSet
    implements LogEntryResultSet
{

    private final LogEntryEntityFetcher fetcher;

    /**
     * Content keys.
     */
    private final List<LogEntryKey> keys;

    private Map<LogEntryKey, LogEntryEntity> entries;

    public LogEntryResultSetLazyFetcher( LogEntryEntityFetcher fetcher, List<LogEntryKey> keys, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        if ( fetcher == null )
        {
            throw new IllegalArgumentException( "The fetcher of the content result set can NOT be null.  This will cause problems!" );
        }
        this.fetcher = fetcher;
        if ( keys == null )
        {
            this.keys = new ArrayList<LogEntryKey>();
        }
        else
        {
            this.keys = keys;
        }
    }

    /**
     * @inheritDoc
     */
    public int getLength()
    {
        if ( keys == null )
        {
            return 0;
        }
        else
        {
            return keys.size();
        }
    }

    /**
     * @inheritDoc
     */
    public LogEntryKey getKey( int index )
    {
        if ( keys == null )
        {
            return null;
        }
        else
        {
            return keys.get( index );
        }
    }

    /**
     * @inheritDoc
     */
    public List<LogEntryKey> getKeys()
    {
        return this.keys;
    }

    /**
     * @inheritDoc
     */
    public LogEntryEntity getLogEntry( int index )
    {
        ensureEntities();

        return this.entries.get( this.keys.get( index ) );
    }

    /**
     * @inheritDoc
     */
    public Collection<LogEntryEntity> getLogEntries()
    {
        ensureEntities();

        return this.entries.values();
    }

    private void ensureEntities()
    {
        if ( this.entries == null )
        {
            this.entries = this.fetcher.fetch( this.keys );
        }
    }

}
