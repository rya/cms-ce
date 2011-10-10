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
 * Date: May 25, 2010
 * Time: 1:31:47 PM
 */
public class AssignContentCommand
{
    private UserKey assigneeKey;

    private UserKey assignerKey;

    private Date assignmentDueDate;

    private ContentKey contentKey;

    private String assignmentDescription;

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public UserKey getAssigneeKey()
    {
        return assigneeKey;
    }

    public void setAssigneeKey( UserKey assigneeKey )
    {
        this.assigneeKey = assigneeKey;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public UserKey getAssignerKey()
    {
        return assignerKey;
    }

    public void setAssignerKey( UserKey assignerKey )
    {
        this.assignerKey = assignerKey;
    }

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }
}
