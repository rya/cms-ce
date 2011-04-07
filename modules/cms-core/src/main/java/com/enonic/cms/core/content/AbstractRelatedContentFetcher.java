/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.resultset.RelatedChildContent;
import com.enonic.cms.domain.content.resultset.RelatedContent;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.domain.content.resultset.RelatedParentContent;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Jun 11, 2009
 */
public abstract class AbstractRelatedContentFetcher
{
    protected ContentDao contentDao;

    protected Integer maxChildrenLevel;

    protected RelatedContentResultSetImpl relatedContentResultSet;

    private boolean includeOfflineContent;

    private ContentAccessResolver contentAccessResolver;

    private UserEntity runningUser;

    private Date availableCheckDate;

    private Map<ContentVersionKey, RelatedContent> relatedContentByVersionKey = new HashMap<ContentVersionKey, RelatedContent>();

    private Map<ContentKey, RelatedParentContent> relatedParentContentByContentKey = new HashMap<ContentKey, RelatedParentContent>();

    protected Set<ContentKey> visitedParentRelatedContent = new HashSet<ContentKey>();

    protected Set<ContentKey> visitedChildRelatedContent = new HashSet<ContentKey>();

    protected AbstractRelatedContentFetcher( ContentDao contentDao, ContentAccessResolver contentAccessResolver )
    {
        this.contentDao = contentDao;
        this.contentAccessResolver = contentAccessResolver;
    }

    public void setMaxChildrenLevel( Integer value )
    {
        maxChildrenLevel = value;
    }

    public void setRunningUser( UserEntity value )
    {
        runningUser = value;
    }

    public void setAvailableCheckDate( Date value )
    {
        availableCheckDate = value;
    }

    public void setIncludeOfflineContent( boolean value )
    {
        includeOfflineContent = value;
    }

    public boolean includeOfflineContent()
    {
        return includeOfflineContent;
    }

    protected Collection<RelatedChildContent> doFindRelatedChildren( Collection<ContentVersionEntity> versions )
    {
//        new code:
//        if ( versions.size() == 0 )
//        {
//            return new ArrayList<RelatedChildContent>();
//        }
//
//        List<RelatedChildContent> relatedChildContents = new ArrayList<RelatedChildContent>();
//        for ( ContentVersionEntity version : versions )com.enonic.vertical.userservices.CustomContentHandlerController_operation_ModifyTest
//        {
//            for ( ContentKey relatedChildKey : version.getContentData().resolveRelatedContentKeys() )
//            {
//                ContentEntity relatedChild = contentDao.findByKey( relatedChildKey );
//                if ( !relatedChild.isDeleted() )
//                {
//                    relatedChildContents.add( new RelatedChildContent( version.getKey(), relatedChild ) );
//                }
//            }
//        }
//        return relatedChildContents;

        if ( versions.size() == 0 )
        {
            return new ArrayList<RelatedChildContent>();
        }

        final List<ContentVersionKey> versionKeys = ContentVersionKey.createList( versions );
        return contentDao.findRelatedChildrenByKeys( versionKeys );
    }

    protected List<RelatedChildContent> doAddAndFetchChildren( final Collection<RelatedChildContent> children, final int level )
    {
        final int nextLevel = level - 1;
        final boolean atLastLevel = nextLevel == 0;

        final List<RelatedChildContent> addedRelatedContent = new ArrayList<RelatedChildContent>();
        final List<ContentEntity> addedContent = new ArrayList<ContentEntity>();

        for ( RelatedChildContent relatedToAdd : children )
        {
            if ( isAddable( relatedToAdd ) )
            {
                addedRelatedContent.add( relatedToAdd );
                addedContent.add( relatedToAdd.getContent() );
                relatedContentResultSet.add( relatedToAdd );

                registerForFastAccess( relatedToAdd );

                // connect the related content to the other related content it belong to
                doConnectRelatedContent( relatedToAdd );
            }

            visitedChildRelatedContent.add( relatedToAdd.getContent().getKey() );
        }

        // fetch more children...
        if ( !atLastLevel )
        {
            final Collection<RelatedChildContent> nextLevelChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( addedContent ) );
            if ( nextLevelChildren.size() > 0 )
            {
                doAddAndFetchChildren( nextLevelChildren, nextLevel );
            }
        }

        return addedRelatedContent;
    }

    protected abstract boolean isAddable( RelatedContent relatedToAdd );

    protected abstract boolean isAddableToRootRelated( RelatedContent relatedToAdd );

    protected boolean isAvailable( final RelatedContent relatedContent )
    {
        final ContentEntity content = relatedContent.getContent();
        boolean statusCheckOK = includeOfflineContent || content.isOnline( availableCheckDate );
        boolean accessCheckOK = runningUser == null || contentAccessResolver.hasReadContentAccess( runningUser, content );
        return statusCheckOK && accessCheckOK;
    }

    protected void registerForFastAccess( RelatedChildContent relatedChildContent )
    {
        relatedContentByVersionKey.put( relatedChildContent.getContent().getMainVersion().getKey(), relatedChildContent );
    }

    protected void registerForFastAccess( RelatedParentContent relatedParentContent )
    {
        relatedContentByVersionKey.put( relatedParentContent.getContent().getMainVersion().getKey(), relatedParentContent );
        relatedParentContentByContentKey.put( relatedParentContent.getContent().getKey(), relatedParentContent );
    }

    protected void doConnectRelatedContent( RelatedChildContent relatedContent )
    {
        RelatedContent parent = relatedContentByVersionKey.get( relatedContent.getParentVersionKey() );
        if ( parent != null )
        {
            parent.addRelatedChild( relatedContent );
        }
    }

    protected void doConnectRelatedContent( RelatedParentContent relatedContent )
    {
        RelatedParentContent child = relatedParentContentByContentKey.get( relatedContent.getChildContentKey() );
        if ( child != null )
        {
            child.addRelatedParent( relatedContent );
        }
    }

    protected List<ContentVersionEntity> gatherMainVersionsFromContent( final Collection<ContentEntity> contents )
    {
        final List<ContentVersionEntity> keys = new ArrayList<ContentVersionEntity>( contents.size() );
        for ( ContentEntity content : contents )
        {
            keys.add( content.getMainVersion() );
        }
        return keys;
    }

    protected List<ContentKey> gatherContentKeysFromContent( final Collection<ContentEntity> contents )
    {
        final List<ContentKey> keys = new ArrayList<ContentKey>( contents.size() );
        for ( ContentEntity content : contents )
        {
            keys.add( content.getKey() );
        }
        return keys;
    }
}
