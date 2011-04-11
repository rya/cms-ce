/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.SiteRedirectAndForwardHelper;
import com.enonic.cms.core.SiteURLResolver;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class SiteRedirectAndForwardHelperTest
    extends TestCase
{

    private MockHttpServletRequest httpServletRequest;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private SiteURLResolver siteURLResolver;

    private MockSitePropertiesService sitePropertiesService;


    protected void setUp()
        throws Exception
    {
        super.setUp();

        httpServletRequest = new MockHttpServletRequest();

        sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( new SiteKey( 0 ), SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );

        siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        siteRedirectAndForwardHelper = new SiteRedirectAndForwardHelper();
        siteRedirectAndForwardHelper.setSiteURLResolver( siteURLResolver );
    }

    public void testGetForwardModelAndViewWithNoSitePathPrefix()
    {

        siteURLResolver.setSitePathPrefix( "" );

        SitePath sitePath = new SitePath( new SiteKey( 0 ), new Path( "/Frontpage" ) );
        ModelAndView modelAndView = siteRedirectAndForwardHelper.getForwardModelAndView( httpServletRequest, sitePath );

        assertEquals( "customforward", modelAndView.getViewName() );
        Map model = modelAndView.getModel();
        assertEquals( "/0/Frontpage", model.get( "path" ) );
    }

    public void testGetForwardModelAndViewWithDefualtSitePathPrefix()
    {

        siteURLResolver.setSitePathPrefix( SiteURLResolver.DEFAULT_SITEPATH_PREFIX );

        SitePath sitePath = new SitePath( new SiteKey( 0 ), new Path( "/Frontpage" ) );
        ModelAndView modelAndView = siteRedirectAndForwardHelper.getForwardModelAndView( httpServletRequest, sitePath );

        assertEquals( "customforward", modelAndView.getViewName() );
        Map model = modelAndView.getModel();
        assertEquals( SiteURLResolver.DEFAULT_SITEPATH_PREFIX + "/0/Frontpage", model.get( "path" ) );
    }

}
