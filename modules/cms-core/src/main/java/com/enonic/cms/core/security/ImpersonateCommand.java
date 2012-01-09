package com.enonic.cms.core.security;


import com.enonic.cms.core.security.user.UserKey;

public class ImpersonateCommand
{
    private boolean requireAccessCheck = true;

    private UserKey user;

    public ImpersonateCommand( boolean requireAccessCheck, UserKey user )
    {
        this.requireAccessCheck = requireAccessCheck;
        this.user = user;
    }

    public boolean isRequireAccessCheck()
    {
        return requireAccessCheck;
    }

    public UserKey getUser()
    {
        return user;
    }
}

