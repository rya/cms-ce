/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import org.springframework.util.Assert;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

/**
 * Oct 28, 2009
 */
public class ContentLocations
{
    private final static ContentLocationComparatorOnPath CONTENT_LOCATION_COMPARATOR_ON_PATH = new ContentLocationComparatorOnPath();

    private ContentEntity content;

    private Map<MenuItemKey, ContentLocation> allLocations = new LinkedHashMap<MenuItemKey, ContentLocation>();

    private Map<SiteKey, LocationsInSite> locationsBySite = new LinkedHashMap<SiteKey, LocationsInSite>();

    private Set<SiteEntity> sites = new LinkedHashSet<SiteEntity>();

    private Set<MenuItemEntity> menuItems = new LinkedHashSet<MenuItemEntity>();

    public ContentLocations( ContentEntity content )
    {
        Assert.notNull( content );
        this.content = content;
    }

    public boolean hasLocations()
    {
        return allLocations.size() > 0;
    }

    public int numberOfLocations()
    {
        return allLocations.size();
    }

    public void addDirectMenuItemLocation( final MenuItemEntity menuItem )
    {
        Assert.notNull( menuItem );

        final SiteKey siteKey = menuItem.getSite().getKey();
        final ContentLocation location = ContentLocation.createMenuItemLocation( content, menuItem );

        allLocations.put( location.getMenuItemKey(), location );

        LocationsInSite locationsInSite = getLocationsInSite( siteKey );
        locationsInSite.addDirectMenuItemLocation( location );

        sites.add( location.getSite() );
        menuItems.add( location.getMenuItem() );
    }

    public void addSectionHomeLocation( final ContentHomeEntity contentHome )
    {
        Assert.notNull( contentHome );
        Assert.isTrue( content.equals( contentHome.getContent() ) );

        ContentLocation existingContentLocation = allLocations.get( contentHome.getMenuItem().getMenuItemKey() );
        if ( existingContentLocation != null && existingContentLocation.getType() == ContentLocationType.SECTION )
        {
            doChangeExistingSectionLocationToSectionAndHomeLocation( contentHome, existingContentLocation );
        }
        else if ( existingContentLocation != null )
        {
            /*
            In the case of an existing content location, the content home is only interesting if the existing location is of type SECTION,
            otherwise this is "old data" left in tables after the menu item have been of another type. Since our code to not clean up
            properly in the tables when chaning menu item type, we have to handle this without throwing an exception - we just ignore
            the content home.
             */
        }
        else
        {
            doAddSectionHomeLocation( contentHome );
        }
    }

    private void doChangeExistingSectionLocationToSectionAndHomeLocation( final ContentHomeEntity contentHome,
                                                                          final ContentLocation existingContentLocation )
    {
        final ContentLocation location =
            ContentLocation.createSectionAndSectionHomeLocation( content, contentHome.getMenuItem(), existingContentLocation.isApproved() );
        allLocations.put( location.getMenuItemKey(), location );

        final SiteKey siteKey = contentHome.getMenuItem().getSite().getKey();
        final LocationsInSite locationsInSite = locationsBySite.get( siteKey );
        locationsInSite.doChangeExistingSectionLocationToGiven( location );
    }

    private void doAddSectionHomeLocation( final ContentHomeEntity contentHome )
    {
        final ContentLocation location = ContentLocation.createSectionHomeLocation( content, contentHome.getMenuItem() );

        allLocations.put( location.getMenuItemKey(), location );

        final SiteKey siteKey = contentHome.getMenuItem().getSite().getKey();
        final LocationsInSite locationsInSite = getLocationsInSite( siteKey );
        locationsInSite.setSectionHomeLocation( location );

        sites.add( location.getSite() );
        menuItems.add( location.getMenuItem() );
    }

    public void addSectionMenuItemLocation( final SectionContentEntity sectionContent )
    {
        Assert.notNull( sectionContent );
        Assert.isTrue( content.equals( sectionContent.getContent() ) );

        final SiteKey siteKey = sectionContent.getMenuItem().getSite().getKey();
        final ContentLocation location =
            ContentLocation.createSectionLocation( content, sectionContent.getMenuItem(), sectionContent.isApproved() );
        allLocations.put( location.getMenuItemKey(), location );

        LocationsInSite locationsInSite = getLocationsInSite( siteKey );
        locationsInSite.addSectionMenuItemLocation( location );

        sites.add( location.getSite() );
        menuItems.add( location.getMenuItem() );
    }

