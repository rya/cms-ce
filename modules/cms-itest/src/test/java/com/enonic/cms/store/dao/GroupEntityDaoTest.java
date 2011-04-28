/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import javax.inject.Inject;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.AccordionSearchCriteria;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.itest.test.Assertions;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class GroupEntityDaoTest
{
    @Inject
    private GroupDao groupDao;

    @Inject
    private UserDao userDao;

    @Inject
    private UserStoreDao userStoreDao;

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

    @Test
    public void testFindByParameters()
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

        List<GroupEntity> groups = groupDao.findByCriteria( "serGrou", "name", true );

        Assertions.assertCollectionWithOneItem( userGroup, groups );
    }

    @Test
    public void testFindByCriteria()
    {
        final UserStoreEntity userStore1 = createUserStore( "TestName1" );
        userStoreDao.storeNew( userStore1 );
        final UserStoreKey userStoreKey1 = userStore1.getKey();

        final UserStoreEntity userStore2 = createUserStore( "TestName2" );
        userStoreDao.storeNew( userStore2 );
        final UserStoreKey userStoreKey2 = userStore2.getKey();

        userStoreDao.getHibernateTemplate().flush();
        userStoreDao.getHibernateTemplate().clear();

        // Setup of prerequisites
        UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( "email@example.com" );
        user.setDisplayName( "DisplayName" );
        user.setName( "uid" );
        user.setSyncValue( "syncValue" );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );
        user.setUserStore( userStore1 );
        userDao.storeNew( user );

        GroupEntity userGroup1 = new GroupEntity();
        userGroup1.setDeleted( 0 );
        userGroup1.setDescription( null );
        userGroup1.setName( "userGroup" );
        userGroup1.setSyncValue( "sync" );
        userGroup1.setUser( user );
        userGroup1.setType( GroupType.USER );
        userGroup1.setRestricted( false );
        userGroup1.setUserStore( userStore1 );
        groupDao.storeNew( userGroup1 );

        GroupEntity userGroup2 = new GroupEntity();
        userGroup2.setDeleted( 0 );
        userGroup2.setDescription( null );
        userGroup2.setName( "userGroup" );
        userGroup2.setSyncValue( "sync" );
        userGroup2.setUser( user );
        userGroup2.setType( GroupType.USER );
        userGroup2.setRestricted( false );
        userGroup2.setUserStore( userStore2 );
        groupDao.storeNew( userGroup2 );

        GroupEntity userGroup3 = new GroupEntity();
        userGroup3.setDeleted( 0 );
        userGroup3.setDescription( null );
        userGroup3.setName( "PPuserGroup-RR" );
        userGroup3.setSyncValue( "sync" );
        userGroup3.setUser( user );
        userGroup3.setUser( user );
        userGroup3.setType( GroupType.USER );
        userGroup3.setRestricted( false );
        userGroup3.setUserStore( userStore1 );
        groupDao.storeNew( userGroup3 );

        GroupEntity userGroup4 = new GroupEntity();
        userGroup4.setDeleted( 0 );
        userGroup4.setDescription( null );
        userGroup4.setName( "PP-RR" );
        userGroup4.setSyncValue( "sync" );
        userGroup4.setUser( user );
        userGroup4.setUser( user );
        userGroup4.setType( GroupType.USER );
        userGroup4.setRestricted( false );
        userGroup4.setUserStore( userStore1 );
        groupDao.storeNew( userGroup4 );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        AccordionSearchCriteria criteria = new AccordionSearchCriteria();
        criteria.setNameExpression( "userGroup" );
        criteria.appendUserStoreKey( userStoreKey1 );

        List<GroupEntity> groups = groupDao.findByCriteria( criteria );

        Assertions.assertUnorderedArrayListEquals( new GroupEntity[]{userGroup1, userGroup3}, groups );
    }

    private UserStoreEntity createUserStore( String name )
    {
        final UserStoreEntity userStore = new UserStoreEntity();

        userStore.setDefaultStore( false );
        userStore.setDeleted( false );
        userStore.setName( name );
        userStore.setConnectorName( "TestConnectorName" );

        final String configAsString = "<config><user-fields><first-name required=\"true\"/></user-fields></config>";
        final Document configXmlDoc = XMLDocumentFactory.create( configAsString );
        final UserStoreConfig config = UserStoreConfigParser.parse( configXmlDoc.getRootElement() );
        userStore.setConfig( config );
        return userStore;
    }

}