/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;
import org.junit.Test;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;

import static org.junit.Assert.*;

/**
 * Jan 8, 2010
 */
public class ContentLocationComparatorOnPathTest
{
    private static int nextSectionContentKey = 0;

    @Test
    public void a_sorts_before_b()
    {
        SiteEntity site1 = createSite( "1", "Site 1" );
        MenuItemEntity a = createMenuItem( "2", "A", site1 );
        MenuItemEntity b = createMenuItem( "3", "B", site1 );

        ContentEntity content = new ContentEntity();
        SortedSet set = new TreeSet( new ContentLocationComparatorOnPath() );
        set.add( ContentLocation.createSectionLocation( content, b, true ) );
        set.add( ContentLocation.createSectionLocation( content, a, true ) );

        Iterator<ContentLocation> it = set.iterator();
        assertEquals( a, it.next().getMenuItem() );
        assertEquals( b, it.next().getMenuItem() );
    }

    @Test
    public void a_sorts_before_b2()
    {
        SiteEntity site1 = createSite( "1", "Site 1" );
        MenuItemEntity Aaaaa = createMenuItem( "2", "Aaaaa", site1 );
        MenuItemEntity A = createMenuItem( "3", "A", site1 );
        MenuItemEntity A_Aa = createMenuItem( "4", "Aa", A, site1 );

        ContentEntity content = new ContentEntity();
        SortedSet set = new TreeSet( new ContentLocationComparatorOnPath() );
        set.add( ContentLocation.createSectionLocation(content, A_Aa, true) );
        set.add( ContentLocation.createSectionLocation( content, Aaaaa, true ) );

        Iterator<ContentLocation> it = set.iterator();
        assertEquals( Aaaaa, it.next().getMenuItem() );
        assertEquals( A_Aa, it.next().getMenuItem() );
    }


    @Test
    public void testxx()
    {
        SiteEntity site1 = createSite( "1", "Site 1" );
        MenuItemEntity news = createMenuItem( "2", "News", site1 );
        MenuItemEntity news_politics = createMenuItem( "3", "Politics", news, site1 );

        ContentEntity content = new ContentEntity();
        SortedSet set = new TreeSet( new ContentLocationComparatorOnPath() );
        set.add( ContentLocation.createSectionLocation( content, news_politics, true ) );
        set.add( ContentLocation.createSectionLocation( content, news, true ) );

        Iterator<ContentLocation> it = set.iterator();
        assertEquals( news, it.next().getMenuItem() );
        assertEquals( news_politics, it.next().getMenuItem() );
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
