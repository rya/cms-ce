/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.group;

import java.util.LinkedHashSet;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.junit.Test;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

import static org.junit.Assert.*;

public class GroupEntityTest
{

    @Test
    public void testAddMembership()
    {

        GroupEntity group_Oslo = createGroup( "0", "Oslo" );
        GroupEntity group_Norway = createGroup( "1", "Norway" );

        group_Oslo.addMembership( group_Norway );

        assertTrue( group_Oslo.isMemberOf( group_Norway, true ) );
        assertTrue( group_Norway.hasMember( group_Oslo ) );

        assertFalse( group_Norway.hasMember( group_Norway ) );
        assertFalse( group_Norway.isMemberOf( group_Norway, true ) );
    }

    @Test
    public void testMemberOfRecursively_DirectMembership()
    {

        GroupEntity group_Oslo = createGroup( "0", "Oslo" );
        GroupEntity group_Norway = createGroup( "1", "Norway" );

        group_Oslo.addMembership( group_Norway );

        assertTrue( group_Oslo.isMemberOf( group_Norway, true ) );
    }

    @Test
    public void testMemberOfRecursively_IndirectMembership()
    {

        GroupEntity group_Oslo = createGroup( "0", "Oslo" );
        GroupEntity group_Norway = createGroup( "1", "Norway" );
        GroupEntity group_Europe = createGroup( "2", "Europe" );

        group_Oslo.addMembership( group_Norway );
        group_Norway.addMembership( group_Europe );

        assertTrue( group_Oslo.isMemberOf( group_Europe, true ) );
    }

    @Test
    public void testMemberOfNotRecursively()
    {

        GroupEntity group_Oslo = createGroup( "0", "Oslo" );
        GroupEntity group_Norway = createGroup( "1", "Norway" );
        GroupEntity group_Europe = createGroup( "2", "Europe" );

        group_Oslo.addMembership( group_Norway );
        group_Norway.addMembership( group_Europe );

        assertFalse( group_Oslo.isMemberOf( group_Europe, false ) );
    }

    @Test
    public void getAllMembersRecursivelyAndCircular()
    {
        GroupEntity parent = createGroup( "0", "Parent", GroupType.USERSTORE_GROUP );
        GroupEntity child = createGroup( "1", "Child", GroupType.USERSTORE_GROUP );

        UserEntity user1 = createUser( "user1", "User One" );
        UserEntity user2 = createUser( "user2", "User Two" );

        user1.getUserGroup().addMembership( parent );
        user2.getUserGroup().addMembership( child );

        child.addMembership( parent );
        //add circular membership
        parent.addMembership( child );

        assertEquals( 3, parent.getAllMembersRecursively().size() );
        assertEquals( 2, parent.getMembers( false ).size() );

        Set<GroupType> groupTypeFilter = new LinkedHashSet<GroupType>();
        groupTypeFilter.add( GroupType.USER );
        assertEquals( 2, parent.getAllMembersRecursively( groupTypeFilter ).size() );
    }

    @Test
    public void getAllMembersRecursively()
    {
        GroupEntity world = createGroup( "0", "World", GroupType.GLOBAL_GROUP );
        GroupEntity europe = createGroup( "1", "Europe", GroupType.GLOBAL_GROUP );
        GroupEntity asia = createGroup( "2", "Asia", GroupType.GLOBAL_GROUP );
        GroupEntity norway = createGroup( "10", "Norway", GroupType.GLOBAL_GROUP );
        GroupEntity sweeden = createGroup( "11", "Sweeden", GroupType.GLOBAL_GROUP );
        GroupEntity ostfold = createGroup( "100", "Ostfold", GroupType.GLOBAL_GROUP );
        GroupEntity vestfold = createGroup( "101", "Vestfold", GroupType.GLOBAL_GROUP );
        GroupEntity bohuslen = createGroup( "102", "Bohuslen", GroupType.GLOBAL_GROUP );

        bohuslen.addMembership( sweeden );
        ostfold.addMembership( norway );
        vestfold.addMembership( norway );

        norway.addMembership( europe );
        sweeden.addMembership( europe );

        europe.addMembership( world );
        asia.addMembership( world );

        assertEquals( 7, world.getAllMembersRecursively().size() );
        assertEquals( 0, asia.getAllMembersRecursively().size() );
        assertEquals( 5, europe.getAllMembersRecursively().size() );
        assertEquals( 1, sweeden.getAllMembersRecursively().size() );
        assertEquals( 2, norway.getAllMembersRecursively().size() );
    }

