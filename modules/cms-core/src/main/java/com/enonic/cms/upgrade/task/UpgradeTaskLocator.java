/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import java.util.List;

public interface UpgradeTaskLocator
{
    public List<UpgradeTask> getTasks();
}
