/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class SiteHandlerEvent
    extends VerticalEvent
{
    /**
     * SiteHandlerEvent constructor.
     *
     * @param source The object emitting the event.
     */
    public SiteHandlerEvent( User user, Object source )
    {
        super( user, source );
    }
}
