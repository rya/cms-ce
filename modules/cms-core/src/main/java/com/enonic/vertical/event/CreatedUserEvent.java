/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserType;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

public class CreatedUserEvent
    extends UserHandlerEvent
{
    private String uid;

    private String uKey;

    private UserType uType;

    private UserStoreKey userStoreKey;

    public CreatedUserEvent( User user, Object source, String uid, String uKey, UserType uType, UserStoreKey userStoreKey )
    {
        super( user, source );

        this.uKey = uKey;
        this.uid = uid;
        this.uType = uType;
        this.userStoreKey = userStoreKey;
    }

    public String getUserKey()
    {
        return uKey;
    }

    public UserType getUserType()
    {
        return uType;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }
}
