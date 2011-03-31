/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

public interface UpgradeTask
    extends Comparable<UpgradeTask>
{
    public int getModelNumber();

    public boolean canUpgrade( UpgradeContext context );

    public void upgrade( UpgradeContext context )
        throws Exception;

    public boolean isRunTransactional();
}
