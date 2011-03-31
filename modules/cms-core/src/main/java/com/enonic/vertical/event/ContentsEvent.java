/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class ContentsEvent
    extends ContentHandlerEvent
{
    int[] contentKeys;

    /**
     * Constructor.
     *
     * @param source      The object emitting the event.
     * @param contentKeys Identifies the content.
     */

    public ContentsEvent( User user, Object source, int[] contentKeys )
    {
        super( user, source );
        this.contentKeys = contentKeys;
    }

    public int[] getContentKeys()
    {
        return contentKeys;
    }
}
