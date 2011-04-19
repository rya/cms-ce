/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;

import javax.inject.Inject;

import org.jdom.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserStoreEntityDaoTest
{
    @Inject
    private UserStoreDao userStoreDao;

    @Test
    public void testStoreUserStore()
    {
        final UserStoreEntity userStore = new UserStoreEntity();

        userStore.setDefaultStore( false );
        userStore.setDeleted( false );
        userStore.setName( "TestName" );
        userStore.setConnectorName( "TestConnectorName" );

        final String configAsString = "<config><user-fields><first-name required=\"true\"/></user-fields></config>";
        final Document configXmlDoc = XMLDocumentFactory._create( configAsString );
        final UserStoreConfig config = UserStoreConfigParser.parse( configXmlDoc.getRootElement() );
        userStore.setConfig( config );

        userStoreDao.storeNew( userStore );

        final UserStoreKey userStoreKey = userStore.getKey();

        userStoreDao.getHibernateTemplate().flush();
        userStoreDao.getHibernateTemplate().clear();

        // Excercise

        final UserStoreEntity storedEntity = userStoreDao.findByKey( userStoreKey );

        // Verify
        assertEquals( userStore, storedEntity );
        assertEquals( storedEntity.getConnectorName(), userStore.getConnectorName() );
        assertEquals( storedEntity.getName(), userStore.getName() );

        final UserStoreConfig userStoreConfig = storedEntity.getConfig();
        assertNotNull( userStoreConfig );

        final Collection<UserStoreUserFieldConfig> userFieldConfigs = userStoreConfig.getUserFieldConfigs();
        assertNotNull( userFieldConfigs );
        assertEquals( 1, userFieldConfigs.size() );
    }
}