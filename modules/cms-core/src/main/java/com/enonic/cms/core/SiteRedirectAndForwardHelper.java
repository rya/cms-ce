/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.framework.spring.mvc.RedirectAndForwardHelper;

import com.enonic.cms.domain.SitePath;

public class SiteRedirectAndForwardHelper
    extends RedirectAndForwardHelper
{

    private SiteURLResolver siteURLResolver;


    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public ModelAndView getModelAndView( HttpServletRequest request, SitePath sitePath, boolean forward )
    {
        if ( forward )
        {
            return getForwardModelAndView( request, sitePath );
        }
        else
        {
            return getRedirectModelAndView( request, sitePath );
        }
    }

    public ModelAndView getRedirectModelAndView( HttpServletRequest request, SitePath sitePath )
    {

        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, true );
        return getRedirectModelAndView( request, path, sitePath.getParams() );
    }

    public ModelAndView getForwardModelAndView( HttpServletRequest request, SitePath sitePath )
    {

        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, false );
        return getForwardModelAndView( request, path, sitePath.getParams() );
    }

}
