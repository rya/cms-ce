/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;

public class MemberCache
{
    final List<GroupEntity> groups = new ArrayList<GroupEntity>();

    final List<UserEntity> users = new ArrayList<UserEntity>();

    public GroupEntity getMemberOfTypeGroup( final GroupSpecification spec )
    {
        for ( final GroupEntity group : groups )
        {
            if ( spec.isSatisfiedBy( group ) )
            {
                return group;
            }
        }
        return null;
    }

    public UserEntity getMemberOfTypeUser( final UserSpecification spec )
    {
        for ( final UserEntity user : users )
        {
            if ( spec.isSatisfiedBy( user ) )
            {
                return user;
            }
        }
        return null;
    }

    public void addMemeberOfTypeGroup( final GroupEntity group )
    {
        groups.add( group );
    }

    public void addMemeberOfTypeUser( final UserEntity user )
    {
        users.add( user );
    }
}