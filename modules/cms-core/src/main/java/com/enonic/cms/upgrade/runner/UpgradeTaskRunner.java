/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.runner;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.UpgradeTask;

public interface UpgradeTaskRunner
{
    public void runUpgradeTask( UpgradeContext context, UpgradeTask task )
        throws Throwable;

    public void runUpgradeTaskInTx( UpgradeContext context, UpgradeTask task )
        throws Throwable;
}
