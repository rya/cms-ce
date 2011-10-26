/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.portal.RedirectInstruction;
import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.store.dao.MenuItemDao;

public class PageRedirectController
    extends AbstractSiteController
{
    private MenuItemDao menuItemDao;

    private PortalRenderResponseServer portalRenderResponseServer;

    protected ModelAndView handleRequestInternal( HttpServletRequest httpRequest, HttpServletResponse httpResponse, SitePath sitePath )
        throws Exception
    {
        String id = sitePath.getParam( "id" );

        // redirect to new path or forward to old page servlet
        if ( id == null )
        {
            // site/x/[...]/page shall show the front page
            SitePath indexPageSitePath = new SitePath( sitePath.getSiteKey(), Path.ROOT, sitePath.getParams() );
            return siteRedirectAndForwardHelper.getForwardModelAndView( httpRequest, indexPageSitePath );
        }

        MenuItemKey menuItemKey = new MenuItemKey( id );
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }

        SitePath newPagePath = new SitePath( sitePath.getSiteKey(), menuItem.getPath(), sitePath.getParams() );

        // Remove id-parameter since this is not valid in the redirect
        newPagePath.removeParam( "id" );

        PortalRequest request = new PortalRequest();

        RedirectInstruction redirectInstruction = new RedirectInstruction( newPagePath );
        redirectInstruction.setPermanentRedirect( true );

        PortalResponse response = PortalResponse.createRedirect( redirectInstruction );

        return portalRenderResponseServer.serveResponse( request, response, httpResponse, httpRequest );
    }

    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    public void setPortalRenderResponseServer( PortalRenderResponseServer portalRenderResponseServer )
    {
        this.portalRenderResponseServer = portalRenderResponseServer;
    }
}
