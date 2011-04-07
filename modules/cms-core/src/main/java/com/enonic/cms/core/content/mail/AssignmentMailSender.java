/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.mail;

import java.util.Date;

import org.springframework.util.Assert;

import com.enonic.cms.business.mail.SendMailService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 18, 2010
 * Time: 8:35:05 AM
 */
public class AssignmentMailSender
{
    public SendMailService sendMailService;

    private UserEntity updater;

    private String assignmentDescription;

    private Date assignmentDueDate;

    private ContentEntity assignedContent;

    private UserEntity originalAssignee;

    private UserEntity originalAssigner;

    private UserEntity newAssignee;

    public AssignmentMailSender( SendMailService sendMailService )
    {
        this.sendMailService = sendMailService;
    }

    public void sendAssignmentMails()
    {
        verify();

        AssignedContentMailTemplate assignedMail = createAssignedContentMailTemplate();
        if ( assignedMail != null )
        {
            sendMailService.sendMail( assignedMail );
        }

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = createAssignmentHasBeenTakenOverMailTemplate();
        if ( assignmentTakenOverMail != null )
        {
            sendMailService.sendMail( assignmentTakenOverMail );
        }
    }

    protected AssignedContentMailTemplate createAssignedContentMailTemplate()
    {
        if ( !doCreateAssignedContentMail() )
        {
            return null;
        }

        AssignedContentMailTemplate assignedContentMailTemplate =
            new AssignedContentMailTemplate( assignedContent, assignedContent.getAssignedVersion() );
        assignedContentMailTemplate.addRecipient( newAssignee );
        assignedContentMailTemplate.setAssignmentDescription( assignmentDescription );
        assignedContentMailTemplate.setAssigner( updater );
        assignedContentMailTemplate.setAssignmentDueDate( assignmentDueDate );

        return assignedContentMailTemplate;
    }

    protected AssignmentTakenOverMailTemplate createAssignmentHasBeenTakenOverMailTemplate()
    {
        if ( !doCreateAssignmentHasBeenTakenOverMail() )
        {
            return null;
        }

        AssignmentTakenOverMailTemplate assignmentTakeOverMailTemplate =
            new AssignmentTakenOverMailTemplate( assignedContent, assignedContent.getAssignedVersion() );

        assignmentTakeOverMailTemplate.setAssigner( updater );
        assignmentTakeOverMailTemplate.setAssignmentDueDate( assignmentDueDate );
        assignmentTakeOverMailTemplate.setAssignmentDescription( assignmentDescription );

        assignmentTakeOverMailTemplate.addRecipient( originalAssignee );
        if ( !originalAssignee.equals( originalAssigner ) )
        {
            assignmentTakeOverMailTemplate.addRecipient( originalAssigner );
        }

        return assignmentTakeOverMailTemplate;
    }


    private boolean doCreateAssignedContentMail()
    {
        if ( updater.equals( newAssignee ) )
        {
            return false;
        }

        return true;
    }

    private boolean doCreateAssignmentHasBeenTakenOverMail()
    {
        boolean contentHasBeenReassigned = originalAssignee != null;

        if ( contentHasBeenReassigned && updater.equals( newAssignee ) )
        {
            return true;
        }

        return false;
    }

    public void verify()
    {
        Assert.isTrue( updater != null );
        Assert.isTrue( sendMailService != null );
        Assert.isTrue( assignedContent != null );
    }


    public void setUpdater( UserEntity updater )
    {
        this.updater = updater;
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

    public void setAssignedContent( ContentEntity assignedContent )
    {
        this.assignedContent = assignedContent;
    }

    public void setOriginalAssignee( UserEntity originalAssignee )
    {
        this.originalAssignee = originalAssignee;
    }

    public void setOriginalAssigner( UserEntity originalAssigner )
    {
        this.originalAssigner = originalAssigner;
    }

    public void setNewAssignee( UserEntity newAssignee )
    {
        this.newAssignee = newAssignee;
    }
}
