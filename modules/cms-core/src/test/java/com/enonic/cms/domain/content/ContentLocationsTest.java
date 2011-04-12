/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.Iterator;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;
import org.junit.Test;

import com.enonic.cms.domain.SiteKey;

import static org.junit.Assert.*;

/**
 * Dec 16, 2009
 */
public class ContentLocationsTest
{
    private static int nextSectionContentKey = 0;

    @Test
    public void content_directly_placed_on_menuitem_and_have_explisit_home_set_gets_home_resolved_to_the_directly_placed_menuitem()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity directlyPlaced = createMenuItem( "1", "About", site );
        MenuItemEntity explicitHome = createMenuItem( "2", "Explisit home", site );

        ContentEntity content = new ContentEntity();
        content.addDirectMenuItemPlacement( directlyPlaced );
        content.addContentHome( createContentHome( site, explicitHome ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( directlyPlaced, resolvedLocation.getMenuItem() );
    }

    @Test
    public void content_directly_placed_on_menuitem_and_added_to_section_gets_home_resolved_to_the_directly_placed_menuitem()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity directlyPlaced = createMenuItem( "2", "About", site );
        MenuItemEntity section = createMenuItem( "3", "Section", directlyPlaced, site );

        ContentEntity content = new ContentEntity();
        content.addDirectMenuItemPlacement( directlyPlaced );
        content.addSectionContent( createApprovedSectionContent( section ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( directlyPlaced, resolvedLocation.getMenuItem() );
    }

    @Test
    public void content_directly_placed_on_menuitem_and_added_to_more_than_one_section_and_have_explisit_home_set_gets_home_resolved_to_the_directly_placed_menuitem()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity news = createMenuItem( "2", "News", site );
        MenuItemEntity news_national = createMenuItem( "3", "National", news, site );
        MenuItemEntity news_national_ObamaInOslo = createMenuItem( "4", "Obama in Oslo", news, site );

        ContentEntity content = new ContentEntity();
        content.addContentHome( createContentHome( site, news_national ) );
        content.addSectionContent( createApprovedSectionContent( news ) );
        content.addSectionContent( createApprovedSectionContent( news_national ) );
        content.addDirectMenuItemPlacement( news_national_ObamaInOslo );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( news_national_ObamaInOslo, resolvedLocation.getMenuItem() );
    }

    @Test
    public void content_having_explisit_home_set_and_added_to_section_gets_home_resolved_to_the_explisit_home()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity latestNews = createMenuItem( "2", "Latest news", site );
        MenuItemEntity news = createMenuItem( "3", "News", site );

        ContentEntity content = new ContentEntity();
        content.addContentHome( createContentHome( site, news ) );
        content.addSectionContent( createApprovedSectionContent( latestNews ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( news, resolvedLocation.getMenuItem() );
    }

    @Test
    public void xxx_new()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity news = createMenuItem( "2", "News", site );

        ContentEntity content = new ContentEntity();
        content.addContentHome( createContentHome( site, news ) );
        content.addSectionContent( createApprovedSectionContent( news ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        assertEquals( 1, contentLocations.numberOfLocations() );

        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( news, resolvedLocation.getMenuItem() );
        assertEquals( ContentLocationType.SECTION_AND_SECTION_HOME, resolvedLocation.getType() );
    }

    @Test
    public void xxx_new2()
    {
        SiteEntity site1 = createSite( "1", "Site1" );
        SiteEntity site2 = createSite( "2", "Site2" );
        MenuItemEntity site1_home = createMenuItem( "3", "Home", site1 );
        MenuItemEntity site1_home_news = createMenuItem( "4", "HomeNews", site1_home, site1 );
        MenuItemEntity site1_home_news_health = createMenuItem( "5", "HomeNewsHealth", site1_home_news, site1 );
        MenuItemEntity site2_home = createMenuItem( "6", "Home", site2 );

        ContentEntity content = new ContentEntity();
        content.addSectionContent( createApprovedSectionContent( site1_home ) );
        content.addSectionContent( createApprovedSectionContent( site1_home_news ) );
        content.addSectionContent( createApprovedSectionContent( site1_home_news_health ) );
        content.addSectionContent( createApprovedSectionContent( site2_home ) );
        content.addContentHome( createContentHome( site1, site1_home ) );
        content.addContentHome( createContentHome( site2, site2_home ) );

        ContentLocations contentLocations = getContentLocationsForAllSites( content, true );
        assertEquals( 4, contentLocations.numberOfLocations() );

        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site1.getKey() );
        assertEquals( site1_home, resolvedLocation.getMenuItem() );
        assertEquals( ContentLocationType.SECTION_AND_SECTION_HOME, resolvedLocation.getType() );

        Iterator<ContentLocation> iterator = contentLocations.getLocationsBySite( site1.getKey() ).iterator();
        assertEquals( site1_home, iterator.next().getMenuItem() );
        assertEquals( site1_home_news, iterator.next().getMenuItem() );
        assertEquals( site1_home_news_health, iterator.next().getMenuItem() );
    }

    @Test
    public void xxx_new3()
    {
        SiteEntity site1 = createSite( "1", "Site1" );
        MenuItemEntity home_with_small_m = createMenuItem( "2", "home", site1 );
        MenuItemEntity home_with_big_m = createMenuItem( "3", "Home", site1 );
        MenuItemEntity home_with_big_m_news = createMenuItem( "4", "Home News", home_with_big_m, site1 );

        ContentEntity content = new ContentEntity();
        content.addSectionContent( createApprovedSectionContent( home_with_small_m ) );
        content.addSectionContent( createApprovedSectionContent( home_with_big_m ) );
        content.addSectionContent( createApprovedSectionContent( home_with_big_m_news ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site1.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site1.getKey() );
        assertEquals( home_with_big_m, resolvedLocation.getMenuItem() );

        Iterator<ContentLocation> iterator = contentLocations.getLocationsBySite( site1.getKey() ).iterator();
        assertEquals( home_with_big_m, iterator.next().getMenuItem() );
        assertEquals( home_with_small_m, iterator.next().getMenuItem() );
        assertEquals( home_with_big_m_news, iterator.next().getMenuItem() );
    }


    @Test
    public void content_added_to_more_than_one_section_gets_home_resolved_to_section_the_nearest_root()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity news = createMenuItem( "2", "News", site );
        MenuItemEntity news_national = createMenuItem( "3", "National", news, site );

        ContentEntity content = new ContentEntity();
        content.addSectionContent( createApprovedSectionContent( news ) );
        content.addSectionContent( createApprovedSectionContent( news_national ) );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( news, resolvedLocation.getMenuItem() );
    }

    @Test
    public void content_added_to_xxxx()
    {
        SiteEntity site = createSite( "1", "MySite" );
        MenuItemEntity news = createMenuItem( "2", "News", site );

        ContentEntity content = new ContentEntity();
        content.addContentHome( createContentHome( site, news ) );
        content.addDirectMenuItemPlacement( news );

        ContentLocations contentLocations = getContentLocationsForSite( content, site.getKey(), true );
        ContentLocation resolvedLocation = contentLocations.getHomeLocation( site.getKey() );
        assertEquals( news, resolvedLocation.getMenuItem() );
    }


    private SiteEntity createSite( String key, String name )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( Integer.valueOf( key ) );
        site.setName( name );
        return site;
    }

    private MenuItemEntity createMenuItem( String key, String name, SiteEntity site )
    {
        return createMenuItem( key, name, null, site );
    }

    private MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent, SiteEntity site )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( Integer.valueOf( key ) );
        menuItem.setName( name );
        menuItem.setSite( site );
        menuItem.setParent( parent );
        return menuItem;
    }

    private ContentHomeEntity createContentHome( SiteEntity site, MenuItemEntity menuItem )
    {
        ContentHomeEntity contentHome = new ContentHomeEntity();
        contentHome.setSite( site );
        contentHome.setMenuItem( menuItem );
        return contentHome;
    }

    private SectionContentEntity createApprovedSectionContent( MenuItemEntity menuItem )
    {
        return createApprovedSectionContent( "1", menuItem );
    }

    private SectionContentEntity createApprovedSectionContent( String order, MenuItemEntity menuItem )
    {
        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( nextSectionContentKey++ ) );
        sectionContent.setMenuItem( menuItem );
        sectionContent.setOrder( Integer.valueOf( order ) );
        sectionContent.setApproved( true );
        return sectionContent;
    }

    private ContentLocations getContentLocationsForSite( ContentEntity content, SiteKey siteKey, boolean includeInactiveLocations )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setSiteKey( siteKey );
        contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
        return contentLocations;
    }

    private ContentLocations getContentLocationsForAllSites( ContentEntity content, boolean includeInactiveLocations )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
        return contentLocations;
    }
}
