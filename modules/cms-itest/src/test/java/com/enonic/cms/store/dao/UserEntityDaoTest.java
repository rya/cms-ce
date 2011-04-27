/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.AccordionSearchCriteria;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.itest.test.Assertions;

import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.UserInfo;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserEntityDaoTest
{
    @Inject
    private UserDao userDao;

    @Inject
    private UserStoreDao userStoreDao;

    @Inject
    private GroupDao groupDao;


    @Test
    public void testFindBySpecWithUserGroupKey()
    {
        // Setup of prerequisites
        final UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( "email@example.com" );
        user.setDisplayName( "DisplayName" );
        user.setName( "uid" );
        user.setSyncValue( "syncValue" );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );

        userDao.storeNew( user );

        final GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" + user.getKey() );
        userGroup.setSyncValue( user.getSync() );
        userGroup.setUser( user );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( 1 );
        groupDao.storeNew( userGroup );
        user.setUserGroup( userGroup );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserSpecification userSpecification = new UserSpecification();
        userSpecification.setUserGroupKey( userGroup.getGroupKey() );
        final UserEntity storedUser = userDao.findSingleBySpecification( userSpecification );

        // Verify
        assertEquals( user, storedUser );
    }

    @Test
    public void testStoreUserWithUserInfo()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        final UserInfo userInfo = new UserInfo();
        userInfo.setBirthday( new Date( 1976, 4, 19 ) );
        userInfo.setInitials( "JVS" );
        userInfo.setCountry( "Norway" );
        userInfo.setNickName( "Skriu" );
        user.updateUserInfo( userInfo );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );
        final UserInfo storedUserInfo = storedUser.getUserInfo();
        assertEquals( "JVS", storedUserInfo.getInitials() );
        assertEquals( "Norway", storedUserInfo.getCountry() );
        assertEquals( "Skriu", storedUserInfo.getNickName() );
        assertEquals( new Date( 1976, 4, 19 ), storedUserInfo.getBirthday() );
    }

    @Test
    public void testStoreUserWithUserInfo_Address()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        final UserInfo userInfo = new UserInfo();
        userInfo.setInitials( "JVS" );
        userInfo.setCountry( "Norway" );

        final Address homeAddress = new Address();
        homeAddress.setLabel( "My Home address" );
        homeAddress.setStreet( "Street 9" );
        homeAddress.setPostalCode( "0123" );
        homeAddress.setPostalAddress( "MyCity" );
        homeAddress.setRegion( "MyRegion" );
        homeAddress.setCountry( "MyCountry" );

        final Address workAddress = new Address();
        workAddress.setLabel( "My Work address" );
        workAddress.setStreet( "Street 113" );
        workAddress.setPostalCode( "3210" );
        workAddress.setPostalAddress( "WorkCity" );
        workAddress.setRegion( "Work Region" );
        workAddress.setCountry( "MyCountry" );

        userInfo.setAddresses( homeAddress, workAddress );

        user.updateUserInfo( userInfo );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        final UserInfo storedUserInfo = storedUser.getUserInfo();
        assertEquals( "JVS", storedUserInfo.getInitials() );
        assertEquals( "Norway", storedUserInfo.getCountry() );

        final Address storedHomeAddress = storedUserInfo.getAddresses()[0];
        assertNotNull( storedHomeAddress );
        assertEquals( storedHomeAddress, homeAddress );
        assertEquals( "My Home address", storedHomeAddress.getLabel() );
        assertEquals( "Street 9", storedHomeAddress.getStreet() );
        assertEquals( "0123", storedHomeAddress.getPostalCode() );
        assertEquals( "MyCity", storedHomeAddress.getPostalAddress() );
        assertEquals( "MyRegion", storedHomeAddress.getRegion() );
        assertEquals( "MyCountry", storedHomeAddress.getCountry() );

        final Address storedWorkAddress = storedUserInfo.getAddresses()[1];
        assertNotNull( storedWorkAddress );
        assertEquals( storedWorkAddress, workAddress );
        assertEquals( "My Work address", storedWorkAddress.getLabel() );
        assertEquals( "Street 113", storedWorkAddress.getStreet() );
        assertEquals( "3210", storedWorkAddress.getPostalCode() );
        assertEquals( "WorkCity", storedWorkAddress.getPostalAddress() );
        assertEquals( "Work Region", storedWorkAddress.getRegion() );
        assertEquals( "MyCountry", storedWorkAddress.getCountry() );
    }

    @Test
    public void testUpdateUserInfo()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserInfo userInfo = new UserInfo();
        userInfo.setCountry( "Norway" );
        user.updateUserInfo( userInfo );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        UserInfo storedUserInfo = storedUser.getUserInfo();
        assertEquals( "Norway", storedUserInfo.getCountry() );

        // Update
        userInfo = new UserInfo();
        userInfo.setCountry( "South Africa" );
        storedUser.updateUserInfo( userInfo );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        storedUserInfo = storedUser.getUserInfo();
        assertEquals( "South Africa", storedUserInfo.getCountry() );
    }

    @Test
    public void testUpdateUserInfo_Address()
    {
    }


    @Test
    public void testDeleteUserInfo()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserInfo userInfo = new UserInfo();
        userInfo.setInitials( "JVS" );
        userInfo.setCountry( "Norway" );
        user.updateUserInfo( userInfo );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        UserInfo storedUserInfo = storedUser.getUserInfo();
        assertEquals( "JVS", storedUserInfo.getInitials() );
        assertEquals( "Norway", storedUserInfo.getCountry() );

        // Update
        userInfo = new UserInfo();
        userInfo.setInitials( null );
        userInfo.setCountry( "South Africa" );
        storedUser.updateUserInfo( userInfo );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        storedUserInfo = storedUser.getUserInfo();
        Assert.assertNull( storedUserInfo.getInitials() );
        assertEquals( "South Africa", storedUserInfo.getCountry() );
    }

    @Test
    public void testDeleteUserInfo_Address()
    {
    }


    @Test
    public void findByEmailAndUserStore()
    {
        final UserStoreEntity userStore = createUserStore( "TestName" );

        userStoreDao.storeNew( userStore );

        final UserStoreKey userStoreKey = userStore.getKey();

        userStoreDao.getHibernateTemplate().flush();
        userStoreDao.getHibernateTemplate().clear();

        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );
        user.setUserStore( userStore );
        userDao.storeNew( user );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserSpecification userSpecification = new UserSpecification();
        userSpecification.setUserStoreKey( userStoreKey );
        userSpecification.setEmail( "email@example.com" );
        final UserEntity storedUser = userDao.findSingleBySpecification( userSpecification );

        // Verify
        assertNotNull( "storedUser cannot be null", storedUser );
        assertEquals( user, storedUser );
    }

    @Test
    public void testCount()
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
        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        Long actual = userDao.count( UserEntity.class );

        // Verify
        assertEquals( 1L, actual.longValue() );
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

        List<UserEntity> users = userDao.findByCriteria( "splayNa", "name", true );

        Assertions.assertCollectionWithOneItem( user, users );
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
        UserEntity user1 = new UserEntity();
        user1.setDeleted( false );
        user1.setEmail( "email@example.com" );
        user1.setDisplayName( "DisplayName" );
        user1.setName( "uid" );
        user1.setSyncValue( "syncValue" );
        user1.setType( UserType.NORMAL );
        user1.setTimestamp( new DateTime() );
        user1.setUserStore( userStore1 );

        userDao.storeNew( user1 );

        UserEntity user2 = new UserEntity();
        user2.setDeleted( false );
        user2.setEmail( "email@example.com" );
        user2.setDisplayName( "DisplayName" );
        user2.setName( "uid" );
        user2.setSyncValue( "syncValue" );
        user2.setType( UserType.NORMAL );
        user2.setTimestamp( new DateTime() );
        user2.setUserStore( userStore2 );

        userDao.storeNew( user2 );

        UserEntity user3 = new UserEntity();
        user3.setDeleted( false );
        user3.setEmail( "email@example.com" );
        user3.setDisplayName( "DisplayName" );
        user3.setName( "PPuid-RR" );
        user3.setSyncValue( "syncValue" );
        user3.setType( UserType.NORMAL );
        user3.setTimestamp( new DateTime() );
        user3.setUserStore( userStore1 );

        userDao.storeNew( user3 );

        UserEntity user4 = new UserEntity();
        user4.setDeleted( false );
        user4.setEmail( "email@example.com" );
        user4.setDisplayName( "DisplayName" );
        user4.setName( "invalid" );
        user4.setSyncValue( "syncValue" );
        user4.setType( UserType.NORMAL );
        user4.setTimestamp( new DateTime() );
        user4.setUserStore( userStore1 );

        userDao.storeNew( user4 );

        GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" );
        userGroup.setSyncValue( "sync" );
        userGroup.setUser( user1 );
        userGroup.setUser( user2 );
        userGroup.setUser( user3 );
        userGroup.setUser( user4 );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( false );
        groupDao.storeNew( userGroup );

        user1.setUserGroup( userGroup );
        user2.setUserGroup( userGroup );
        user3.setUserGroup( userGroup );
        user4.setUserGroup( userGroup );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        AccordionSearchCriteria criteria = new AccordionSearchCriteria();
        criteria.setNameExpression( "uid" );
        criteria.appendUserStoreKey( userStoreKey1 );

        List<UserEntity> users = userDao.findByCriteria( criteria );

        Assertions.assertUnorderedArrayListEquals( new UserEntity[]{user1, user3}, users );
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
        final UserStoreConfig config = UserStoreConfigParser.parse(configXmlDoc.getRootElement());
        userStore.setConfig( config );
        return userStore;
    }

    private UserEntity createUser( String uid, String displayName, String email, String syncValue )
    {
        final UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( email );
        user.setDisplayName( displayName );
        user.setName( uid );
        user.setSyncValue( syncValue );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );
        return user;
    }
}