    void resolveHomes()
    {
        for ( LocationsInSite locationsInSite : locationsBySite.values() )
        {
            locationsInSite.resolveHomeLocation();
        }
    }

    private LocationsInSite getLocationsInSite( final SiteKey siteKey )
    {
        LocationsInSite locationsInSite = locationsBySite.get( siteKey );
        if ( locationsInSite == null )
        {
            locationsInSite = new LocationsInSite( siteKey );
            locationsBySite.put( siteKey, locationsInSite );
        }
        return locationsInSite;
    }

    public Iterable<SiteEntity> getSites()
    {
        return sites;
    }

    public Iterable<MenuItemEntity> getMenuItems()
    {
        return menuItems;
    }

    public Iterable<ContentLocation> getAllLocations()
    {
        return allLocations.values();
    }

    public Iterable<ContentLocation> getLocationsBySite( SiteKey siteKey )
    {
        LocationsInSite locations = locationsBySite.get( siteKey );

        if ( locations == null )
        {
            return new TreeSet<ContentLocation>( new ContentLocationComparatorOnPath() );
        }

        return locations.getAllLocations();
    }

    public ContentLocation getHomeLocation( final SiteKey siteKey )
    {
        final LocationsInSite locationsInSite = locationsBySite.get( siteKey );
        if ( locationsInSite == null )
        {
            return null;
        }

        return locationsInSite.getHomeLocation();
    }

    public boolean isHomeLocation( ContentLocation location )
    {
        final ContentLocation homeLocation = getHomeLocation( location.getSiteKey() );
        return location.equals( homeLocation );
    }

    private class LocationsInSite
    {
        private SiteKey siteKey;

        private SortedSet<ContentLocation> allLocations = new TreeSet<ContentLocation>( CONTENT_LOCATION_COMPARATOR_ON_PATH );

        private SortedSet<ContentLocation> directMenuItemPlacementLocations =
            new TreeSet<ContentLocation>( CONTENT_LOCATION_COMPARATOR_ON_PATH );

        private ContentLocation sectionHomeLocation = null;

        private SortedSet<ContentLocation> sectionLocations = new TreeSet<ContentLocation>( CONTENT_LOCATION_COMPARATOR_ON_PATH );

        private ContentLocation homeLocation = null;

        private LocationsInSite( SiteKey siteKey )
        {
            this.siteKey = siteKey;
        }

        public SiteKey getSiteKey()
        {
            return siteKey;
        }

        public Iterable<ContentLocation> getAllLocations()
        {
            return allLocations;
        }

        void addDirectMenuItemLocation( ContentLocation location )
        {
            allLocations.add( location );
            directMenuItemPlacementLocations.add( location );
        }

        void addSectionMenuItemLocation( ContentLocation location )
        {
            allLocations.add( location );
            sectionLocations.add( location );
        }

        void setSectionHomeLocation( ContentLocation location )
        {
            Assert.notNull( location );
            allLocations.add( location );
            sectionHomeLocation = location;
        }

        boolean hasSectionHomeLocation()
        {
            return sectionHomeLocation != null;
        }

        public ContentLocation getSectionHomeLocation()
        {
            return sectionHomeLocation;
        }

        boolean hasDirectMenuItemLocation()
        {
            return !directMenuItemPlacementLocations.isEmpty();
        }

        boolean hasSectionLocations()
        {
            return !sectionLocations.isEmpty();
        }

        public void resolveHomeLocation()
        {
            if ( hasDirectMenuItemLocation() )
            {
                homeLocation = directMenuItemPlacementLocations.first();
            }
            else if ( hasSectionHomeLocation() )
            {
                homeLocation = sectionHomeLocation;
            }
            else if ( hasSectionLocations() )
            {
                homeLocation = sectionLocations.first();
            }
            else
            {
                homeLocation = null;
            }
        }

        public ContentLocation getHomeLocation()
        {
            return homeLocation;
        }

        private void doChangeExistingSectionLocationToGiven( ContentLocation location )
        {
            if ( !sectionLocations.contains( location ) )
            {
                throw new IllegalStateException(
                    "Expected to find an existing section location for content: " + location.getContent().getKey() );
            }

            allLocations.remove( location );
            allLocations.add( location );
            if ( location.getType() == ContentLocationType.SECTION || location.getType() == ContentLocationType.SECTION_AND_SECTION_HOME )
            {
                sectionLocations.remove( location );
                sectionLocations.add( location );
            }

            if ( location.getType() == ContentLocationType.SECTION_AND_SECTION_HOME )
            {
                sectionHomeLocation = location;
            }
        }
    }
}
