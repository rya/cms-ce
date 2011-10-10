/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.synchronize;

import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;

public interface SynchronizeUserStoreJob
{
    public void start();

    public SynchronizeStatus getStatus();
}
