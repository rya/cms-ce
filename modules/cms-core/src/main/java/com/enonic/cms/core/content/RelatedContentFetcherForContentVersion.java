/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;

import org.springframework.util.Assert;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.resultset.RelatedChildContent;
import com.enonic.cms.domain.content.resultset.RelatedContent;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetImpl;

/**
 * Jun 11, 2009
 */
public class RelatedContentFetcherForContentVersion
    extends AbstractRelatedContentFetcher
{

    private Collection<ContentVersionEntity> originallyRequestedContentVersions;

    public RelatedContentFetcherForContentVersion( ContentDao contentDao, ContentAccessResolver contentAccessResolver )
    {
        super( contentDao, contentAccessResolver );
    }

    public RelatedContentResultSet fetch( final Collection<ContentVersionEntity> versions )
    {
        return doFetch( versions );
    }

    private RelatedContentResultSet doFetch( Collection<ContentVersionEntity> versions )
    {
        Assert.notNull( versions, "versions cannot be null" );

        originallyRequestedContentVersions = versions;
        relatedContentResultSet = new RelatedContentResultSetImpl();

        boolean fetchChildren = maxChildrenLevel > 0;
        if ( fetchChildren )
        {
            Collection<RelatedChildContent> rootRelatedChildren = doFindRelatedChildren( versions );
            if ( versions.size() > 0 )
            {
                doAddAndFetchChildren( rootRelatedChildren, maxChildrenLevel );
                for ( RelatedChildContent rootRelatedChild : rootRelatedChildren )
                {
                    if ( isAddableToRootRelated( rootRelatedChild ) )
                    {
                        relatedContentResultSet.addRootRelatedChild( rootRelatedChild );
                    }
                }
            }
        }

        return relatedContentResultSet;
    }

    @Override
    protected boolean isAddableToRootRelated( RelatedContent relatedToAdd )
    {
        return includeOfflineContent() || isAvailable( relatedToAdd );
    }

    @Override
    protected boolean isAddable( final RelatedContent relatedToAdd )
    {
        final ContentEntity content = relatedToAdd.getContent();
        final boolean contentIsAllreadyVisited = visitedChildRelatedContent.contains( relatedToAdd.getContent().getKey() );

        final boolean contentVersionIsInOriginallyRequestedContentVersionSet =
            originallyRequestedContentVersions.contains( content.getMainVersion() );

        return ( includeOfflineContent() || isAvailable( relatedToAdd ) ) && !contentIsAllreadyVisited &&
            !contentVersionIsInOriginallyRequestedContentVersionSet;
    }
}
