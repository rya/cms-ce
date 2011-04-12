/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;

/**
 * Nov 8, 2010
 */
public class RelatedContentResultSetWithOverridingContent
    implements RelatedContentResultSet
{
    private RelatedContentResultSet source;

    private ContentEntity overridingContent;

    public RelatedContentResultSetWithOverridingContent( RelatedContentResultSet source, ContentEntity overridingContent )
    {
        this.source = source;
        this.overridingContent = overridingContent;
    }

    public int size()
    {
        return source.size();
    }

    public boolean isEmpty()
    {
        return source.isEmpty();
    }

    public Collection<RelatedContent> getDistinctCollectionOfRelatedContent()
    {
        final Collection<RelatedContent> sourceCollection = source.getDistinctCollectionOfRelatedContent();
        List<RelatedContent> overridingCollection = new ArrayList<RelatedContent>( sourceCollection.size() );
        for ( RelatedContent rc : sourceCollection )
        {
            if ( rc.getContent().equals( overridingContent ) )
            {
                overridingCollection.add( doCreateOverridingRelatedContent( rc ) );
            }
            else
            {
                overridingCollection.add( rc );
            }
        }
        return overridingCollection;
    }

    public Set<ContentEntity> getDinstinctSetOfContent()
    {
        return source.getDinstinctSetOfContent();
    }

    public Collection<ContentKey> getContentKeys()
    {
        return source.getContentKeys();
    }

    public Iterable<RelatedParentContent> getRootRelatedParents( ContentEntity content )
    {
        return source.getRootRelatedParents( content );
    }

    public Iterable<RelatedChildContent> getRootRelatedChildren( ContentVersionEntity contentVersion )
    {
        return source.getRootRelatedChildren( contentVersion );
    }

    public RelatedContent getRelatedContent( ContentKey contentKey )
    {
        return source.getRelatedContent( contentKey );
    }

    public void overwriteRootRelatedChild( RelatedChildContent overwritingRCC )
    {
        source.overwriteRootRelatedChild( overwritingRCC );
    }

    public void overwrite( RelatedContentResultSet overwritingSet )
    {
        source.overwrite( overwritingSet );
    }

    public void retainRelatedRootChildren( ContentVersionKey parent, Collection<ContentKey> children )
    {
        source.retainRelatedRootChildren( parent, children );
    }

    private RelatedContent doCreateOverridingRelatedContent( RelatedContent relatedContent )
    {
        if ( relatedContent instanceof RelatedChildContent )
        {
            RelatedChildContent rcc = (RelatedChildContent) relatedContent;
            return new RelatedChildContent( rcc.getParentVersionKey(), overridingContent );
        }
        else
        {
            RelatedParentContent rpc = (RelatedParentContent) relatedContent;
            return new RelatedParentContent( rpc.getChildContentKey(), overridingContent );
        }
    }
}
