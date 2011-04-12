/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.domain.AbstractResultSet;


public final class ContentResultSetNonLazy
    extends AbstractResultSet
    implements ContentResultSet
{
    private List<ContentEntity> contents;

    /**
     * Lazy initialized distinct set of the keys.
     */
    private Set<ContentKey> keySet;

    public static ContentResultSet createFrom( ContentResultSet base, ContentEntity extra )
    {
        List<ContentEntity> list = new ArrayList<ContentEntity>();
        list.addAll( base.getContents() );
        list.add( extra );
        return new ContentResultSetNonLazy( list, 0, list.size() );
    }

    public ContentResultSetNonLazy( ContentEntity content )
    {
        super( 0, 1 );
        this.contents = new ArrayList<ContentEntity>( 1 );
        this.contents.add( content );
    }

    /**
     * Creates an ContentResultSet with given list as contents.
     */
    public ContentResultSetNonLazy( List<ContentEntity> contents, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.contents = contents;
    }

    /**
     * Creates an ContentResultSet based on a collecion of contents.
     */
    public ContentResultSetNonLazy( Collection<ContentEntity> contents, int fromIndex, int totalCount )
    {
        super( fromIndex, totalCount );

        this.contents = new ArrayList<ContentEntity>();
        this.contents.addAll( contents );
    }

    /**
     * Creates an empty ContentResultSet.
     */
    public ContentResultSetNonLazy( int fromIndex )
    {
        super( fromIndex, 0 );

        this.contents = new ArrayList<ContentEntity>();
    }

    /**
     * @inheritDoc
     */
    public int getLength()
    {
        return this.contents.size();
    }

    /**
     * @inheritDoc
     */
    public ContentKey getKey( int index )
    {
        return this.contents.get( index ).getKey();
    }

    /**
     * @inheritDoc
     */
    public List<ContentKey> getKeys()
    {
        List<ContentKey> keys = new ArrayList<ContentKey>( contents.size() );
        for ( ContentEntity content : contents )
        {
            keys.add( content.getKey() );
        }
        return keys;
    }

    /**
     * @inheritDoc
     */
    public ContentEntity getContent( int index )
    {
        return this.contents.get( index );
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
        return this.contents;
    }

    public ContentResultSet createRandomizedResult( int newTotalCount )
    {
        if ( newTotalCount > getLength() )
        {
            newTotalCount = getLength();
        }

        Random randomGenerator = new Random( System.currentTimeMillis() );
        Set<ContentKey> addedContentKeys = new HashSet<ContentKey>( newTotalCount );
        List<ContentEntity> randomContents = new ArrayList<ContentEntity>( newTotalCount );
        while ( randomContents.size() < newTotalCount )
        {
            ContentEntity randomContent = getContent( randomGenerator.nextInt( getLength() ) );
            ContentKey randomContentKey = randomContent.getKey();
            if ( !addedContentKeys.contains( randomContentKey ) )
            {
                randomContents.add( randomContent );
                addedContentKeys.add( randomContentKey );
            }
        }
        return new ContentResultSetNonLazy( randomContents, 0, newTotalCount );
    }

    private void ensureInitializedKeySet()
    {
        if ( keySet == null )
        {
            keySet = new HashSet<ContentKey>();
            for ( ContentEntity content : contents )
            {
                keySet.add( content.getKey() );
            }
        }
    }

}