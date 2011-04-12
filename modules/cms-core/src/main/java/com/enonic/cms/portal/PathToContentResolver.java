/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.StringUtils;

import com.enonic.cms.store.dao.SectionContentDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocations;

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
