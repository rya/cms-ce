/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.domain.AbstractResultSet;


public final class ContentVersionResultSetNonLazy
    extends AbstractResultSet
    implements ContentVersionResultSet
{
    private List<ContentVersionEntity> versionContents;

    /**
     * Lazy initialized distinct set of the keys.
     */
    private Set<ContentVersionKey> keySet;

    public ContentVersionResultSetNonLazy( ContentVersionEntity content )
    {
        super( 0, 1 );
        this.versionContents = new ArrayList<ContentVersionEntity>( 1 );
        this.versionContents.add( content );
    }

    /**
     * Creates an ContentResultSet with given list as contents.
     */
    public ContentVersionResultSetNonLazy( List<ContentVersionEntity> contents, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.versionContents = contents;
    }

    /**
     * Creates an ContentResultSet based on a collecion of contents.
     */
    public ContentVersionResultSetNonLazy( Collection<ContentVersionEntity> contents, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.versionContents = new ArrayList<ContentVersionEntity>();
        this.versionContents.addAll( contents );
    }

    /**
     * Creates an empty ContentResultSet.
     */
    public ContentVersionResultSetNonLazy( int fromIndex )
    {
        super( fromIndex, 0 );

        this.versionContents = new ArrayList<ContentVersionEntity>();
    }

    /**
     * @inheritDoc
     */
    public int getLength()
    {
        return this.versionContents.size();
    }

    /**
     * @inheritDoc
     */
    public ContentVersionKey getKey( int index )
    {
        return this.versionContents.get( index ).getKey();
    }

    /**
     * @inheritDoc
     */
    public List<ContentVersionKey> getKeys()
    {
        List<ContentVersionKey> keys = new ArrayList<ContentVersionKey>( versionContents.size() );
        for ( ContentVersionEntity content : versionContents )
        {
            keys.add( content.getKey() );
        }
        return keys;
    }

    /**
     * @inheritDoc
     */
    public ContentVersionEntity getContent( int index )
    {
        return this.versionContents.get( index );
    }

    public boolean containsContent( ContentVersionKey contentKey )
    {
        ensureInitializedKeySet();

        return keySet.contains( contentKey );
    }

    /**
     * @inheritDoc
     */
    public Collection<ContentVersionEntity> getContents()
    {
        return this.versionContents;
    }

    private void ensureInitializedKeySet()
    {
        if ( keySet == null )
        {
            keySet = new HashSet<ContentVersionKey>();
            for ( ContentVersionEntity content : versionContents )
            {
                keySet.add( content.getKey() );
            }
        }
    }

}