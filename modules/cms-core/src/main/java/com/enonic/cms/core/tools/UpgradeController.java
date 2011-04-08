/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;
import com.enonic.cms.upgrade.log.UpgradeLogEntry;

/**
 * This class manages the upgrade.
 */
@RequestMapping(value = "/tools/upgrade")
public final class UpgradeController
    extends AbstractToolController
    implements InitializingBean
{
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
        if ( req.getParameter( "upgradeStep" ) != null )
        {
            this.upgradeProcessTask.startUpgrade( false );
            redirectToSelf( req, res );
            return null;
        }
        else if ( req.getParameter( "upgradeAll" ) != null )
        {
            this.upgradeProcessTask.startUpgrade( true );
            redirectToSelf( req, res );
            return null;
        }

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "needsOldUpgrade", this.upgradeService.needsOldUpgradeSystem() );
        model.put( "requiredVersion", VerticalProperties.REQUIRED_40X_VERSION );
        model.put( "upgradeNeeded", this.upgradeService.needsUpgrade() );
        model.put( "upgradeInProgress", this.upgradeProcessTask.isInProgress() );
        model.put( "upgradeError", this.upgradeProcessTask.getError() );
        model.put( "upgradeLog", getFormattedLog( this.upgradeProcessTask.getLog() ) );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );
        model.put( "baseUrl", createBaseUrl( req ) );

        if ( !this.upgradeProcessTask.isInProgress() && !this.upgradeService.needsUpgrade() )
        {
            this.upgradeProcessTask.getLog().clear();
        }

        return new ModelAndView( "upgradePage", model );
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


}
