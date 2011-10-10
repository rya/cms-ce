/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.user.UserKey;

public class AddMembershipsCommand
{
    private GroupSpecification groupToAdd;

    private List<GroupKey> groupsToAddTo = new ArrayList<GroupKey>();

    private UserKey executor;

    private boolean respondWithException = false;

    private boolean updateOpenGroupsOnly = false;

    public AddMembershipsCommand( GroupSpecification spec, UserKey executor )
    {
        setGroupToAdd( spec );
        setExecutor( executor );
    }

    public void addGroupsToAddTo( GroupKey groupKey )
    {
        groupsToAddTo.add( groupKey );
    }

    public GroupSpecification getGroupToAdd()
    {
        return groupToAdd;
    }

    public void setGroupToAdd( GroupSpecification spec )
    {
        groupToAdd = spec;
    }

    public List<GroupKey> getGroupsToAddTo()
    {
        return groupsToAddTo;
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