/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Date;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 21, 2010
 * Time: 3:24:26 PM
 */
public class AssignmentActionResolver
{

    public AssignmentAction resolveAssignmentAction( ContentEntity submittedContent, ContentVersionEntity submittedVersion,
                                                     ContentEntity persistedContent )
    {
        UserEntity existingAssignee = persistedContent.getAssignee();
        UserEntity submittedAssignee = submittedContent.getAssignee();

        boolean contentIsAssigned = existingAssignee != null;
        boolean assigneeSubmitted = submittedAssignee != null;

        if ( !submittedVersion.isDraft() )
        {
            if ( contentIsAssigned && submittedVersion.isApproved() )
            {
                return AssignmentAction.UNASSIGN_SINCE_APPROVED;
            }
            else if ( contentIsAssigned )
            {
                return AssignmentAction.UNASSIGN;
            }
            else
            {
                return AssignmentAction.DONT_TOUCH;
            }
        }

        if ( !contentIsAssigned && assigneeSubmitted )
        {
            return AssignmentAction.ASSIGN;
        }

        if ( !contentIsAssigned && !assigneeSubmitted )
        {
            return AssignmentAction.DONT_TOUCH;
        }

        if ( contentIsAssigned && !assigneeSubmitted )
        {
            return AssignmentAction.UNASSIGN;
        }

        if ( contentIsAssigned && assigneeSubmitted && !existingAssignee.equals( submittedAssignee ) )
        {
            return AssignmentAction.REASSIGN;
        }

        if ( assignmentDescriptionModified( persistedContent, submittedContent ) )
        {
            return AssignmentAction.UPDATE_ASSIGNMENT;
        }

        if ( assignmentDueDateModified( persistedContent, submittedContent ) )
        {
            return AssignmentAction.UPDATE_ASSIGNMENT;
        }

        return AssignmentAction.DONT_TOUCH;
    }

    private boolean assignmentDueDateModified( ContentEntity persistedContent, ContentEntity submittedContent )
    {
        Date newDuedate = submittedContent.getAssignmentDueDate();

        Date existingDuedate = persistedContent.getAssignmentDueDate();

        if ( existingDuedate == null && newDuedate == null )
        {
            return false;
        }

        long otherTime = newDuedate != null ? newDuedate.getTime() : -1;
        long thisTime = existingDuedate != null ? existingDuedate.getTime() : -1;

        if ( thisTime == otherTime )
        {
            return false;
        }

        return true;
    }


    private boolean assignmentDescriptionModified( ContentEntity persistedContent, ContentEntity submittedContent )
    {
        boolean modified = false;

        String existingAssignmentDescription = persistedContent.getAssignmentDescription();
        String newAssignmentDescription = submittedContent.getAssignmentDescription();

        if ( newAssignmentDescription != null )
        {
            if ( !newAssignmentDescription.equals( existingAssignmentDescription ) )
            {
                return true;
            }
        }
        else if ( persistedContent.getAssignmentDescription() != null )
        {
            return true;
        }

        return modified;
    }


}
