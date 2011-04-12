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
import java.util.Random;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentEntityFetcher;
import com.enonic.cms.domain.AbstractResultSet;


public final class ContentResultSetLazyFetcher
    extends AbstractResultSet
    implements ContentResultSet
{

    private final ContentEntityFetcher fetcher;

    /**
     * Content keys.
     */
    private final List<ContentKey> keys;

    /**
     * Lazy initialized distinct set of the keys.
     */
    private Set<ContentKey> keySet = null;

    private Map<ContentKey, ContentEntity> contents;

    public ContentResultSetLazyFetcher( ContentEntityFetcher fetcher, List<ContentKey> keys, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        if ( fetcher == null )
        {
            throw new IllegalArgumentException( "The fetcher of the content result set can NOT be null.  This will cause problems!" );
        }
        this.fetcher = fetcher;
        if ( keys == null )
        {
            this.keys = new ArrayList<ContentKey>();
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
    public ContentKey getKey( int index )
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
    public List<ContentKey> getKeys()
    {
        return this.keys;
    }

    /**
     * @inheritDoc
     */
    public ContentEntity getContent( int index )
    {
        ensureEntities();

        return this.contents.get( this.keys.get( index ) );
    }

    public boolean containsContent( ContentKey contentKey )
    {
        ensureInitializedKeySet();

        return keySet.contains( contentKey );
    }

    /**
     * @inheritDoc
     */
    public Collection<ContentEntity> getContents()
    {
        ensureEntities();

        return this.contents.values();
    }

    public ContentResultSet createRandomizedResult( int newTotalCount )
    {
        if ( newTotalCount > getLength() )
        {
            newTotalCount = getLength();
        }

        Random randomGenerator = new Random( System.currentTimeMillis() );
        Set<ContentKey> addedContentKeys = new HashSet<ContentKey>( newTotalCount );
        List<ContentKey> randomContentKeys = new ArrayList<ContentKey>( newTotalCount );
        while ( randomContentKeys.size() < newTotalCount )
        {
            ContentKey randomContentKey = getKey( randomGenerator.nextInt( getLength() ) );
            if ( !addedContentKeys.contains( randomContentKey ) )
            {
                randomContentKeys.add( randomContentKey );
                addedContentKeys.add( randomContentKey );
            }
        }
        return new ContentResultSetLazyFetcher( fetcher, randomContentKeys, 0, newTotalCount );
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
            keySet = new HashSet<ContentKey>();
            for ( ContentKey key : keys )
            {
                keySet.add( key );
            }
        }
    }
}
