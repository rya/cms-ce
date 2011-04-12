/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.group.AbstractMembershipsCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.UserInfo;

/**
 * Jun 18, 2009
 */
public class StoreNewUserCommand
    extends AbstractMembershipsCommand
{
    private UserKey storer;

    private String username;

    private String password;

    private String displayName;

    private String email;

    private String syncValue = "NA";

    private UserStoreKey userStoreKey;

    private UserType type = UserType.NORMAL;

    private UserInfo userInfo = null;

    private boolean allowAnyUserAccess = false;

    public UserKey getStorer()
    {
        return storer;
    }

    public void setStorer( UserKey value )
    {
        storer = value;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String value )
    {
        displayName = value;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( final String value )
    {
        username = value;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( final String value )
    {
        password = value;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public void setUserStoreKey( final UserStoreKey value )
    {
        userStoreKey = value;
    }

    public UserType getType()
    {
        return type;
    }

    public void setType( final UserType value )
    {
        type = value;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String value )
    {
        email = value;
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public void setSyncValue( final String value )
    {
        syncValue = value;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( final UserInfo value )
    {
        userInfo = value;
    }

    public void setAllowAnyUserAccess( boolean allowAnyUserAccess )
    {
        this.allowAnyUserAccess = allowAnyUserAccess;
    }

    public boolean allowAnyUserAccess()
    {
        return allowAnyUserAccess;
    }
}