    @Test
    public void getAllMembersRecursivelyWithTypeFiltering()
    {
        GroupEntity world = createGroup( "0", "World", GroupType.GLOBAL_GROUP );
        GroupEntity europe = createGroup( "1", "Europe", GroupType.GLOBAL_GROUP );
        GroupEntity asia = createGroup( "2", "Asia", GroupType.GLOBAL_GROUP );
        GroupEntity norway = createGroup( "10", "Norway", GroupType.EXPERT_CONTRIBUTORS );
        GroupEntity sweeden = createGroup( "11", "Sweeden", GroupType.GLOBAL_GROUP );
        GroupEntity ostfold = createGroup( "100", "Ostfold", GroupType.EXPERT_CONTRIBUTORS );
        GroupEntity vestfold = createGroup( "101", "Vestfold", GroupType.EXPERT_CONTRIBUTORS );
        GroupEntity bohuslen = createGroup( "102", "Bohuslen", GroupType.GLOBAL_GROUP );

        bohuslen.addMembership( sweeden );
        ostfold.addMembership( norway );
        vestfold.addMembership( norway );

        norway.addMembership( europe );
        sweeden.addMembership( europe );

        europe.addMembership( world );
        asia.addMembership( world );

        Set<GroupType> filter = new LinkedHashSet<GroupType>();
        filter.add( GroupType.EXPERT_CONTRIBUTORS );

        assertEquals( 3, world.getAllMembersRecursively( filter ).size() );
        assertEquals( 0, asia.getAllMembersRecursively( filter ).size() );
        assertEquals( 3, europe.getAllMembersRecursively( filter ).size() );
        assertEquals( 0, sweeden.getAllMembersRecursively( filter ).size() );
        assertEquals( 2, norway.getAllMembersRecursively( filter ).size() );
    }

    @Test
    public void testMemberOfRecursively_DeepIndirectMembership()
    {

        GroupEntity groupDyreriket = createGroup( "0", "Dyreriket" );
        GroupEntity groupPattedyr = createGroup( "1", "Pattedyr" );
        GroupEntity groupHunder = createGroup( "2", "Hunder" );
        GroupEntity groupKatter = createGroup( "3", "Katter" );
        GroupEntity groupPusekatter = createGroup( "4", "Katter" );

        groupPattedyr.addMembership( groupDyreriket );
        groupHunder.addMembership( groupPattedyr );
        groupKatter.addMembership( groupPattedyr );
        groupPusekatter.addMembership( groupKatter );

        assertTrue( groupPusekatter.isMemberOf( groupDyreriket, true ) );
        assertTrue( groupHunder.isMemberOf( groupDyreriket, true ) );
        assertTrue( groupKatter.isMemberOf( groupDyreriket, true ) );
        assertTrue( groupKatter.isMemberOf( groupPattedyr, true ) );
    }

    @Test
    public void testNotMemberOfRecursively_IndirectMembership()
    {

        GroupEntity groupDyreriket = createGroup( "0", "Dyreriket" );
        GroupEntity groupPattedyr = createGroup( "1", "Pattedyr" );
        GroupEntity groupHunder = createGroup( "2", "Hunder" );
        GroupEntity groupKatter = createGroup( "3", "Katter" );
        GroupEntity groupPusekatter = createGroup( "4", "Pusekatter" );

        groupPattedyr.addMembership( groupDyreriket );
        groupHunder.addMembership( groupPattedyr );
        groupKatter.addMembership( groupPattedyr );
        groupPusekatter.addMembership( groupKatter );

        assertFalse( groupHunder.isMemberOf( groupKatter, true ) );
        assertFalse( groupKatter.isMemberOf( groupHunder, true ) );
        assertFalse( groupDyreriket.isMemberOf( groupKatter, true ) );
        assertFalse( groupPusekatter.isMemberOf( groupHunder, true ) );
    }

