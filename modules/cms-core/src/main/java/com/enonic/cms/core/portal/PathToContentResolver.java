/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.store.dao.SectionContentDao;

/**
 * Feb 23, 2010
 */
public class PathToContentResolver
{
    public static final String CONTENT_PATH_SEPARATOR = "--";

    private SectionContentDao sectionContentDao;

    public PathToContentResolver( SectionContentDao sectionContentDao )
    {
        this.sectionContentDao = sectionContentDao;
    }

    public Path resolveContentUrlLocalPath( ContentEntity content, SiteKey siteKey )
    {
        return doResolveContentUrlLocalPath( content, siteKey );
    }

    private Path doResolveContentUrlLocalPath( ContentEntity content, SiteKey siteKey )
    {
        ContentLocation resolvedHomeLocation = getContentLocation( content, siteKey );

        if ( resolvedHomeLocation == null )
        {
            return createContentPathWithNoLocation( content );
        }

        if ( resolvedHomeLocation.isOnMenuItem() )
        {
            return createContentPathFromMenuItem( resolvedHomeLocation );
        }

        if ( resolvedHomeLocation.isInSectionOrSectionHome() )
        {
            return createContentPathFromLocation( content, resolvedHomeLocation );
        }

        throw new IllegalArgumentException( "ContentLocationType not supported: " + resolvedHomeLocation.getType() );
    }

    public Path resolveContentUrlLocalPathForPermalink( final ContentEntity content, final SitePath sitePath )
    {
        Path contentUrlLocalPath = doResolveContentUrlLocalPath( content, sitePath.getSiteKey() );

        if ( sitePath.hasReferenceToWindow() )
        {
            contentUrlLocalPath = getPathWithWindowReference( sitePath, contentUrlLocalPath );
        }

        return contentUrlLocalPath;
    }

    private Path getPathWithWindowReference( final SitePath sitePath, final Path contentUrlLocalPath )
    {
        final WindowReference windowReference = sitePath.getWindowReference();
        return contentUrlLocalPath.appendPathElement( WindowReference.WINDOW_PATH_PREFIX ).appendPathElement(
            windowReference.getPortletName() );
    }

    public Path resolveContentPermalink( ContentEntity content )
    {
        return createContentPathWithNoLocation( content );
    }

    private ContentLocation getContentLocation( ContentEntity content, SiteKey siteKey )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
        contentLocationSpecification.setSiteKey( siteKey );
        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
        ContentLocation resolvedHomeLocation = contentLocations.getHomeLocation( siteKey );
        return resolvedHomeLocation;
    }

    private Path createContentPathWithNoLocation( ContentEntity content )
    {
        StringBuffer localPath = new StringBuffer();

        localPath.append( content.getKey() );
        localPath.append( "/" );
        localPath.append( StringUtils.isBlank( content.getName() ) ? "" : content.getName() );

        return new Path( localPath.toString(), true );
    }

    private Path createContentPathFromMenuItem( ContentLocation resolvedHomeLocation )
    {
        final MenuItemEntity menuItem = resolvedHomeLocation.getMenuItem();
        return new Path( menuItem.getPathAsString(), true );
    }


    private Path createContentPathFromLocation( ContentEntity content, ContentLocation resolvedHomeLocation )
    {
        StringBuffer localPath = new StringBuffer();

        final MenuItemEntity menuItem = resolvedHomeLocation.getMenuItem();

        localPath.append( menuItem.getPathAsString() );
        localPath.append( "/" );

        localPath.append( StringUtils.isBlank( content.getName() ) ? "" : content.getName() );

        if ( resolvedHomeLocation.isHomeButNotInSection() || !isUniqueInSection( content, menuItem ) )
        {
            localPath.append( CONTENT_PATH_SEPARATOR );
            localPath.append( content.getKey() );
        }

        return new Path( localPath.toString(), true );
    }


    private boolean isUniqueInSection( ContentEntity content, MenuItemEntity menuItem )
    {
        String contentName = content.getName();

        int count = sectionContentDao.getCountNamedContentsInSection( menuItem.getMenuItemKey(), contentName );

        return count == 1;
    }
}
