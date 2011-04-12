/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Date;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.Address;
import com.enonic.cms.domain.user.UserInfo;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserEntityDaoTest
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
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
        final UserStoreEntity userStore = createUserStore();

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

    private UserStoreEntity createUserStore()
    {
        final UserStoreEntity userStore = new UserStoreEntity();

        userStore.setDefaultStore( false );
        userStore.setDeleted( false );
        userStore.setName( "TestName" );
        userStore.setConnectorName( "TestConnectorName" );

        final String configAsString = "<config><user-fields><first-name required=\"true\"/></user-fields></config>";
        final XMLDocument configXmlDoc = XMLDocumentFactory.create( configAsString );
        final UserStoreConfig config = UserStoreConfigParser.parse(configXmlDoc.getAsJDOMDocument().getRootElement());
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
