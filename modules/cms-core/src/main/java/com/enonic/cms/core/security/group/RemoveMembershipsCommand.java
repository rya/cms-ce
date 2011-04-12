/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.user.UserKey;

public class RemoveMembershipsCommand
{
    private UserKey executor;

    private GroupSpecification groupToRemove;

    private List<GroupKey> groupsToRemoveFrom = new ArrayList<GroupKey>();

    private boolean respondWithException = false;

    private boolean updateOpenGroupsOnly = false;

    public RemoveMembershipsCommand( GroupSpecification groupToUpdateSpecification, UserKey executor )
    {
        setGroupToRemove( groupToUpdateSpecification );
        setExecutor( executor );
    }

    public GroupSpecification getGroupToRemove()
    {
        return groupToRemove;
    }

    public void setGroupToRemove( GroupSpecification spec )
    {
        groupToRemove = spec;
    }

    public List<GroupKey> getGroupsToRemoveFrom()
    {
        return groupsToRemoveFrom;
    }

    public void addGroupToRemoveFrom( GroupKey groupKey )
    {
        groupsToRemoveFrom.add( groupKey );
    }

    public UserKey getExecutor()
    {
        return executor;
    }

    public void setExecutor( UserKey key )
    {
        executor = key;
    }

    public boolean isRespondWithException()
    {
        return respondWithException;
    }

    public void setRespondWithException( boolean b )
    {
        respondWithException = b;
    }

    public boolean isUpdateOpenGroupsOnly()
    {
        return updateOpenGroupsOnly;
    }

    public void setUpdateOpenGroupsOnly( boolean value )
    {
        this.updateOpenGroupsOnly = value;
    }
}
