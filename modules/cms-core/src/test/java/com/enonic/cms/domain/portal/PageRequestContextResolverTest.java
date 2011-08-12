/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;
import com.enonic.cms.portal.ContentPath;
import com.enonic.cms.portal.PageRequestContext;
import com.enonic.cms.portal.PageRequestContextResolver;
import com.enonic.cms.portal.PageRequestType;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 13, 2010
 * Time: 2:27:33 PM
 */
public class PageRequestContextResolverTest
    extends TestCase
{

    SiteKey siteKey = new SiteKey( "1" );

    SiteEntity site;

    ContentDao contentDao;

    @Before
    public void setUp()
    {
        site = mock( SiteEntity.class );
        contentDao = mock( ContentDao.class );

        when( site.getKey() ).thenReturn( new SiteKey( 0 ) );
    }


    @Test
    public void testMenuItemRequest()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/menuItem" );

        SectionContentEntity sectionContent = createSectionContent( new ContentKey( 123 ), "contentName" );
        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );

        injectMenuItemToSiteEntityResolver( "/test/menuItem", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.MENUITEM ) );
    }

    @Test
    public void testContentRequest_root_without_content_key()
    {
        SitePath sitePath = new SitePath( siteKey, "/contentName" );

        SectionContentEntity sectionContent = createSectionContent( new ContentKey( 123 ), "contentName" );
        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );

        injectMenuItemToSiteEntityResolver( "/", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );

        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );
        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
        assertNull( resolvedContext.getRequestedMenuItem() );
    }

    @Test
    public void testContentRequest_root_with_content_key()
    {
        SitePath sitePath = new SitePath( siteKey, "/contentName--123" );
        final ContentKey contentKey = new ContentKey( 123 );
        ContentPath contentPath = new ContentPath( contentKey, "contentName", new Path( "/" ) );
        sitePath.setContentPath( contentPath );

        SectionContentEntity sectionContent = createSectionContent( contentKey, "contentName" );
        MenuItemEntity menuItem = createMenuItem( "menuItem", site );

        addSectionContentToMenuItem( menuItem, sectionContent );

        injectContentIntoContentDao( contentKey, "contentName" );
        injectMenuItemToSiteEntityResolver( "/", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
    }

    private MenuItemEntity createMenuItem( String menuItemName, SiteEntity site )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName( "menuItemName" );
        menuItem.setSite( site );
        return menuItem;
    }

    @Test
    public void testContentRequest_with_content_key()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName--123" );

        final ContentKey contentKey = new ContentKey( 123 );
        ContentEntity content = createContent( contentKey, "contentName" );

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setSite( site );

        content.addDirectMenuItemPlacement( menuItem );
        content.addContentHome( createContentHome( site, menuItem ) );

        injectContentIntoContentDao( content );
        injectMenuItemToSiteEntityResolver( "/test", createMenuItem( "test", site ) );
        injectMenuItemToSiteEntityResolver( "/test/path", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( "Content path should not be null", resolvedContentPath );
        assertEquals( "123", resolvedContentPath.getContentKey().toString() );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }

    @Test
    public void testContentRequest_with_content_key_not_published_to_section()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName--123" );

        final ContentKey contentKey = new ContentKey( 123 );
        MenuItemEntity menuItem = new MenuItemEntity();

        injectContentIntoContentDao( contentKey, "contentName" );
        injectMenuItemToSiteEntityResolver( "/test/path", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( "Content path should be null", resolvedContentPath );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }


    @Test
    public void testContentRequest_content_in_section()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName" );

        SectionContentEntity sectionContent = createSectionContent( new ContentKey( 123 ), "contentName" );
        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );

        injectMenuItemToSiteEntityResolver( "/test/path", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( resolvedContentPath );
        assertEquals( "123", resolvedContentPath.getContentKey().toString() );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }

    @Test
    public void testContentRequest_several_content_in_section()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName3" );

        SectionContentEntity sectionContent = createSectionContent( new ContentKey( 123 ), "contentName1" );
        SectionContentEntity sectionContent2 = createSectionContent( new ContentKey( 234 ), "contentName2" );
        SectionContentEntity sectionContent3 = createSectionContent( new ContentKey( 345 ), "contentName3" );
        SectionContentEntity sectionContent4 = createSectionContent( new ContentKey( 456 ), "contentName4" );

        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );
        addSectionContentToMenuItem( menuItem, sectionContent2 );
        addSectionContentToMenuItem( menuItem, sectionContent3 );
        addSectionContentToMenuItem( menuItem, sectionContent4 );

        injectMenuItemToSiteEntityResolver( "/test/path", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( resolvedContentPath );
        assertEquals( "345", resolvedContentPath.getContentKey().toString() );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }


    @Test
    public void testContentRequest_content_in_section_not_matching_name()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/noMatchingContentName" );

        SectionContentEntity sectionContent = createSectionContent( new ContentKey( 123 ), "contentName" );
        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );

        injectMenuItemToSiteEntityResolver( "/test/path", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
        assertNull( resolvedContext.getRequestedMenuItem() );
    }


    @Test
    public void testContentRequest_contentOnRoot()

    {
        SitePath sitePath = new SitePath( siteKey, "/123/contentName" );

        final ContentKey contentKey = new ContentKey( 123 );

        final MenuItemEntity rootMenuItem = new MenuItemEntity();
        rootMenuItem.setName( "" );
        rootMenuItem.setKey( 1 );

        injectMenuItemToSiteEntityResolver( "/", rootMenuItem );
        injectContentIntoContentDao( contentKey, "contentName" );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( resolvedContentPath );
        assertNotNull( resolvedContext.getRequestedMenuItem() );

        assertEquals( new ContentKey( "123" ), resolvedContentPath.getContentKey() );
        assertEquals( "/", resolvedContentPath.getPathToMenuItem().getPathAsString() );
    }

    @Test
    public void testContentRequest_contentOnRoot_extraElementInPath()
    {
        SitePath sitePath = new SitePath( siteKey, "/123/test/contentName" );

        final ContentKey contentKey = new ContentKey( 123 );

        final MenuItemEntity rootMenuItem = new MenuItemEntity();
        rootMenuItem.setName( "" );
        rootMenuItem.setKey( 1 );

        injectRootPageOnSite( rootMenuItem );
        injectContentIntoContentDao( contentKey, "contentName" );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
    }

    @Test
    public void testContentRequest_emptyPathYieldsNoException()

    {
        SitePath sitePath = new SitePath( siteKey, "" );

        final ContentKey contentKey = new ContentKey( 123 );

        final MenuItemEntity rootMenuItem = new MenuItemEntity();
        rootMenuItem.setName( "" );
        rootMenuItem.setKey( 1 );

        injectRootPageOnSite( rootMenuItem );
        injectContentIntoContentDao( contentKey, "contentName" );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNull( resolvedContentPath );
    }

    @Test
    public void testContentRequest_old_style_content_path()
    {
        ContentKey contentKey = new ContentKey( "123" );

        ContentEntity content = createContent( contentKey, "contentName" );

        SiteEntity site = new SiteEntity();
        site.setKey( 1 );
        site.setName( "mySite" );

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setName( "test" );
        menuItem.setSite( site );

        content.addContentHome( createContentHome( site, menuItem ) );

        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName." + contentKey.toString() + ".cms" );

        injectContentIntoContentDao( content );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( "Content path should not be null", resolvedContentPath );
        assertEquals( "123", resolvedContentPath.getContentKey().toString() );
        assertEquals( menuItem, resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }

    private SectionContentEntity createSectionContent( ContentKey contentKey, String contentName )
    {

        ContentEntity content = createContent( contentKey, contentName );

        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( contentKey.toString() ) );
        sectionContent.setContent( content );

        return sectionContent;
    }

    private ContentEntity createContent( ContentKey contentKey, String contentName )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );
        content.setName( contentName );
        return content;
    }

    private void addSectionContentToMenuItem( MenuItemEntity menuItem, SectionContentEntity sectionContent )
    {
        if ( menuItem.getSectionContents() == null )
        {
            menuItem.setSectionContent( new HashSet<SectionContentEntity>() );
        }

        menuItem.getSectionContents().add( sectionContent );
    }

    private void injectFirstMenuItem( MenuItemEntity firstMenuItem )
    {
        when( site.getFirstMenuItem() ).thenReturn( firstMenuItem );
    }

    private void injectRootPageOnSite( MenuItemEntity rootMenuItem )
    {
        when( site.getFrontPage() ).thenReturn( rootMenuItem );
    }

    private void injectMenuItemToSiteEntityResolver( String localPath, MenuItemEntity menuItem )
    {
        when( site.resolveMenuItemByPath( new Path( localPath ) ) ).thenReturn( menuItem );
    }

    private void injectContentIntoContentDao( ContentEntity content )
    {
        when( contentDao.findByKey( content.getKey() ) ).thenReturn( content );
    }

    private void injectContentIntoContentDao( ContentKey contentKey, String contentName )
    {
        ContentEntity content = createContent( contentKey, contentName );

        when( contentDao.findByKey( contentKey ) ).thenReturn( content );

    }

    private ContentHomeEntity createContentHome( SiteEntity site, MenuItemEntity menuItem )
    {
        ContentHomeEntity contentHome = new ContentHomeEntity();
        contentHome.setSite( site );
        contentHome.setMenuItem( menuItem );
        return contentHome;
    }


}