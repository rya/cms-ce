/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class UserHandlerEvent
    extends VerticalEvent
{
    public UserHandlerEvent( User user, Object source )
    {
        super( user, source );
    }
}
