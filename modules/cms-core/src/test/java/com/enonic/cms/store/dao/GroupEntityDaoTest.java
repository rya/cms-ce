/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class GroupEntityDaoTest
{
    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;


    @Test
    public void testGroupToUserNavigation()
    {
        // Setup of prerequisites
        UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( "email@example.com" );
        user.setDisplayName( "DisplayName" );
        user.setName( "uid" );
        user.setSyncValue( "syncValue" );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );

        userDao.storeNew( user );

        GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" + user.getKey() );
        userGroup.setSyncValue( user.getSync() );
        userGroup.setUser( user );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( false );
        groupDao.storeNew( userGroup );
        user.setUserGroup( userGroup );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Small verification before the real exercise
        UserSpecification userSpec = new UserSpecification();
        userSpec.setUserGroupKey( userGroup.getGroupKey() );
        UserEntity storedUser = userDao.findSingleBySpecification( userSpec );
        assertEquals( user, storedUser );
        assertEquals( userGroup, storedUser.getUserGroup() );

        userDao.getHibernateTemplate().clear();

        // Excercise: Find usergroup and navigate to the user
        GroupSpecification groupSpecification = new GroupSpecification();
        groupSpecification.setKey( userGroup.getGroupKey() );
        GroupEntity storedGroup = groupDao.findSingleBySpecification( groupSpecification );

        // Verify: the user
        assertEquals( user, storedGroup.getUser() );
    }


}