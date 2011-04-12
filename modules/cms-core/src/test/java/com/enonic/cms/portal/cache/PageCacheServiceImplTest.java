/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;

import java.util.Locale;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.portal.rendering.PageCacheKey;
import com.enonic.cms.portal.rendering.RenderedPageResult;
import com.enonic.cms.portal.rendering.RenderedWindowResult;
import com.enonic.cms.portal.rendering.WindowCacheKey;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.cache.config.CacheConfig;
import com.enonic.cms.framework.cache.standard.StandardCache;
import com.enonic.cms.framework.cache.standard.StandardCacheFacade;

import com.enonic.cms.domain.CacheObjectSettings;
import com.enonic.cms.domain.SiteKey;

import static org.junit.Assert.*;


public class PageCacheServiceImplTest
{
    private SiteKey siteKey_1 = new SiteKey( 1 );

    private SiteKey siteKey_2 = new SiteKey( 2 );

    private PageCacheServiceImpl pageCacheService_site_1;

    private PageCacheServiceImpl pageCacheService_site_2;

    private StandardCacheFacade cacheFacade;

    private CacheObjectSettings settings;

    @Before
    public void before()
    {
        int maxEntries = 100;
        int timeToLiveSeconds = 1000;

        StandardCache standardCache = new StandardCache( maxEntries );
        CacheConfig cacheConfig = new CacheConfig( maxEntries, 0, timeToLiveSeconds );

        cacheFacade = new StandardCacheFacade( "page", standardCache, cacheConfig );

        pageCacheService_site_1 = new PageCacheServiceImpl( siteKey_1 );
        pageCacheService_site_1.setEnabled( true );
        pageCacheService_site_1.setTimeToLive( timeToLiveSeconds );
        pageCacheService_site_1.setCacheFacade( cacheFacade );

        pageCacheService_site_2 = new PageCacheServiceImpl( siteKey_2 );
        pageCacheService_site_2.setEnabled( true );
        pageCacheService_site_1.setTimeToLive( timeToLiveSeconds );
        pageCacheService_site_2.setCacheFacade( cacheFacade );

        settings = new CacheObjectSettings( "default", timeToLiveSeconds );
    }

    @Test
    public void testCachePage()
    {
        pageCacheService_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePage( createPKey( "ABC", "2", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );

        assertEquals( 3, cacheFacade.getCount() );
    }

    @Test
    public void testCacheContentObject()
    {
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        assertEquals( 3, cacheFacade.getCount() );
    }

    @Test
    public void testRemoveEntriesBySite()
    {

        pageCacheService_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        pageCacheService_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCacheService_site_1.removeEntriesBySite();
        assertEquals( 2, cacheFacade.getCount() );

        pageCacheService_site_2.removeEntriesBySite();
        assertEquals( 0, cacheFacade.getCount() );

    }

    @Test
    public void testRemoveContentObjectEntriesBySite()
    {

        pageCacheService_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        pageCacheService_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCacheService_site_1.removePortletWindowEntriesBySite();
        // expect on page entry on site 1 and the two entries on site 2
        assertEquals( 3, cacheFacade.getCount() );

        pageCacheService_site_2.removePortletWindowEntriesBySite();
        assertEquals( 2, cacheFacade.getCount() );

    }

    @Test
    public void testRemovePageEntriesBySite()
    {
        pageCacheService_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        pageCacheService_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCacheService_site_1.removePageEntriesBySite();
        // expect two object entries on site 1 and the two entries on site 2
        assertEquals( 4, cacheFacade.getCount() );

        pageCacheService_site_2.removePageEntriesBySite();
        assertEquals( 3, cacheFacade.getCount() );

    }

    @Test
    public void testRemoveEntriesByMenuItem()
    {

        pageCacheService_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        pageCacheService_site_1.cachePage( createPKey( "ABC", "2", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_1.cachePortletWindow( createCOKey( "ABC", "2", "102", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        pageCacheService_site_2.cachePage( createPKey( "ABC", "11", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCacheService_site_2.cachePortletWindow( createCOKey( "ABC", "11", "101", "q", "p", "a", new Locale( "no" ) ),
                                                    new RenderedWindowResult(), settings );

        assertEquals( 7, cacheFacade.getCount() );

        pageCacheService_site_1.removeEntriesByMenuItem( new MenuItemKey( 1 ) );
        assertEquals( 4, cacheFacade.getCount() );

        pageCacheService_site_2.removeEntriesByMenuItem( new MenuItemKey( 11 ) );
        assertEquals( 2, cacheFacade.getCount() );

    }

    private PageCacheKey createPKey( String userKey, String menuItemKey, String queryString, String deviceClass, Locale resolvedLocale )
    {
        PageCacheKey key = new PageCacheKey();
        key.setQueryString( queryString );
        key.setDeviceClass( deviceClass );
        key.setLocale( resolvedLocale );
        key.setUserKey( userKey );
        key.setMenuItemKey( new MenuItemKey( menuItemKey ) );
        return key;
    }

    private WindowCacheKey createCOKey( String userKey, String menuItemKey, String contentObjectKey, String queryString,
                                        String paramsString, String deviceClass, Locale resolvedLocale )
    {
        return new WindowCacheKey( userKey, new MenuItemKey( menuItemKey ), Integer.valueOf( contentObjectKey ), queryString, paramsString,
                                   deviceClass, resolvedLocale );
    }
}
