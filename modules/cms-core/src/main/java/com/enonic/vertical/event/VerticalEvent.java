/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.core.security.user.User;

public class VerticalEvent
    extends java.util.EventObject
{

    private User user;

    /**
     * VerticalEvent constructor.
     *
     * @param source The object that emitted the event.
     */
    public VerticalEvent( User user, Object source )
    {
        super( source );
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

}
