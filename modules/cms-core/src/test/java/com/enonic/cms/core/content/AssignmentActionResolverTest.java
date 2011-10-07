/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 22, 2010
 * Time: 8:53:51 AM
 */
public class AssignmentActionResolverTest
{
    UserEntity assignee1;

    UserEntity assignee2;

    UserEntity assigner1;

    UserEntity assigner2;

    Date now = Calendar.getInstance().getTime();

    AssignmentActionResolver actionResolver;

    @Before
    public void setUp()
    {
        assignee1 = createUser( "assignee1" );
        assignee2 = createUser( "assignee2" );
        assigner1 = createUser( "assigner1" );
        assigner2 = createUser( "assigner2" );

        actionResolver = new AssignmentActionResolver();
    }

    @Test
    public void testNewAssigneSubmitted()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee2, assigner1, "descr", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.REASSIGN );
    }

    @Test
    public void testAssignerChanged()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner2, "descr", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( "Should not affect anything, since this is not really valid", action, AssignmentAction.DONT_TOUCH );
    }

    @Test
    public void testNoChanges()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.DONT_TOUCH );
    }


    @Test
    public void testOnlyDescrChanged()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr2", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );
    }

    @Test
    public void testOnlyDuedateChanged()
    {
        Date newDate = new Date( 123456789 );

        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", newDate );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );
    }


    @Test
    public void testEmptyAssigneeSubmitted()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), null, null, null, null );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UNASSIGN );
    }

    @Test
    public void testNoOriginalAssignmentThenSubmitted()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), null, null, null, null );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.ASSIGN );
    }

    @Test
    public void testNoOriginalAssignmentNoSubmitted()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), null, null, null, null );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), null, null, null, null );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.DONT_TOUCH );
    }

    @Test
    public void testUnassignedOnApproved()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        ContentEntity submittedContent = createContent( false, new ContentKey( "1" ), assignee1, assigner1, "descr", now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getMainVersion(), originalContent );

        assertEquals( action, AssignmentAction.UNASSIGN_SINCE_APPROVED );
    }


    @Test
    public void testMetadataNullValues()
    {
        ContentEntity originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, null, now );
        ContentEntity submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, null, now );

        AssignmentAction action =
            actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.DONT_TOUCH );

        originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, null, now );
        submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );

        action = actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );

        originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, null, now );

        action = actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );

        originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", null );
        submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", null );

        action = actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.DONT_TOUCH );

        originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", null );
        submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );

        action = actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );

        originalContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", now );
        submittedContent = createContent( true, new ContentKey( "1" ), assignee1, assigner1, "descr", null );

        action = actionResolver.resolveAssignmentAction( submittedContent, submittedContent.getDraftVersion(), originalContent );

        assertEquals( action, AssignmentAction.UPDATE_ASSIGNMENT );
    }

    private UserEntity createUser( String username )
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( username ) );
        user.setName( username );
        user.setDisplayName( username );

        return user;
    }

    private ContentEntity createContent( boolean hasDraft, ContentKey contentKey, UserEntity assignee, UserEntity assigner,
                                         String assignmentDescription, Date assignDuedate )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );
        content.setAssignmentDescription( assignmentDescription );
        content.setAssignee( assignee );
        content.setAssigner( assigner );
        content.setAssignmentDueDate( assignDuedate );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( "1" ) );
        version.setStatus( hasDraft ? ContentStatus.DRAFT : ContentStatus.APPROVED );

        content.addVersion( version );
        content.setDraftVersion( hasDraft ? version : null );
        content.setMainVersion( version );

        return content;
    }


}
