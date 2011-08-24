/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;
import com.enonic.cms.upgrade.log.UpgradeLogEntry;

/**
 * This class manages the upgrade.
 */
public final class UpgradeController
    implements InitializingBean, Controller
{

    private static final String AUTHENTICATED_SESSION_KEY = "authenticatedUpgrade";

    private static final String AUTHENTICATION_FAILED_KEY = "authenticationFailed";

    private String entrerpriseAdminPassword;


    /**
     * Upgrade service.
     */
    private UpgradeService upgradeService;

    /**
     * Upgrade process task.
     */
    private UpgradeProcessTask upgradeProcessTask;

    /**
     * After properties set.
     */
    public void afterPropertiesSet()
    {
        this.upgradeProcessTask = new UpgradeProcessTask( this.upgradeService );
    }

    /**
     * Set the upgrade service.
     */
    @Autowired
    public void setUpgradeService( UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    /**
     * Handle the request.
     */
    protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        boolean authenticated = doAuthenticate( req );

        final boolean doUpgradeStep = req.getParameter( "upgradeStep" ) != null;
        final boolean doUpgradeAll = req.getParameter( "upgradeAll" ) != null;

        if ( doUpgradeStep && authenticated )
        {
            this.upgradeProcessTask.startUpgrade( false );
            redirectToSelf( req, res );
            return null;
        }
        else if ( doUpgradeAll && authenticated )
        {
            this.upgradeProcessTask.startUpgrade( true );
            redirectToSelf( req, res );
            return null;
        }

        HashMap<String, Object> model = new HashMap<String, Object>();

        if ( !authenticated && ( doUpgradeAll || doUpgradeStep ) )
        {
            model.put( AUTHENTICATION_FAILED_KEY, true );
        }
        else
        {
            model.put( AUTHENTICATION_FAILED_KEY, false );
        }

        model.put( "needsOldUpgrade", this.upgradeService.needsOldUpgradeSystem() );
        model.put( "requiredVersion", VerticalProperties.REQUIRED_40X_VERSION );
        model.put( "upgradeNeeded", this.upgradeService.needsUpgrade() );
        model.put( "upgradeInProgress", this.upgradeProcessTask.isInProgress() );
        model.put( "upgradeError", this.upgradeProcessTask.getError() );
        model.put( "upgradeLog", getFormattedLog( this.upgradeProcessTask.getLog() ) );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );
        model.put( "baseUrl", createBaseUrl( req ) );
        model.put( "authenticated", authenticated );

        if ( !this.upgradeProcessTask.isInProgress() && !this.upgradeService.needsUpgrade() )
        {
            this.upgradeProcessTask.getLog().clear();
        }

        return new ModelAndView( "upgradePage", model );
    }

    private boolean isAuthenticatedSession( HttpServletRequest req )
    {
        return req.getSession().getAttribute( AUTHENTICATED_SESSION_KEY ) != null;

    }

    private boolean doAuthenticate( HttpServletRequest req )
        throws Exception
    {
        if ( isAuthenticatedSession( req ) )
        {
            return true;
        }

        String adminPassword = req.getParameter( "adminPassword" );

        if ( StringUtils.equals( adminPassword, entrerpriseAdminPassword ) )
        {
            req.getSession().setAttribute( AUTHENTICATED_SESSION_KEY, "true" );
            return true;
        }

        return false;
    }

    /**
     * Return the formatted log.
     */
    private List<String> getFormattedLog( UpgradeLog log )
    {
        ArrayList<String> list = new ArrayList<String>();
        for ( UpgradeLogEntry entry : log.getEntries() )
        {
            list.add( getFormattedLogEntry( entry ) );
        }
        return list;
    }

    /**
     * Return the formatted log entry.
     */
    private String getFormattedLogEntry( UpgradeLogEntry entry )
    {
        StringBuffer str = new StringBuffer();
        str.append( "<div class='logentry'>" );

        str.append( "<div class='" ).append( "level-" ).append( entry.getLevel().name().toLowerCase() ).append( "'>" );
        str.append( entry.getMessage() );
        str.append( "</div>" );

        if ( entry.getCause() != null )
        {
            str.append( getFormattedCause( entry.getCause() ) );
        }

        str.append( "</div>" );
        return str.toString();
    }

    /**
     * Return the formatted cause.
     */
    private String getFormattedCause( Throwable cause )
    {
        StringBuffer str = new StringBuffer();
        str.append( "<div class='stacktrace'>" );

        for ( StackTraceElement elem : cause.getStackTrace() )
        {
            str.append( "<div class='traceelem'>" );
            str.append( elem.getClassName() ).append( "." ).append( elem.getMethodName() ).append( " (line " );
            str.append( elem.getLineNumber() ).append( ")" );
            str.append( "</div>" );
        }

        str.append( "</div>" );
        return str.toString();
    }

    /**
     * Handle the request.
     */
    public final ModelAndView handleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        return doHandleRequest( req, res );
    }

    /**
     * Return the base path.
     */
    protected String createBaseUrl( HttpServletRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        return str.toString();
    }

    /**
     * Redirect to self.
     */
    protected void redirectToSelf( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        String url = req.getRequestURL().toString();
        int index = url.indexOf( "?" );

        if ( index > -1 )
        {
            url = url.substring( 0, index );
        }

        res.sendRedirect( url );
    }

    @Value("${cms.admin.password}")
    public void setEntrerpriseAdminPassword( String password )
    {
        this.entrerpriseAdminPassword = password;
    }

}
