/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.domain.content.ContentAndVersion;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.ContentResultSetWithOverridingContent;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetWithOverridingContent;

/**
 * Sep 30, 2010
 */
public class ContentPreviewContext
        implements Serializable
{
    private ContentAndVersion contentPreviewed;

    /**
     * Content be available online even if they are drafts or archived.
     */
    private Set<ContentKey> contentToBeAvailableOnline = new HashSet<ContentKey>();

    public ContentPreviewContext( ContentAndVersion contentPreviewed )
    {
        this.contentPreviewed = contentPreviewed;

        for ( ContentKey contentKey : this.contentPreviewed.getVersion().getContentData().resolveRelatedContentKeys() )
        {
            this.contentToBeAvailableOnline.add( contentKey );
        }
    }

    public ContentAndVersion getContentAndVersionPreviewed()
    {
        return contentPreviewed;
    }

    public ContentEntity getContentPreviewed()
    {
        return contentPreviewed.getContent();
    }

    public ContentVersionEntity getVersionPreviewed()
    {
        return contentPreviewed.getVersion();
    }

    public ContentResultSet applyPreviewedContentOnContentResultSet( ContentResultSet contents, int[] contentKeys )
    {
        List<ContentKey> conentKeysList = new ArrayList<ContentKey>();
        if ( contentKeys != null && contentKeys.length > 0 )
        {
            for ( int contentKey : contentKeys )
            {
                conentKeysList.add( new ContentKey( contentKey ) );
            }
        }
        return applyPreviewedContentOnContentResultSet( contents, conentKeysList );
    }

    public ContentResultSet applyPreviewedContentOnContentResultSet( ContentResultSet contents,
                                                                     Collection<ContentKey> contentKeys )
    {
        ContentEntity contentInPreview = getContentAndVersionPreviewed().getContent();

        boolean contentsContainsContentInPreview = contents.containsContent( contentInPreview.getKey() );
        boolean previewedContentIsRequested = contentInPreview.getKey().isInCollection( contentKeys );

        if ( previewedContentIsRequested && !contentsContainsContentInPreview )
        {
            return appendToContentResultSet( contents );
        }
        else
        {
            return overrideContentResultSet( contents );
        }
    }

    public ContentResultSet overrideContentResultSet( ContentResultSet contents )
    {
        return new ContentResultSetWithOverridingContent( contents, getContentAndVersionPreviewed().getContent() );
    }

    public ContentResultSet appendToContentResultSet( ContentResultSet contents )
    {
        return ContentResultSetNonLazy.createFrom( contents, getContentAndVersionPreviewed().getContent() );
    }

    public boolean isContentPreviewed( ContentEntity other )
    {
        return other.equals( contentPreviewed.getContent() );
    }

    public boolean isContentPreviewed( ContentKey otherKey )
    {
        return otherKey.equals( contentPreviewed.getContent().getKey() );
    }

    public void registerContentToBeAvailableOnline( RelatedContentResultSet relatedContentResultSet )
    {
        contentToBeAvailableOnline.addAll( relatedContentResultSet.getContentKeys() );
    }

    public void registerContentToBeAvailableOnline( ContentResultSet contentResultSet )
    {
        contentToBeAvailableOnline.addAll( contentResultSet.getKeys() );
    }

    public boolean treatContentAsAvailableEvenIfOffline( ContentKey contentKey )
    {
        return contentToBeAvailableOnline.contains( contentKey );
    }

    public RelatedContentResultSet overrideRelatedContentResultSet( RelatedContentResultSet source )
    {
        return new RelatedContentResultSetWithOverridingContent( source, getContentAndVersionPreviewed().getContent() );
    }
}
