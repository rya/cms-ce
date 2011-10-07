/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import org.mockito.Mockito;

import junit.framework.TestCase;

import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SiteContext;
import com.enonic.cms.business.SiteContextManager;
import com.enonic.cms.business.portal.cache.SiteCachesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.SiteNotFoundException;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;

public class SiteServiceImplTest
    extends TestCase
{

    private SiteServiceImpl siteService;

    private SiteContextManager siteContextManager = new SiteContextManager();

    private SiteCachesService siteCachesService;

    private MockSitePropertiesService sitePropertiesService;

    private SiteDao siteDao;

    private final SiteKey siteKey = new SiteKey( 5 );

    protected void setUp()
        throws Exception
    {
        super.setUp();

        siteCachesService = createNiceMock( SiteCachesService.class );
        sitePropertiesService = new MockSitePropertiesService();

        siteDao = Mockito.mock( SiteDao.class );

        siteService = new SiteServiceImpl();
        siteService.setSiteDao( siteDao );
        siteService.setSiteContextManager( siteContextManager );
        siteService.setSiteCachesService( siteCachesService );
        siteService.setSitePropertiesService( sitePropertiesService );

    }

    public void testGetSiteContext()
    {
        replay( siteCachesService );

        Mockito.when( siteDao.findByKey( siteKey ) ).thenReturn( createSite( siteKey, "MySite" ) );

        SiteContext siteContext = siteService.getSiteContext( siteKey );

        assertEquals( siteKey, siteContext.getSiteKey() );

        // sjekk at SiteContexten har blitt registert hos siteContextManageren
        assertSame( siteContext, siteContextManager.getSiteContext( siteKey ) );

        assertSame( siteContext, siteService.getSiteContext( siteKey ) );
    }

    public void testGetSiteContextWhenSiteNotExist()
    {
        replay( siteCachesService );

        try
        {
            siteService.getSiteContext( new SiteKey( 99999 ) );
            fail( "Expected SiteNotFoundException" );
        }
        catch ( SiteNotFoundException e )
        {
            assertTrue( e.getMessage().startsWith( "Site not found: '99999'" ) );
        }

    }

    public void testGetSiteContextWhenSiteNotExistingAnymore()
    {
        replay( siteCachesService );

        siteContextManager.registerSiteContext( new SiteContext( new SiteKey( 123 ) ) );

        try
        {
            siteService.getSiteContext( new SiteKey( 123 ) );
            fail( "Expected SiteNotFoundException" );
        }
        catch ( SiteNotFoundException e )
        {
            assertTrue( e.getMessage().startsWith( "Site not found: '123'" ) );

        }

        assertNull( siteContextManager.getSiteContext( new SiteKey( 123 ) ) );
    }

    private SiteEntity createSite( SiteKey siteKey, String name )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( siteKey.toInt() );
        site.setName( name );
        return site;
    }
}
