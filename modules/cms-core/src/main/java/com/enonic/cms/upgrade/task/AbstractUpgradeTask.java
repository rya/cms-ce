/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

public abstract class AbstractUpgradeTask
    implements UpgradeTask
{
    private final static int START_MODEL_NUMBER = 119;

    private final int modelNumber;

    public AbstractUpgradeTask( int modelNumber )
    {
        this.modelNumber = modelNumber;
    }

    public final int getModelNumber()
    {
        return this.modelNumber;
    }

    protected boolean canModelUpgrade( UpgradeContext context )
    {
        context.logInfo( "Upgrade check ok." );
        return true;
    }

    public final boolean canUpgrade( UpgradeContext context )
    {
        if ( context.getStartModelNumber() < START_MODEL_NUMBER )
        {
            context.logError( "Cannot upgrade from database version " + context.getStartModelNumber() + ". Version " + START_MODEL_NUMBER +
                " required. Please upgrade to latest version of 4.5 and try again." );
            return false;
        }
        return canModelUpgrade( context );
    }

    public boolean isRunTransactional()
    {
        return true;
    }

    public int compareTo( UpgradeTask upgradeTask )
    {
        int modelArg0 = getModelNumber();
        int modelArg1 = upgradeTask.getModelNumber();
        return modelArg0 - modelArg1;
    }
}
