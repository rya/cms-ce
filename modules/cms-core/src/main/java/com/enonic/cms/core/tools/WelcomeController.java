/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteService;

/**
 * Controller for displaying the welcome page, the root page for an installation, listing all sites, plugins, etcs, and linking to DAV,
 * Admin pages, and other information pages.
 */
@Controller
@RequestMapping(value = "/")
public final class WelcomeController
{
    private SiteService siteService;

    @Inject
    public void setSiteService( SiteService siteService )
    {
        this.siteService = siteService;
    }

    private Map<String, Integer> createSiteMap()
        throws Exception
    {
        HashMap<String, Integer> siteMap = new HashMap<String, Integer>();
        List<SiteEntity> sites =  siteService.findAll();

        for ( SiteEntity site : sites )
        {
            siteMap.put( site.getName(), site.getKey().integerValue() );
        }

        return siteMap;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handle( HttpServletRequest req )
        throws Exception
    {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "versionTitle", Version.getTitle() );
        model.put( "versionTitleVersion", Version.getTitleAndVersion() );
        model.put( "versionCopyright", Version.getCopyright() );
        model.put( "baseUrl", createBaseUrl( req ) );
        model.put( "sites", createSiteMap() );
        return new ModelAndView( "welcomePage", model );
    }

    private String createBaseUrl( HttpServletRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        if ( str.charAt( str.length() - 1 ) != '/' ) {
            str.append( "/" );
        }

        return str.toString();
    }
}
