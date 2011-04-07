/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import junit.framework.TestCase;

import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.structure.SiteServiceImpl;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SiteContext;
import com.enonic.cms.business.SiteContextManager;
import com.enonic.cms.business.portal.cache.SiteCachesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.SiteNotFoundException;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class SiteServiceImplTest
    extends TestCase
{

    private SiteServiceImpl siteService;

    private SiteContextManager siteContextManager = new SiteContextManager();

    private PresentationService presentationService;

    //private MockControl presentationServiceMC;

    private SiteCachesService siteCachesService;

    //private MockControl siteCachesServiceMC;

    private MockSitePropertiesService sitePropertiesService;

    private final SiteKey siteKey = new SiteKey( 5 );

    protected void setUp()
        throws Exception
    {
        super.setUp();

        presentationService = createNiceMock( PresentationService.class );
        siteCachesService = createNiceMock( SiteCachesService.class );
        sitePropertiesService = new MockSitePropertiesService();

        siteService = new SiteServiceImpl();
        siteService.setSiteContextManager( siteContextManager );
        siteService.setPresentationService( presentationService );
        siteService.setSiteCachesService( siteCachesService );
        siteService.setSitePropertiesService( sitePropertiesService );

        expect( presentationService.siteExists( siteKey ) ).andReturn( true ).anyTimes();
    }

    public void testGetSiteContext()
    {

        replay( presentationService );
        replay( siteCachesService );

        SiteContext siteContext = siteService.getSiteContext( siteKey );

        assertEquals( siteKey, siteContext.getSiteKey() );

        // sjekk at SiteContexten har blitt registert hos siteContextManageren
        assertSame( siteContext, siteContextManager.getSiteContext( siteKey ) );

        assertSame( siteContext, siteService.getSiteContext( siteKey ) );
    }

    public void testGetSiteContextWhenSiteNotExist()
    {

        replay( presentationService );
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

        replay( presentationService );
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
}
