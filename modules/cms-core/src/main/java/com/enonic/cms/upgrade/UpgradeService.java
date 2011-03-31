/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade;

import com.enonic.cms.upgrade.log.UpgradeLog;

/**
 * This interface defines the upgrade manager.
 */
public interface UpgradeService
{
    /**
     * Return true if it needs upgrade.
     */
    public boolean needsUpgrade();

    /**
     * Return true if software is too old.
     */
    public boolean needsSoftwareUpgrade();

    /**
     * Return true if it needs to upgrade with old upgrade model first. Only for upgrading versions before 4.1.
     */
    public boolean needsOldUpgradeSystem();

    /**
     * Return the current model.
     */
    public int getCurrentModelNumber();

    /**
     * Return the target model.
     */
    public int getTargetModelNumber();

    /**
     * Upgrade all steps.
     */
    public boolean upgrade( UpgradeLog log )
        throws UpgradeException;

    /**
     * Upgrade single step.
     */
    public boolean upgradeStep( UpgradeLog log )
        throws UpgradeException;

    /**
     * Check if you can upgrade.
     */
    public boolean canUpgrade( UpgradeLog log );
}