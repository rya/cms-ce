/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import java.util.HashSet;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.portal.ContentPath;
import com.enonic.cms.portal.PageRequestContext;
import com.enonic.cms.portal.PageRequestContextResolver;
import com.enonic.cms.portal.PageRequestType;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

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
        MenuItemEntity menuItem = new MenuItemEntity();
        addSectionContentToMenuItem( menuItem, sectionContent );

        injectContentIntoContentDao( contentKey, "contentName" );
        injectMenuItemToSiteEntityResolver( "/", menuItem );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( resolvedContentPath );
        assertEquals( "123", resolvedContentPath.getContentKey().toString() );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
        assertTrue( resolvedContext.getPageRequestType().equals( PageRequestType.CONTENT ) );
    }

    @Test
    public void testContentRequest_with_content_key()
    {
        SitePath sitePath = new SitePath( siteKey, "/test/path/contentName--123" );

        injectMenuItemToSiteEntityResolver( "/test/path", new MenuItemEntity() );
        injectContentIntoContentDao( new ContentKey( 123 ), "contentName" );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext resolvedContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        ContentPath resolvedContentPath = resolvedContext.getContentPath();
        assertNotNull( resolvedContentPath );
        assertEquals( "123", resolvedContentPath.getContentKey().toString() );
        assertNotNull( resolvedContext.getRequestedMenuItem() );
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

    private SectionContentEntity createSectionContent( ContentKey contentKey, String contentName )
    {

        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );
        content.setName( contentName );

        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( contentKey.toString() ) );
        sectionContent.setContent( content );

        return sectionContent;
    }

    private void addSectionContentToMenuItem( MenuItemEntity menuItem, SectionContentEntity sectionContent )
    {
        if ( menuItem.getSectionContents() == null )
        {
            menuItem.setSectionContent( new HashSet<SectionContentEntity>() );
        }

        menuItem.getSectionContents().add( sectionContent );
    }

    private void injectRootPageOnSite( MenuItemEntity rootMenuItem )
    {
        when( site.getFrontPage() ).thenReturn( rootMenuItem );
    }

    private void injectMenuItemToSiteEntityResolver( String localPath, MenuItemEntity menuItem )
    {
        when( site.resolveMenuItemByPath( new Path( localPath ) ) ).thenReturn( menuItem );
    }

    private void injectContentIntoContentDao( ContentKey contentKey, String contentName )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );
        content.setName( contentName );

        when( contentDao.findByKey( contentKey ) ).thenReturn( content );

    }

}