/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import static org.junit.Assert.*;

/**
 * Sep 3, 2010
 */
public class SiteBasePathResolverTest
{
    private MockHttpServletRequest request = new MockHttpServletRequest();

    private SiteKey siteKey1 = new SiteKey( 1 );

    @Before
    public void before()
    {
        request.setRequestURI( "/site/" + siteKey1.toString() );

        ServletRequestAccessor.setRequest( request );
    }

    @Test
    public void resolvePortalSiteBasePath()
    {
        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( request, siteKey1 );
        assertTrue( siteBasePath instanceof PortalSiteBasePath );

        assertEquals( "/site/1", siteBasePath.getAsPath().toString() );
    }

    @Test
    public void resolveAdminSitePreviewBasePath()
    {
        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( request, siteKey1 );
        assertTrue( siteBasePath instanceof AdminSitePreviewBasePath );
        assertEquals( "/admin/preview/1", siteBasePath.getAsPath().toString() );
    }

    @Test
    public void resolveAdminSiteDebugBasePath()
    {
        RenderTrace.enter();

        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( request, siteKey1 );
        assertTrue( siteBasePath instanceof AdminSiteDebugBasePath );

        RenderTrace.exit();

        assertEquals( "/admin/site/1", siteBasePath.getAsPath().toString() );
    }
}
