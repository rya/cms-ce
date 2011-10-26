/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import com.enonic.cms.upgrade.UpgradeException;
import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;

/**
 * This class implements the upgrade process task.
 */
public final class UpgradeProcessTask
    implements Runnable
{
    /**
     * Upgrade service.
     */
    private final UpgradeService upgradeService;

    /**
     * Upgrade log.
     */
    private final UpgradeLog upgradeLog;

    /**
     * Thread.
     */
    private Thread upgradeThread;

    /**
     * Upgrade error.
     */
    private UpgradeException upgradeError;

    /**
     * Execute all?
     */
    private boolean executeAll;

    /**
     * Construct the upgrade process task.
     */
    public UpgradeProcessTask( UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
        this.upgradeLog = new UpgradeLog();
    }

    /**
     * Retrun true if in progress.
     */
    public boolean isInProgress()
    {
        return this.upgradeThread != null;
    }

    /**
     * Return the upgrade log.
     */
    public UpgradeLog getLog()
    {
        return this.upgradeLog;
    }

    /**
     * Return true if error.
     */
    public boolean isError()
    {
        return this.upgradeError != null;
    }

    /**
     * Return the error.
     */
    public UpgradeException getError()
    {
        return this.upgradeError;
    }

    /**
     * Start upgrade.
     */
    public void startUpgrade( boolean executeAll )
    {
        if ( isInProgress() )
        {
            return;
        }

        this.upgradeThread = new Thread( this, "Upgrade Thread" );
        this.executeAll = executeAll;
        this.upgradeError = null;
        this.upgradeLog.clear();
        this.upgradeThread.start();
    }

    /**
     * Execute task.
     */
    public void run()
    {
        try
        {
            if ( this.executeAll )
            {
                this.upgradeService.upgrade( this.upgradeLog );
            }
            else
            {
                this.upgradeService.upgradeStep( this.upgradeLog );
            }
        }
        catch ( UpgradeException e )
        {
            this.upgradeError = e;
        }
        finally
        {
            this.upgradeThread = null;
        }
    }
}
