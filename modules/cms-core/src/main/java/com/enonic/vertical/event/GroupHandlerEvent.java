/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class GroupHandlerEvent
    extends VerticalEvent
{

    /**
     * Instantiates the object with the supplied parameters.
     *
     * @param source The object that emitted the event.
     */
    public GroupHandlerEvent( User user, Object source )
    {
        super( user, source );
    }
}
