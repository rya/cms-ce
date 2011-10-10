/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Jun 10, 2010
 */
public class UnassignContentCommand
{
    private UserKey unassigner;

    private ContentKey contentKey;

    public UserKey getUnassigner()
    {
        return unassigner;
    }

    public void setUnassigner( UserKey unassigner )
    {
        this.unassigner = unassigner;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }
}
