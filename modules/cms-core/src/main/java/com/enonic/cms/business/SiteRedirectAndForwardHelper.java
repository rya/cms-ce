/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import javax.servlet.http.HttpServletRequest;
import com.enonic.cms.core.portal.mvc.view.SiteCustomForwardView;
import org.springframework.web.servlet.ModelAndView;
import com.enonic.cms.core.SitePath;
import java.util.HashMap;
import java.util.Map;

public class SiteRedirectAndForwardHelper
{
    private SiteURLResolver siteURLResolver;

    private boolean replaceSpacesWithPlus = false;

    public void setReplaceSpacesWithPlus( boolean value )
    {
        this.replaceSpacesWithPlus = value;
    }

    private ModelAndView getForwardModelAndView(String path, Map<String, String[]> params)
    {
        if ( replaceSpacesWithPlus )
        {
            path = replaceSpacesWithPlus( path );
        }

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put( "path", path );
        model.put( "requestParams", params );
        return new ModelAndView( new SiteCustomForwardView(), model );
    }

    private String replaceSpacesWithPlus( String path )
    {
        return path.replaceAll( " ", "+" );
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public ModelAndView getForwardModelAndView( HttpServletRequest request, SitePath sitePath )
    {
        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, false );
        return getForwardModelAndView(path, sitePath.getParams() );
    }
}
