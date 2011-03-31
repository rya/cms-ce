/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class RemovedUserEvent
    extends UserHandlerEvent
{
    private String userKey;

    public RemovedUserEvent( User user, Object source, String userKey )
    {
        super( user, source );

        this.userKey = userKey;
    }

    public String getUserKey()
    {
        return userKey;
    }
}
