/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.security.user.UserEntity;

public class ContentSpecification
{
    private UserEntity assignee;

    private boolean assignedDraftsOnly = false;

    private UserEntity user;

    private boolean includeDeleted = false;

    public UserEntity getAssignee()
    {
        return assignee;
    }

    public void setAssignee( UserEntity assignee )
    {
        this.assignee = assignee;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public boolean doIncludeDeleted()
    {
        return includeDeleted;
    }

    public void setIncludeDeleted( boolean includeDeleted )
    {
        this.includeDeleted = includeDeleted;
    }

    public boolean assignedDraftsOnly()
    {
        return assignedDraftsOnly;
    }

    public void setAssignedDraftsOnly( boolean assignedDraftsOnly )
    {
        this.assignedDraftsOnly = assignedDraftsOnly;
    }
}