    @Test
    public void testMemberOfRecursively_EternalLoopPrevented()
    {

        GroupEntity groupDyreriket = createGroup( "0", "Dyreriket" );
        GroupEntity groupPattedyr = createGroup( "1", "Pattedyr" );
        GroupEntity groupKatter = createGroup( "3", "Katter" );
        GroupEntity groupPusekatter = createGroup( "4", "Pusekatter" );

        groupDyreriket.addMembership( groupPattedyr );
        groupPattedyr.addMembership( groupDyreriket );
        groupKatter.addMembership( groupPattedyr );
        groupKatter.addMembership( groupPusekatter );
        groupPusekatter.addMembership( groupKatter );

        assertTrue( groupPusekatter.isMemberOf( groupDyreriket, true ) );
    }

    @Test
    public void testIsAdministratorDirectly()
    {

        GroupEntity group = createGroup( "0", "Admin", GroupType.ADMINS );
        assertTrue( group.isAdministrator() );
    }

    @Test
    public void testIsAdministratorInDirectly()
    {

        GroupEntity adminGroup = createGroup( "0", "Admin", GroupType.ADMINS );
        GroupEntity myGroup = createGroup( "1", "MyGroup", GroupType.GLOBAL_GROUP );
        myGroup.addMembership( adminGroup );

        assertTrue( myGroup.isAdministrator() );
    }

    @Test
    public void testIsUserStoreAdminDirectly()
    {

        UserStoreEntity userStore = createUserStore( 1 );
        GroupEntity group = createGroup( "0", "Admin", GroupType.USERSTORE_ADMINS );
        group.setUserStore( userStore );

        assertTrue( group.isUserstoreAdmin( userStore ) );
    }

    @Test
    public void testIsUserStoreAdminInDirectly()
    {

        UserStoreEntity userStore = createUserStore( 1 );
        GroupEntity userStoreAdminGroup = createGroup( "0", "Admin", GroupType.USERSTORE_ADMINS );
        userStoreAdminGroup.setUserStore( userStore );
        GroupEntity myGroup = createGroup( "1", "MyGroup", GroupType.GLOBAL_GROUP );
        myGroup.addMembership( userStoreAdminGroup );

        assertTrue( myGroup.isUserstoreAdmin( userStore ) );

        myGroup.removeMembership( userStoreAdminGroup );

        assertFalse( myGroup.isUserstoreAdmin( userStore ) );
    }

    @Test
    public void testRemoveMembership()
    {

        GroupEntity groupParent = createGroup( "0", "Parent" );
        GroupEntity groupChild = createGroup( "1", "Child" );

        assertFalse( groupChild.isMemberOf( groupParent, false ) );
        assertFalse( groupParent.hasMember( groupChild ) );

        groupChild.addMembership( groupParent );

        assertTrue( groupChild.isMemberOf( groupParent, false ) );
        assertTrue( groupParent.hasMember( groupChild ) );

        groupChild.removeMembership( groupParent );

        assertFalse( groupChild.isMemberOf( groupParent, false ) );
        assertFalse( groupParent.hasMember( groupChild ) );

    }

    private GroupEntity createGroup( String key, String name )
    {
        return createGroup( key, name, null );
    }

    private GroupEntity createGroup( String key, String name, GroupType type )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( key );
        group.setName( name );
        group.setDeleted( 0 );
        if ( type != null )
        {
            group.setType( type );
        }
        return group;
    }

    private UserStoreEntity createUserStore( int key )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( key ) );
        return userStore;
    }

    private UserEntity createUser( String key, String name )
    {
        UserEntity userEntity = new UserEntity();
        userEntity.setKey( new UserKey( key ) );
        userEntity.setName( name );
        userEntity.setDeleted( 0 );
        userEntity.setType( UserType.NORMAL );
        userEntity.setUserGroup( createGroup( key + "group", name + "group", GroupType.USER ) );
        return userEntity;

    }
}
