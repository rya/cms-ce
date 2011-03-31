/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class ContentHandlerEvent
    extends VerticalEvent
{
    /**
     * ContentHandlerEvent constructor.
     *
     * @param source The object that emitted the event.
     */
    public ContentHandlerEvent( User user, Object source )
    {
        super( user, source );
    }
}
