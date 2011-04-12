/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.access;

import com.enonic.cms.core.content.ContentAccessRightsAccumulated;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.DomainFactory;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.core.business.AbstractPersistContentTest;

import com.enonic.cms.core.content.DomainFixture;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ContentAccessResolverTest
    extends AbstractPersistContentTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupEntityDao groupEntityDao;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Before
    public void before()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );
        fixture.initSystemData();

        groupEntityDao.invalidateCachedKeys();
    }

    @Test
    public void root_user_get_accumulated_all_rights()
    {
        ContentEntity content = new ContentEntity();
        UserEntity rootUser = fixture.findUserByType( UserType.ADMINISTRATOR );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );

        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( rootUser, content );
        assertTrue( accumulated.isReadAccess() );
        assertTrue( accumulated.isUpdateAccess() );
        assertTrue( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_memberOf_enterpriseadminsgroup_get_accumulated_all_rights()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );
        GroupEntity enterpriseAdminsGroup = fixture.findGroupByType( GroupType.ENTERPRISE_ADMINS );
        user.getUserGroup().addMembership( enterpriseAdminsGroup );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );

        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertTrue( accumulated.isUpdateAccess() );
        assertTrue( accumulated.isDeleteAccess() );
    }

    @Test
    public void anonymous_gets_accumulated_right_from_anonymous()
    {
        ContentEntity content = new ContentEntity();
        UserEntity anonymousUser = fixture.findUserByType( UserType.ANONYMOUS );

        content.addContentAccessRight( factory.createContentAccess( "read, update", anonymousUser.getUserGroup(), content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( anonymousUser, content );
        assertTrue( accumulated.isReadAccess() );
        assertTrue( accumulated.isUpdateAccess() );
        assertFalse( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_right_from_anonymous()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        GroupEntity anonymousUsersGroup = fixture.findGroupByType( GroupType.ANONYMOUS );
        content.addContentAccessRight( factory.createContentAccess( "read", anonymousUsersGroup, content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertFalse( accumulated.isUpdateAccess() );
        assertFalse( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_right_from_usergroup()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        content.addContentAccessRight( factory.createContentAccess( "read", user.getUserGroup(), content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertFalse( accumulated.isUpdateAccess() );
        assertFalse( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_right_from_autenticated_users_group()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        GroupEntity authenticatedUsersGroup = fixture.findGroupByTypeAndUserstore( GroupType.AUTHENTICATED_USERS, "testuserstore" );
        content.addContentAccessRight( factory.createContentAccess( "read", authenticatedUsersGroup, content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertFalse( accumulated.isUpdateAccess() );
        assertFalse( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_right_from_indirect_membership()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        GroupEntity group1 = factory.createGlobalGroup( "Group-1" );
        user.getUserGroup().addMembership( group1 );
        fixture.save( group1 );

        GroupEntity group1_group2 = factory.createGlobalGroup( "Group-1-2" );
        group1.addMembership( group1_group2 );
        fixture.save( group1_group2 );

        GroupEntity group1_group2_group3 = factory.createGlobalGroup( "Group-1-2-3" );
        group1_group2.addMembership( group1_group2_group3 );
        fixture.save( group1_group2_group3 );

        content.addContentAccessRight( factory.createContentAccess( "read", group1_group2_group3, content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertFalse( accumulated.isUpdateAccess() );
        assertFalse( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_rights_from_different_indirect_memberships()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        GroupEntity group1 = factory.createGlobalGroup( "Group-1" );
        user.getUserGroup().addMembership( group1 );
        fixture.save( group1 );

        GroupEntity group1_group2 = factory.createGlobalGroup( "Group-1-2" );
        group1.addMembership( group1_group2 );
        fixture.save( group1_group2 );

        GroupEntity group1_group2_group3 = factory.createGlobalGroup( "Group-1-2-3" );
        group1_group2.addMembership( group1_group2_group3 );
        fixture.save( group1_group2_group3 );

        content.addContentAccessRight( factory.createContentAccess( "read", group1, content ) );
        content.addContentAccessRight( factory.createContentAccess( "update", group1_group2, content ) );
        content.addContentAccessRight( factory.createContentAccess( "delete", group1_group2_group3, content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertTrue( accumulated.isUpdateAccess() );
        assertTrue( accumulated.isDeleteAccess() );
    }

    @Test
    public void user_gets_accumulated_rights_from_indirect_memberships_in_different_branches()
    {
        ContentEntity content = new ContentEntity();
        UserEntity user = fixture.createAndStoreNormalUserWithUserGroup( "myuser", "My User", "testuserstore" );

        GroupEntity group1 = factory.createGlobalGroup( "Group-1" );
        fixture.save( group1 );
        user.getUserGroup().addMembership( group1 );

        GroupEntity group1_group2a = factory.createGlobalGroup( "Group-1-2a" );
        fixture.save( group1_group2a );
        group1.addMembership( group1_group2a );

        GroupEntity group1_group2b = factory.createGlobalGroup( "Group-1-2b" );
        fixture.save( group1_group2b );
        group1.addMembership( group1_group2b );

        GroupEntity group1_group2a_group3 = factory.createGlobalGroup( "Group-1-2a-3" );
        fixture.save( group1_group2a_group3 );
        group1_group2a.addMembership( group1_group2a_group3 );

        GroupEntity group1_group2b_group3 = factory.createGlobalGroup( "Group-1-2b-3" );
        fixture.save( group1_group2b_group3 );
        group1_group2b.addMembership( group1_group2b_group3 );

        fixture.flushAndClearHibernateSesssion();

        content.addContentAccessRight( factory.createContentAccess( "read", user.getUserGroup(), content ) );
        content.addContentAccessRight( factory.createContentAccess( "update", group1_group2a_group3, content ) );
        content.addContentAccessRight( factory.createContentAccess( "delete", group1_group2b_group3, content ) );

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupEntityDao );
        ContentAccessRightsAccumulated accumulated = contentAccessResolver.getAccumulatedAccessRights( user, content );
        assertTrue( accumulated.isReadAccess() );
        assertTrue( accumulated.isUpdateAccess() );
        assertTrue( accumulated.isDeleteAccess() );
    }


}
