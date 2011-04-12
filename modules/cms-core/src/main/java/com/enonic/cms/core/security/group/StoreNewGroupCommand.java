/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class StoreNewGroupCommand
{
    private String name;

    private String description;

    private boolean restriced = false;

    private String syncValue;

    private GroupType type;

    private UserKey userKey;

    private UserStoreKey userStoreKey;

    private List<GroupKey> members = null;

    private UserEntity executor;

    private boolean respondWithException = false;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public boolean isRestriced()
    {
        return restriced;
    }

    public void setRestriced( boolean restriced )
    {
        this.restriced = restriced;
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public void setSyncValue( String syncValue )
    {
        this.syncValue = syncValue;
    }

    public GroupType getType()
    {
        return type;
    }

    public void setType( GroupType type )
    {
        this.type = type;
    }

    public UserKey getUserKey()
    {
        return userKey;
    }

    public void setUserKey( UserKey userKey )
    {
        this.userKey = userKey;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public void setUserStoreKey( UserStoreKey userStoreKey )
    {
        this.userStoreKey = userStoreKey;
    }

    public void addMember( GroupKey groupKey )
    {
        if ( members == null )
        {
            members = new ArrayList<GroupKey>();
        }
        members.add( groupKey );
    }

    public List<GroupKey> getMembers()
    {
        return members;
    }

    public UserEntity getExecutor()
    {
        return executor;
    }

    public void setExecutor( UserEntity executor )
    {
        this.executor = executor;
    }

    public boolean isRespondWithException()
    {
        return respondWithException;
    }

    public void setRespondWithException( boolean respondWithException )
    {
        this.respondWithException = respondWithException;
    }
}
