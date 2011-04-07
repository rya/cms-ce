/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.mail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.content.mail.AssignedContentMailTemplate;
import com.enonic.cms.core.content.mail.AssignmentMailSender;
import com.enonic.cms.core.content.mail.AssignmentTakenOverMailTemplate;

import com.enonic.cms.business.mail.MailRecipient;
import com.enonic.cms.business.mail.SendMailService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 18, 2010
 * Time: 9:44:59 AM
 */
public class AssignmentMailSenderTest
{

    private SendMailService sendMailService;

    @Before
    public void setUp()
    {
        sendMailService = Mockito.mock( SendMailService.class );
    }


    @Test
    public void testAssignmentMailSender()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = createUser( "originalAssignee" );
        UserEntity newAssignee = createUser( "newAssignee" );
        UserEntity originalAssigner = createUser( "originalAssigner" );

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        assignmentMailSender.sendAssignmentMails();
    }


    @Test
    public void testTemplateCreators()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = createUser( "originalAssignee" );
        UserEntity newAssignee = createUser( "newAssignee" );
        UserEntity originalAssigner = createUser( "originalAssigner" );

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignedContentMailTemplate assignedContentMail = assignmentMailSender.createAssignedContentMailTemplate();

        assertNotNull( assignedContentMail );

        List<MailRecipient> mailRecipients = assignedContentMail.getMailRecipients();
        assertEquals( 1, mailRecipients.size() );
        assertTrue( "New assignee should get mail", mailRecipients.contains( new MailRecipient( newAssignee ) ) );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNull( "Content reassigned, no assignmentTakenOver-mail should be created", assignmentTakenOverMail );

    }

    @Test
    public void testTemplateCreators_testNoOriginalAssignment()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = null;
        UserEntity newAssignee = updater;
        UserEntity originalAssigner = null;

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNull( "Original assignment was not set, no assignmentTakenOver-mail should be created", assignmentTakenOverMail );
    }

    @Test
    public void testTemplateCreators_reassigned_by_original_assignee()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = updater;
        UserEntity newAssignee = createUser( "newAssignee" );
        UserEntity originalAssigner = createUser( "originalAssigner" );

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNull( "Content reassigned, no assignmentTakenOver-mail should be created", assignmentTakenOverMail );
    }


    @Test
    public void testTemplateCreators_ressigned_original_assignee_and_assigner_equal()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = createUser( "originalAssignee" );
        UserEntity newAssignee = createUser( "newAssignee" );
        UserEntity originalAssigner = originalAssignee;

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNull( "Content reassigned, no assignmentTakenOver-mail should be created", assignmentTakenOverMail );


    }

    @Test
    public void testTemplateCreators_reassigned_by_original_assigner()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = createUser( "originalAssignee" );
        UserEntity newAssignee = createUser( "newAssignee" );
        UserEntity originalAssigner = updater;

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNull( "Content reassigned, no assignmentTakenOver-mail should be created", assignmentTakenOverMail );

    }


    @Test
    public void testTemplateCreators_reassigned_by_new_assignee()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = createUser( "originalAssignee" );
        UserEntity newAssignee = updater;
        UserEntity originalAssigner = createUser( "originalAssigner" );

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignedContentMailTemplate assignedContentMail = assignmentMailSender.createAssignedContentMailTemplate();

        assertNull( assignedContentMail );

        AssignmentTakenOverMailTemplate assignmentTakenOverMail = assignmentMailSender.createAssignmentHasBeenTakenOverMailTemplate();

        assertNotNull( assignmentTakenOverMail );

        List<MailRecipient> reassignedMailRecipients = assignmentTakenOverMail.getMailRecipients();

        assertEquals( 2, reassignedMailRecipients.size() );
        assertTrue( "Original assignee should get assignment taken over-mail ",
                    reassignedMailRecipients.contains( new MailRecipient( originalAssignee ) ) );
        assertTrue( "Original assigner should get assignment taken over-mail",
                    reassignedMailRecipients.contains( new MailRecipient( originalAssigner ) ) );

    }

    @Test
    public void testTemplateCreators_all_roles_updater()
    {
        UserEntity updater = createUser( "updater" );
        UserEntity originalAssignee = updater;
        UserEntity newAssignee = updater;
        UserEntity originalAssigner = updater;

        AssignmentMailSender assignmentMailSender = setUpMailFactory( updater, originalAssignee, newAssignee, originalAssigner );

        AssignedContentMailTemplate assignedContentMail = assignmentMailSender.createAssignedContentMailTemplate();

        assertNull( assignedContentMail );

    }

    private AssignmentMailSender setUpMailFactory( UserEntity updater, UserEntity originalAssignee, UserEntity newAssignee,
                                                   UserEntity originalAssigner )
    {
        AssignmentMailSender assignmentMailSender = new AssignmentMailSender( sendMailService );
        assignmentMailSender.setAssignedContent( new ContentEntity() );
        assignmentMailSender.setNewAssignee( newAssignee );
        assignmentMailSender.setOriginalAssignee( originalAssignee );
        assignmentMailSender.setOriginalAssigner( originalAssigner );
        assignmentMailSender.setUpdater( updater );
        return assignmentMailSender;
    }


    private UserEntity createUser( String name )
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( name ) );
        user.setDisplayName( name );
        user.setName( name );
        user.setEmail( name + "@enonic.com" );

        return user;
    }

}
