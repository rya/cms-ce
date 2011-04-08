/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.SqlServerDialect;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.internal.service.CmsCoreServicesSpringManagedBeansBridge;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.store.support.ConnectionFactory;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * Controller for displaying the welcome page, the root page for an installation, listing all sites, plugins, etcs, and linking to DAV,
 * Admin pages, and other information pages.
 */
@RequestMapping(value = "/")
public final class WelcomeController
    extends AbstractController
{
    private UpgradeService upgradeService;

    private ToolsAccessResolver toolsAccessResolver;

    private Dialect dialect;

    private ConnectionFactory connectionFactory;

    @Autowired
    public void setUpgradeService( final UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    @Autowired
    public void setToolsAccessResolver( final ToolsAccessResolver toolsAccessResolver )
    {
        this.toolsAccessResolver = toolsAccessResolver;
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

    private Set<Map.Entry<String, String>> createAdditionalMessages( boolean upgradeNeeded )
    {
        final Map<String, String> msgList = new HashMap<String, String>();
        if ( upgradeNeeded )
        {
            checkUseOfDataTypeWhenRunningMSSQLServer( msgList );
        }
        return msgList.entrySet();
    }

    private void checkUseOfDataTypeWhenRunningMSSQLServer( final Map<String, String> msgList )
    {
        try
        {
            if ( dialect instanceof SqlServerDialect && !currentDatabaseUseNvarcharDataType() )
            {
                StringBuilder builder = new StringBuilder();
                builder.append(
                    "If varchar is used instead of nvarchar you will not be able to use unicode characters throughout Enonic CMS. " );
                builder.append(
                    "Note that due to the different java versions and/or jdbc driver type versions this information might not be accurate. " );
                builder.append( "Please verify that the appropriate datatype is used. " );
                builder.append(
                    "To convert from varchar to nvarchar you may use the dbtool to do a backup/restore. Please consult the documentation." );

                msgList.put( "MSSql Server Warning", builder.toString() );
            }
        }
        catch ( SQLException ex )
        {
            msgList.put( "MSSql Server Warning!", "Could not determine use of varchar/nvarchar" );
        }
    }

    private boolean currentDatabaseUseNvarcharDataType()
        throws SQLException
    {
        Statement stmt = null;
        ResultSet result = null;
        Connection connection = null;

        try
        {
            connection = connectionFactory.getConnection( true );
            stmt = connection.createStatement();
            result = stmt.executeQuery( "select mve_sKey from tModelVersion" );
            final ResultSetMetaData metaData = result.getMetaData();
            return metaData.getColumnType( 1 ) == -9; // NVARCHAR
        }
        finally
        {
            JdbcUtils.closeResultSet( result );
            JdbcUtils.closeStatement( stmt );
            JdbcUtils.closeConnection( connection );
        }
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
        model.put( "toolsRestricted", !this.toolsAccessResolver.hasAccess( req ) );
        model.put( "toolsRestrictedError", this.toolsAccessResolver.getErrorMessage( req ) );
        model.put( "additionalMessages", createAdditionalMessages( upgradeNeeded ) );
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
