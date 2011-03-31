/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class GroupEvent
    extends GroupHandlerEvent
{

    private String groupKey;

    /**
     * Constructor.
     *
     * @param source   The object emitting the event.
     * @param groupKey Identifies the group.
     */
    public GroupEvent( User user, Object source, String groupKey )
    {
        super( user, source );
        this.groupKey = groupKey;
    }

    public String getGroupKey()
    {
        return groupKey;
    }
}
