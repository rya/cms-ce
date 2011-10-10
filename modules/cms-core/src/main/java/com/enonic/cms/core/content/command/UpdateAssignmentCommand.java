/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import java.util.Date;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 22, 2010
 * Time: 8:14:01 AM
 */
public class UpdateAssignmentCommand
{
    private String assignmentDescription;

    private Date assignmentDueDate;

    private UserKey updater;

    private ContentKey contentKey;

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public void setUpdater( UserKey updater )
    {
        this.updater = updater;
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
