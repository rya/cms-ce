/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

/**
 * Sep 16, 2010
 */
public class ContentResultSetWithOverridingContent
    implements ContentResultSet
{
    private ContentResultSet source;

    private ContentEntity overridingContent;

    private Collection<ContentEntity> cachedContents;

    public ContentResultSetWithOverridingContent( ContentResultSet source, ContentEntity overridingContent )
    {
        this.source = source;
        this.overridingContent = overridingContent;
    }

    public ContentKey getKey( int index )
    {
        return source.getKey( index );
    }

    public List<ContentKey> getKeys()
    {
        return source.getKeys();
    }

    public boolean containsContent( ContentKey contentKey )
    {
        return source.containsContent( contentKey );
    }

    public int getLength()
    {
        return source.getLength();
    }

    public int getFromIndex()
    {
        return source.getFromIndex();
    }

    public int getTotalCount()
    {
        return source.getTotalCount();
    }

    public boolean hasErrors()
    {
        return source.hasErrors();
    }

    public List<String> getErrors()
    {
        return source.getErrors();
    }

    public ContentEntity getContent( int index )
    {
        ContentEntity content = source.getContent( index );
        if ( content == null )
        {
            return null;
        }

        if ( isContentToBeOverridden( content ) )
        {
            return overridingContent;
        }

        return content;
    }

    public Collection<ContentEntity> getContents()
    {
        Collection<ContentEntity> contents = source.getContents();
        if ( cachedContents == null )
        {
            cachedContents = ensureOverridenContentIsOverride( contents );
        }

        return cachedContents;
    }

    public ContentResultSet createRandomizedResult( int count )
    {
        ContentResultSet randomizedResult = source.createRandomizedResult( count );
        return new ContentResultSetWithOverridingContent( randomizedResult, overridingContent );
    }

    private boolean isContentToBeOverridden( ContentEntity content )
    {
        return content.getKey().equals( overridingContent.getKey() );
    }

    private Collection<ContentEntity> ensureOverridenContentIsOverride( Collection<ContentEntity> collection )
    {
        List<ContentEntity> contentsWithOverridenContent = new ArrayList<ContentEntity>();
        for ( ContentEntity content : collection )
        {
            if ( isContentToBeOverridden( content ) )
            {
                contentsWithOverridenContent.add( overridingContent );
            }
            else
            {
                contentsWithOverridenContent.add( content );
            }
        }
        return contentsWithOverridenContent;
    }
}
