/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateTime;

import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Nov 16, 2010
 */
public class GetContentExecutor
{
    private ContentService contentService;

    private ContentDao contentDao;

    private UserDao userDao;

    private DateTime now;

    private PreviewContext previewContext;

    private UserKey user;

    private String query;

    private String orderBy;

    private int index;

    private int count;

    private int childrenLevel = 0;

    private int parentLevel = 0;

    private int parentChildrenLevel = 0;

    private Collection<ContentKey> contentFilter;

    private Collection<CategoryKey> categoryFilter;

    private int categoryLevels = 0;

    private Collection<ContentTypeKey> contentTypeFilter;

    private boolean includeOfflineContent = false;

    private boolean includeOfflineRelatedContent = false;


    public GetContentExecutor( ContentService contentService, ContentDao contentDao, UserDao userDao, DateTime now,
                               PreviewContext previewContext )
    {
        this.contentService = contentService;
        this.contentDao = contentDao;
        this.userDao = userDao;
        this.now = now;
        this.previewContext = previewContext;
    }

    public GetContentExecutor user( UserKey value )
    {
        this.user = value;
        return this;
    }

    public GetContentExecutor query( String value )
    {
        this.query = value;
        return this;
    }

    public GetContentExecutor orderBy( String value )
    {
        this.orderBy = value;
        return this;
    }

    public GetContentExecutor index( int value )
    {
        this.index = value;
        return this;
    }

    public GetContentExecutor count( int value )
    {
        this.count = value;
        return this;
    }

    public GetContentExecutor childrenLevel( int value )
    {
        this.childrenLevel = value;
        return this;
    }

    public GetContentExecutor parentLevel( int value )
    {
        this.parentLevel = value;
        return this;
    }

    public GetContentExecutor parentChildrenLevel( int value )
    {
        this.parentChildrenLevel = value;
        return this;
    }

    public GetContentExecutor contentFilter( Collection<ContentKey> value )
    {
        this.contentFilter = value;
        return this;
    }

    public GetContentExecutor categoryFilter( Collection<CategoryKey> value, int categoryLevels )
    {
        this.categoryFilter = value;
        this.categoryLevels = categoryLevels;
        return this;
    }

    public GetContentExecutor contentTypeFilter( Collection<ContentTypeKey> value )
    {
        this.contentTypeFilter = value;
        return this;
    }

    public GetContentExecutor includeOfflineContent()
    {
        this.includeOfflineContent = true;
        return this;
    }

    public GetContentExecutor includeOfflineRelatedContent()
    {
        this.includeOfflineRelatedContent = true;
        return this;
    }


    public GetContentResult execute()
    {
        ContentByContentQuery contentByContentQuery = new ContentByContentQuery();
        contentByContentQuery.setQuery( query );
        contentByContentQuery.setOrderBy( orderBy );
        contentByContentQuery.setIndex( index );
        contentByContentQuery.setCount( count );
        contentByContentQuery.setUser( resolveUser( user ) );
        contentByContentQuery.setContentKeyFilter( contentFilter );
        contentByContentQuery.setCategoryKeyFilter( categoryFilter, categoryLevels );
        contentByContentQuery.setContentTypeFilter( contentTypeFilter );
        if ( includeOfflineContent )
        {
            contentByContentQuery.setFilterIncludeOfflineContent();
        }
        else
        {
            contentByContentQuery.setFilterContentOnlineAt( now.toDate() );
        }

        ContentResultSet contents = contentService.queryContent( contentByContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().applyPreviewedContentOnContentResultSet( contents, contentFilter );
        }

        RelatedContentQuery relatedContentSpec = new RelatedContentQuery( now.toDate() );
        relatedContentSpec.setUser( resolveUser( user ) );
        relatedContentSpec.setContentResultSet( contents );
        relatedContentSpec.setParentLevel( parentLevel );
        relatedContentSpec.setChildrenLevel( childrenLevel );
        relatedContentSpec.setParentChildrenLevel( parentChildrenLevel );
        relatedContentSpec.setIncludeOnlyMainVersions( true );
        if ( includeOfflineRelatedContent )
        {
            relatedContentSpec.setFilterIncludeOfflineContent();
        }

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentSpec );

        if ( previewContext.isPreviewingContent() )
        {
            RelatedContentResultSet relatedContentsForPreview = resolveRelatedContentsForPreview( relatedContentSpec, relatedContents );
            previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContentsForPreview );
            return new GetContentResult( contents, relatedContentsForPreview );
        }
        else
        {
            return new GetContentResult( contents, relatedContents );
        }
    }

    private RelatedContentResultSet resolveRelatedContentsForPreview( RelatedContentQuery originalRelatedContentQuery,
                                                                      RelatedContentResultSet originalRelatedContents )
    {
        ContentVersionKey versionPreviewedKey = previewContext.getContentPreviewContext().getVersionPreviewed().getKey();

        Set<ContentKey> relatedChildrenToVersionPreviewed =
            previewContext.getContentPreviewContext().getVersionPreviewed().getContentData().resolveRelatedContentKeys();

        // Ensure to retain only related children that are in the previewed version.
        originalRelatedContents.retainRelatedRootChildren( versionPreviewedKey, relatedChildrenToVersionPreviewed );

        // Fetch related content that is even offline..
        RelatedContentResultSet relatedContentsForPreviewedContent = fetchRelatedContentIncludingOffline( originalRelatedContentQuery );
        // Ensure to retain only related children that are in the previewed version
        relatedContentsForPreviewedContent.retainRelatedRootChildren( versionPreviewedKey, relatedChildrenToVersionPreviewed );

        // Ensure the offline content is included
        originalRelatedContents.overwrite( relatedContentsForPreviewedContent );

        // Ensure unsaved related children are included
        ensureUnsavedRelatedChildrenAreIncluded( originalRelatedContents );

        return previewContext.getContentPreviewContext().overrideRelatedContentResultSet( originalRelatedContents );
    }

    private RelatedContentResultSet fetchRelatedContentIncludingOffline( final RelatedContentQuery originalRelatedContentQuery )
    {
        final RelatedContentQuery relatedSpecForPreviewedContent = new RelatedContentQuery( originalRelatedContentQuery );
        relatedSpecForPreviewedContent.setContentResultSet(
            new ContentResultSetNonLazy( previewContext.getContentPreviewContext().getContentAndVersionPreviewed().getContent() ) );
        relatedSpecForPreviewedContent.setFilterIncludeOfflineContent();

        return contentService.queryRelatedContent( relatedSpecForPreviewedContent );
    }

    private void ensureUnsavedRelatedChildrenAreIncluded( RelatedContentResultSet originalRelatedContents )
    {
        final ContentVersionEntity versionPreviewed = previewContext.getContentPreviewContext().getVersionPreviewed();
        final ContentVersionKey versionPreviewedKey = versionPreviewed.getKey();

        for ( ContentKey relatedContentKey : versionPreviewed.getContentData().resolveRelatedContentKeys() )
        {
            ContentEntity content = contentDao.findByKey( relatedContentKey );
            if ( content != null && !content.isDeleted() )
            {
                RelatedChildContent relatedChildContent = new RelatedChildContent( versionPreviewedKey, content );
                originalRelatedContents.overwriteRootRelatedChild( relatedChildContent );
            }
        }
    }

    private UserEntity resolveUser( UserKey userKey )
    {
        return userDao.findByKey( userKey );
    }
}
