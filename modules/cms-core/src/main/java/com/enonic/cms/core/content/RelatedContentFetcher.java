/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.content.resultset.RelatedParentContent;


public class RelatedContentFetcher
    extends AbstractRelatedContentFetcher
{
    private Integer maxParentLevel;

    private Integer maxParentChildrenLevel;

    private boolean includeOnlyMainVersions = true;

    /**
     * The content which this fetcher should find the related content of.
     */
    private ContentResultSet contentResultSet;


    public RelatedContentFetcher( ContentDao contentDao, ContentAccessResolver contentAccessResolver )
    {
        super( contentDao, contentAccessResolver );
    }

    public RelatedContentResultSet fetch( final ContentEntity content )
    {
        this.contentResultSet = new ContentResultSetNonLazy( content );
        return doFetch();
    }

    public RelatedContentResultSet fetch( final ContentResultSet contentResultSet )
    {
        this.contentResultSet = contentResultSet;
        return doFetch();
    }

    private RelatedContentResultSet doFetch()
    {
        relatedContentResultSet = new RelatedContentResultSetImpl();

        boolean fetchChildren = maxChildrenLevel > 0;
        if ( fetchChildren )
        {
            Collection<RelatedChildContent> rootRelatedChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( contentResultSet.getContents() ) );
            if ( rootRelatedChildren.size() > 0 )
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

        boolean fetchParents = maxParentLevel > 0;
        if ( fetchParents )
        {
            Collection<RelatedParentContent> rootRelatedParents = doFindRelatedParents( contentResultSet.getKeys() );
            if ( rootRelatedParents.size() > 0 )
            {
                doAddAndFetchParents( rootRelatedParents, maxParentLevel );
                for ( RelatedParentContent rootRelatedParent : rootRelatedParents )
                {
                    if ( isAddableToRootRelated( rootRelatedParent ) )
                    {
                        relatedContentResultSet.addRootRelatedParent( rootRelatedParent );
                    }
                }
            }
        }

        return relatedContentResultSet;
    }

    private List<RelatedParentContent> doAddAndFetchParents( final Collection<RelatedParentContent> parentsToAdd, final int level )
    {
        final int nextLevel = level - 1;

        List<RelatedParentContent> addedRelatedContent = new ArrayList<RelatedParentContent>();
        final List<ContentEntity> addedContent = new ArrayList<ContentEntity>();

        for ( RelatedParentContent relatedToAdd : parentsToAdd )
        {
            if ( isAddable( relatedToAdd ) )
            {
                addedRelatedContent.add( relatedToAdd );
                addedContent.add( relatedToAdd.getContent() );
                relatedContentResultSet.add( relatedToAdd );

                registerForFastAccess( relatedToAdd );

                doConnectRelatedContent( relatedToAdd );
            }

            visitedParentRelatedContent.add( relatedToAdd.getContent().getKey() );
        }

        // fetch parent children...
        final boolean fetchParentChildren = level == maxParentLevel && maxParentChildrenLevel > 0;
        if ( fetchParentChildren )
        {
            final Collection<RelatedChildContent> nextLevelChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( addedContent ) );
            if ( nextLevelChildren.size() > 0 )
            {
                doAddAndFetchChildren( nextLevelChildren, maxParentChildrenLevel );
            }
        }

        // fetch more parents...
        final boolean atLastLevel = nextLevel == 0;
        if ( !atLastLevel )
        {
            final Collection<RelatedParentContent> nextLevelParents = doFindRelatedParents( gatherContentKeysFromContent( addedContent ) );
            if ( nextLevelParents.size() > 0 )
            {
                doAddAndFetchParents( nextLevelParents, nextLevel );
            }
        }

        return addedRelatedContent;
    }

    @Override
    protected boolean isAddableToRootRelated( RelatedContent relatedToAdd )
    {
        return includeOfflineContent() || isAvailable( relatedToAdd );
    }

    @Override
    protected boolean isAddable( final RelatedContent relatedToAdd )
    {
        final boolean contentIsAllreadyVisited;
        if ( relatedToAdd instanceof RelatedParentContent )
        {
            contentIsAllreadyVisited = visitedParentRelatedContent.contains( relatedToAdd.getContent().getKey() );
        }
        else
        {
            contentIsAllreadyVisited = visitedChildRelatedContent.contains( relatedToAdd.getContent().getKey() );
        }

        final boolean availableCheckOK = includeOfflineContent() || isAvailable( relatedToAdd );

        return availableCheckOK && !contentIsAllreadyVisited;
    }

    private Collection<RelatedParentContent> doFindRelatedParents( List<ContentKey> contentKeys )
    {
        if ( contentKeys.size() == 0 )
        {
            return new ArrayList<RelatedParentContent>();
        }

        return contentDao.findRelatedParentByKeys( contentKeys, includeOnlyMainVersions );
    }


    public void setMaxParentLevel( Integer value )
    {
        this.maxParentLevel = value;
    }

    public void setMaxParentChildrenLevel( Integer value )
    {
        this.maxParentChildrenLevel = value;
    }

    public void setIncludeOnlyMainVersions( boolean includeOnlyMainVersions )
    {
        this.includeOnlyMainVersions = includeOnlyMainVersions;
    }
}
