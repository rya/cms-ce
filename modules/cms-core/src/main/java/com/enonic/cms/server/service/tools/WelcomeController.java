/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.internal.service.CmsCoreServicesSpringManagedBeansBridge;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.store.support.ConnectionFactory;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * Controller for displaying the welcome page, the root page for an installation, listing all sites, plugins, etcs, and linking to DAV,
 * Admin pages, and other information pages.
 */
public final class WelcomeController
    extends AbstractController
{
    private UpgradeService upgradeService;

    private Dialect dialect;

    private ConnectionFactory connectionFactory;

    @Autowired
    public void setUpgradeService( final UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    @Autowired
    public void setDialect( final Dialect dialect )
    {
        this.dialect = dialect;
    }

    @Autowired
    public void setConnectionFactory( final ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    private Map<String, Integer> createSiteMap()
        throws Exception
    {
        HashMap<String, Integer> siteMap = new HashMap<String, Integer>();
        AdminService adminService = CmsCoreServicesSpringManagedBeansBridge.getAdminService();
        Map menuMap = adminService.getMenuMap();

        for ( Object val : menuMap.keySet() )
        {
            Integer siteKey = (Integer) val;
            String siteName = (String) menuMap.get( siteKey );
            siteMap.put( siteName, siteKey );
        }

        return siteMap;
    }

    protected ModelAndView handleRequestInternal( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        final boolean modelUpgradeNeeded = this.upgradeService.needsUpgrade();
        final boolean softwareUpgradeNeeded = this.upgradeService.needsSoftwareUpgrade();
        final boolean upgradeNeeded = modelUpgradeNeeded || softwareUpgradeNeeded;

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "versionTitle", Version.getTitle() );
        model.put( "versionTitleVersion", Version.getTitleAndVersion() );
        model.put( "versionCopyright", Version.getCopyright() );
        model.put( "baseUrl", createBaseUrl( req ) );
        if ( !upgradeNeeded )
        {
            model.put( "sites", createSiteMap() );
        }
        model.put( "upgradeNeeded", upgradeNeeded );
        model.put( "modelUpgradeNeeded", modelUpgradeNeeded );
        model.put( "softwareUpgradeNeeded", softwareUpgradeNeeded );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );
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
        if ( str.charAt( str.length() - 1 ) == '/' )
        {
            str.deleteCharAt( str.length() - 1 );
        }

        return str.toString();
    }
}
