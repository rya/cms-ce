/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class ContentEvent
    extends ContentHandlerEvent
{

    private int contentKey;

    private String title;

    private boolean isCurrent;

    /**
     * ContentEvent constructor comment.
     *
     * @param source     The object that emitted the event.
     * @param contentKey Identifies the content.
     */
    public ContentEvent( User user, Object source, int contentKey, String title, boolean isCurrent )
    {
        super( user, source );
        this.contentKey = contentKey;
        this.title = title;
        this.isCurrent = isCurrent;

    }

    public int getContentKey()
    {
        return contentKey;
    }

    public String getTitle()
    {
        return title;
    }
}
