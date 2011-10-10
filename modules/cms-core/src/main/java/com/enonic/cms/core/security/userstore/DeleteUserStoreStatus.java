/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.core.security.userstore.status.LocalGroupsStatus;
import com.enonic.cms.core.security.userstore.status.LocalUsersStatus;

public class DeleteUserStoreStatus
{
    private boolean completed = false;

    private final LocalUsersStatus localUsersStatus = new LocalUsersStatus();

    private final LocalGroupsStatus localGroupsStatus = new LocalGroupsStatus();

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted()
    {
        completed = true;
    }

    public LocalUsersStatus getLocalUsersStatus()
    {
        return localUsersStatus;
    }

    public LocalGroupsStatus getLocalGroupsStatus()
    {
        return localGroupsStatus;
    }

    public void setTotalLocalUserCount( final int value )
    {
        localUsersStatus.setTotalCount( value );
    }

    public void usersDeleted()
    {
        localUsersStatus.deleted();
    }

    public void setTotalLocalGroupCount( final int value )
    {
        localGroupsStatus.setTotalCount( value );
    }

    public void groupsDeleted()
    {
        localGroupsStatus.deleted();
    }
}
