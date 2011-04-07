/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 17, 2010
 * Time: 2:16:40 PM
 */
public class AssignContentResult
{
    private ContentKey assignedContentKey;

    private UserEntity originalAssignee;

    private UserEntity newAssignee;

    private UserEntity originalAssigner;

    public UserEntity getOriginalAssignee()
    {
        return originalAssignee;
    }

    public void setOriginalAssignee( UserEntity originalAssignee )
    {
        this.originalAssignee = originalAssignee;
    }

    public UserEntity getNewAssignee()
    {
        return newAssignee;
    }

    public void setNewAssignee( UserEntity newAssignee )
    {
        this.newAssignee = newAssignee;
    }

    public UserEntity getOriginalAssigner()
    {
        return originalAssigner;
    }

    public void setOriginalAssigner( UserEntity originalAssigner )
    {
        this.originalAssigner = originalAssigner;
    }

    public ContentKey getAssignedContentKey()
    {
        return assignedContentKey;
    }

    public void setAssignedContentKey( ContentKey assignedContentKey )
    {
        this.assignedContentKey = assignedContentKey;
    }
}
