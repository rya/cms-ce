/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GroupMembershipDiffResolverTest
{
    private GroupEntity userGroup;

    @Before
    public void setUp()
    {
        userGroup = new GroupEntity();
        userGroup.setKey( "user1" );
    }

    @Test
    public void testFindGroupsToJoin()
    {
        GroupEntity memberGroup1 = new GroupEntity();
        memberGroup1.setDeleted( false );
        memberGroup1.setKey( "group100" );
        userGroup.addMembership( memberGroup1 );
        Set<GroupKey> requestedGroups = new HashSet<GroupKey>();
        requestedGroups.add( new GroupKey( "group100" ) );
        requestedGroups.add( new GroupKey( "group700" ) );
        requestedGroups.add( new GroupKey( "group888" ) );
        GroupMembershipDiffResolver groupMembershipDiffResolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> groupsToJoin = groupMembershipDiffResolver.findGroupsToJoin( requestedGroups );

        // asserts
        Set<GroupKey> expectedGroupsToJoin = new HashSet<GroupKey>();
        expectedGroupsToJoin.add( new GroupKey( "group700" ) );
        expectedGroupsToJoin.add( new GroupKey( "group888" ) );

        assertCollectionsEqual( expectedGroupsToJoin, groupsToJoin );
    }

    @Test
    public void testFindGroupsToLeave()
    {
        GroupEntity memberGroup1 = new GroupEntity();
        GroupEntity memberGroup2 = new GroupEntity();
        GroupEntity memberGroup3 = new GroupEntity();
        memberGroup1.setDeleted( false );
        memberGroup2.setDeleted( false );
        memberGroup3.setDeleted( false );
        memberGroup1.setKey( "group100" );
        memberGroup2.setKey( "group700" );
        memberGroup3.setKey( "group888" );
        userGroup.addMembership( memberGroup1 );
        userGroup.addMembership( memberGroup2 );
        userGroup.addMembership( memberGroup3 );
        Set<GroupKey> requestedGroups = new HashSet<GroupKey>();
        requestedGroups.add( new GroupKey( "group100" ) );
        GroupMembershipDiffResolver groupMembershipDiffResolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> groupsToLeave = groupMembershipDiffResolver.findGroupsToLeave( requestedGroups );

        // asserts
        Set<GroupKey> expectedGroupsToLeave = new HashSet<GroupKey>();
        expectedGroupsToLeave.add( new GroupKey( "group700" ) );
        expectedGroupsToLeave.add( new GroupKey( "group888" ) );

        assertCollectionsEqual( expectedGroupsToLeave, groupsToLeave );
    }

    @Test
    public void testFindGroupsDiff()
    {
        GroupEntity memberGroup1 = new GroupEntity();
        GroupEntity memberGroup2 = new GroupEntity();
        memberGroup1.setDeleted( false );
        memberGroup2.setDeleted( false );
        memberGroup1.setKey( "group100" );
        memberGroup2.setKey( "group700" );
        userGroup.addMembership( memberGroup1 );
        userGroup.addMembership( memberGroup2 );
        Set<GroupKey> requestedGroups = new HashSet<GroupKey>();
        requestedGroups.add( new GroupKey( "group700" ) );
        requestedGroups.add( new GroupKey( "group800" ) );
        requestedGroups.add( new GroupKey( "group888" ) );
        requestedGroups.add( new GroupKey( "group999" ) );
        GroupMembershipDiffResolver groupMembershipDiffResolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> groupsToJoin = groupMembershipDiffResolver.findGroupsToJoin( requestedGroups );
        Set<GroupKey> groupsToLeave = groupMembershipDiffResolver.findGroupsToLeave( requestedGroups );

        // asserts
        Set<GroupKey> expectedGroupsToJoin = new HashSet<GroupKey>();
        expectedGroupsToJoin.add( new GroupKey( "group800" ) );
        expectedGroupsToJoin.add( new GroupKey( "group888" ) );
        expectedGroupsToJoin.add( new GroupKey( "group999" ) );

        Set<GroupKey> expectedGroupsToLeave = new HashSet<GroupKey>();
        expectedGroupsToLeave.add( new GroupKey( "group100" ) );

        assertCollectionsEqual( expectedGroupsToJoin, groupsToJoin );
        assertCollectionsEqual( expectedGroupsToLeave, groupsToLeave );
    }

    @Test
    public void testFindGroupsDiffWithEmptyValues()
    {
        Set<GroupKey> requestedGroups = new HashSet<GroupKey>();
        GroupMembershipDiffResolver groupMembershipDiffResolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> groupsToJoin = groupMembershipDiffResolver.findGroupsToJoin( requestedGroups );
        Set<GroupKey> groupsToLeave = groupMembershipDiffResolver.findGroupsToLeave( requestedGroups );

        // asserts
        Set<GroupKey> expectedGroupsToJoin = new HashSet<GroupKey>();

        Set<GroupKey> expectedGroupsToLeave = new HashSet<GroupKey>();

        assertCollectionsEqual( expectedGroupsToJoin, groupsToJoin );
        assertCollectionsEqual( expectedGroupsToLeave, groupsToLeave );
    }

    public <T> void assertCollectionsEqual( Collection<T> expected, Collection<T> actual )
    {
        assertEquals( expected.size(), actual.size() );
        for ( T actualElement : actual )
        {
            assertTrue( expected.contains( actualElement ) );
        }
    }
}
