/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.domain.AbstractResultSet;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.index.ContentVersionEntityFetcher;

public final class ContentVersionResultSetLazyFetcher
    extends AbstractResultSet
    implements ContentVersionResultSet
{

    private final ContentVersionEntityFetcher fetcher;

    /**
     * Content keys.
     */
    private final List<ContentVersionKey> keys;

    /**
     * Lazy initialized distinct set of the keys.
     */
    private Set<ContentVersionKey> keySet = null;

    private Map<ContentVersionKey, ContentVersionEntity> contents;

    public ContentVersionResultSetLazyFetcher( ContentVersionEntityFetcher fetcher, List<ContentVersionKey> keys, int fromIndex,
                                               int totalCount )
    {
        super( fromIndex, totalCount );

        if ( fetcher == null )
        {
            throw new IllegalArgumentException( "The fetcher of the content result set can NOT be null.  This will cause problems!" );
        }
        this.fetcher = fetcher;
        if ( keys == null )
        {
            this.keys = new ArrayList<ContentVersionKey>();
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
    public ContentVersionKey getKey( int index )
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
    public List<ContentVersionKey> getKeys()
    {
        return this.keys;
    }

    /**
     * @inheritDoc
     */
    public ContentVersionEntity getContent( int index )
    {
        ensureEntities();

        return this.contents.get( this.keys.get( index ) );
    }

    public boolean containsContent( ContentVersionKey versionKey )
    {
        ensureInitializedKeySet();

        return keySet.contains( versionKey );
    }

    /**
     * @inheritDoc
     */
    public Collection<ContentVersionEntity> getContents()
    {
        ensureEntities();

        return this.contents.values();
    }

    private void ensureEntities()
    {
        if ( this.contents == null )
        {
            this.contents = this.fetcher.fetch( this.keys );
        }
    }

    private void ensureInitializedKeySet()
    {
        if ( keySet == null )
        {
            keySet = new HashSet<ContentVersionKey>();
            for ( ContentVersionKey key : keys )
            {
                keySet.add( key );
            }
        }
    }
}
