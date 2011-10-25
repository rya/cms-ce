/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.upgrade.UpgradeService;

/**
 * This class implements the upgrade checker and should be used in all servlets that needs this functionality.
 */
public final class UpgradeChecker
{
    /**
     * Upgrade service.
     */
    private final UpgradeService upgradeService;

    /**
     * Construct the upgrade checker.
     */
    public UpgradeChecker( UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    /**
     * Check if we need to upgrade. Print out an error if we do and return true.
     */
    public boolean checkUpgrade( HttpServletResponse res )
        throws IOException
    {
        boolean upgradeNeeded = this.upgradeService.needsUpgrade();
        boolean softwareUpgradeNeeded = this.upgradeService.needsSoftwareUpgrade();

        if ( upgradeNeeded )
        {
            doUpgradeNeededError( res );
        }
        else if ( softwareUpgradeNeeded )
        {
            doSoftwareUpgradeNeededError( res );
        }

        return upgradeNeeded || softwareUpgradeNeeded;
    }

    /**
     * Send the upgrade needed error.
     */
    private void doUpgradeNeededError( HttpServletResponse res )
        throws IOException
    {
        StringBuffer str = new StringBuffer();
        str.append( "Service unavailable. Upgrade is needed from model " );
        str.append( this.upgradeService.getCurrentModelNumber() ).append( " to model " );
        str.append( this.upgradeService.getTargetModelNumber() ).append( "." );
        res.sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE, str.toString() );
    }

    /**
     * Do software upgrade needed error.
     */
    private void doSoftwareUpgradeNeededError( HttpServletResponse res )
        throws IOException
    {
        StringBuffer str = new StringBuffer();
        str.append( "Service unavailable. Model is newer than software. Software upgrade is needed." );
        res.sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE, str.toString() );
    }
}
