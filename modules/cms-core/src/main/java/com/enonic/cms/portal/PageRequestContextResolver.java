/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 13, 2010
 * Time: 1:38:52 PM
 */
public class PageRequestContextResolver
{
    private ContentDao contentDao;

    private static final int CONTENT_ON_ROOT_ELEMENTS = 2;

    public PageRequestContextResolver( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public PageRequestContext resolvePageRequestContext( final SiteEntity site, final SitePath sitePath )
    {
        PageRequestContext context = new PageRequestContext();

        context.setSitePath( sitePath );

        final boolean explicitContentPath = sitePath.hasPathToContent();

        if ( explicitContentPath )
        {
            setPageRequestTypeContextDataForExplicitContentPath( sitePath, site, context );
            return context;
        }

        final Path pathToMenuItem = sitePath.resolvePathToMenuItem();

        MenuItemEntity menuItem = site.resolveMenuItemByPath( pathToMenuItem );

        if ( menuItem != null )
        {
            context.setRequestedMenuItem( menuItem );
            context.setPageRequestType( PageRequestType.MENUITEM );
            return context;
        }

        // No menuitem with requested path found, this could be a content request

        menuItem = resolveParentMenuItem( site, pathToMenuItem );

        ContentPath resolvedContentPath;

        if ( menuItem != null )
        {
            String possibleContentName = pathToMenuItem.getLastPathElement();
            resolvedContentPath = resolveContentPathForContentWithHome( menuItem, possibleContentName );
        }
        else
        {
            menuItem = site.resolveMenuItemByPath( Path.ROOT );
            resolvedContentPath = resolveContentPermaLink( pathToMenuItem );
        }

        if ( resolvedContentPath != null )
        {
            context.setRequestedMenuItem( menuItem );
            context.setResolvedContentPath( resolvedContentPath );
            context.setPageRequestType( PageRequestType.CONTENT );
        }
        return context;
    }

    private ContentPath resolveContentPathForContentWithHome( MenuItemEntity menuItem, String possibleContentName )
    {
        ContentEntity matchingContent = searchForContentInSection( possibleContentName, menuItem );

        if ( matchingContent != null )
        {
            return new ContentPath( matchingContent.getKey(), matchingContent.getName(), menuItem.getPath() );
        }

        return null;
    }


    private void setPageRequestTypeContextDataForExplicitContentPath( SitePath sitePath, SiteEntity site, PageRequestContext context )
    {
        Preconditions.checkNotNull( sitePath.getContentPath() );

        context.setPageRequestType( PageRequestType.CONTENT );

        ContentPath contentPath = sitePath.getContentPath();
        ContentEntity content = contentDao.findByKey( contentPath.getContentKey() );

        if ( content == null )
        {
            return;
        }

        Path requestedMenuItemPath = contentPath.getPathToMenuItem();

        MenuItemEntity requestedMenuItem = site.resolveMenuItemByPath( requestedMenuItemPath );

        // Requested menu item path is not found, check if it was an old style path and resolve new path for future redirect
        if ( requestedMenuItem == null )
        {
            if ( contentPath.isOldStyleContentPath() )
            {
                requestedMenuItem = resolveContentHome( site, content );
            }
        }

        context.setRequestedMenuItem( requestedMenuItem );
        context.setResolvedContentPath( contentPath );
    }

    private MenuItemEntity resolveContentHome( SiteEntity site, ContentEntity content )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setSiteKey( site.getKey() );
        contentLocationSpecification.setIncludeInactiveLocationsInSection( false );

        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        ContentLocation homeLocation = contentLocations.getHomeLocation( site.getKey() );

        if ( homeLocation != null )
        {
            return homeLocation.getMenuItem();
        }

        MenuItemEntity siteFrontPage = site.getFrontPage();

        if ( siteFrontPage != null )
        {
            return siteFrontPage;
        }

        return null;
    }

    private ContentPath resolveContentPermaLink( Path pathToMenuItem )
    {
        if ( !pathMatchesPermaLinkPattern( pathToMenuItem ) )
        {
            return null;
        }

        String contentKeyPathElement = pathToMenuItem.getFirstPathElement();

        ContentEntity foundContent = contentDao.findByKey( new ContentKey( contentKeyPathElement ) );

        if ( foundContent == null )
        {
            // verifying if content is deleted is done later in process
            return null;
        }

        String contentNameFromRequest = pathToMenuItem.getLastPathElement();

        ContentPath contentPath = new ContentPath( foundContent.getKey(), contentNameFromRequest, Path.ROOT );
        contentPath.setPermaLink( true );
        return contentPath;
    }

    private boolean pathMatchesPermaLinkPattern( Path pathToMenuItem )
    {
        // Pattern for content on root: /<key>/<title>
        final boolean correctNumberOfElements = pathToMenuItem.numberOfElements() == CONTENT_ON_ROOT_ELEMENTS;

        if ( !correctNumberOfElements )
        {
            return false;
        }

        String firstElement = pathToMenuItem.getFirstPathElement();

        if ( !StringUtils.isNumeric( firstElement ) )
        {
            return false;
        }

        return true;
    }

    private MenuItemEntity resolveParentMenuItem( final SiteEntity site, final Path pathToMenuItem )
    {
        if ( pathToMenuItem.getPathElementsCount() <= 1 )
        {
            return null;
        }

        Path parentMenuItemPath = pathToMenuItem.substractLastPathElement();

        return site.resolveMenuItemByPath( parentMenuItemPath );
    }


    private ContentEntity searchForContentInSection( final String contentNameFromRequest, final MenuItemEntity menuItem )
    {
        Set<SectionContentEntity> sectionContents = menuItem.getSectionContents();

        if ( sectionContents != null )
        {
            for ( SectionContentEntity sectionContent : sectionContents )
            {
                ContentEntity currentContent = sectionContent.getContent();

                if ( currentContent.getName().equalsIgnoreCase( contentNameFromRequest ) )
                {
                    return currentContent;
                }
            }
        }

        return null;
    }

}
