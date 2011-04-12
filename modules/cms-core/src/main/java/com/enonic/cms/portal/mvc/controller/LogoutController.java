/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.ReservedLocalPaths;

public class LogoutController
    extends AbstractSiteController
{
    /**
     * @inheritDoc
     */
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception
    {
        SitePath userServicesPath = new SitePath( sitePath.getSiteKey(), ReservedLocalPaths.PATH_USERSERVICES, sitePath.getParams() );
        userServicesPath.addParam( "_handler", "user" );
        userServicesPath.addParam( "_op", "logout" );
        return redirectAndForwardHelper.getForwardModelAndView( request, userServicesPath );
    }

}
